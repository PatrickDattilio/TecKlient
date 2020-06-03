package com.dattilio.klient.client

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.util.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference


class JavaFxScheduler  /* package for unit test */
internal constructor() : Scheduler() {
    override fun createWorker(): Worker {
        return JavaFxWorker()
    }

    /**
     * A Worker implementation which manages a queue of QueuedRunnable for execution on the Java FX Application thread
     * For a simpler implementation the queue always contains at least one element.
     * [.head] is the element, which is in execution or was last executed
     * [.tail] is an atomic reference to the last element in the queue, or null when the worker was disposed
     * Recursive actions are not preferred and inserted at the tail of the queue as any other action would be
     * The Worker will only schedule a single job with [Platform.runLater] for when the queue was previously empty
     */
    private class JavaFxWorker : Worker(), Runnable {
        @Volatile
        private var head =
            QueuedRunnable(null) /// only advanced in run(), initialised with a starter element
        private val tail = AtomicReference(head) /// points to the last element, null when disposed

        private class QueuedRunnable(@field:Volatile private var action: Runnable?) : AtomicReference<QueuedRunnable?>(), Disposable, Runnable {

            override fun dispose() {
                action = null
            }

            override fun isDisposed(): Boolean {
              return   action == null
            }

            override fun run() {
                val action = action
                action?.run()
                this.action = null
            }

        }

        override fun dispose() {
            tail.set(null)
            var qr: QueuedRunnable? = head
            while (qr != null) {
                qr.dispose()
                qr = qr.getAndSet(null)
            }
        }

        override fun isDisposed(): Boolean {
           return  tail.get() == null
        }

        override fun schedule(action: Runnable?, delayTime: Long, unit: TimeUnit): Disposable {
            val delay = 0.coerceAtLeast(unit.toMillis(delayTime).toInt()).toLong()
            assertThatTheDelayIsValidForTheJavaFxTimer(
                delay
            )
            val queuedRunnable =
                QueuedRunnable(action)
            if (delay == 0L) { // delay is too small for the java fx timer, schedule it without delay
                return schedule(queuedRunnable)
            }
            val timer = Timeline(KeyFrame(Duration.millis(delay.toDouble()), EventHandler { schedule(queuedRunnable) }))
            timer.play()
            return Disposable.fromRunnable {
                queuedRunnable.dispose()
                timer.stop()
            }
        }

        override fun schedule(action: Runnable?): Disposable {
            if (isDisposed) {
                return Disposable.disposed()
            }
            val queuedRunnable = if (action is QueuedRunnable) action else QueuedRunnable(
                action
            )
            var tailPivot: QueuedRunnable?
            do {
                tailPivot = tail.get()
            } while (tailPivot != null && !tailPivot.compareAndSet(null, queuedRunnable))
            if (tailPivot == null) {
                queuedRunnable.dispose()
            } else {
                tail.compareAndSet(tailPivot, queuedRunnable) // can only fail with a concurrent dispose and we don't want to override the disposed value
                if (tailPivot === head) {
                    if (Platform.isFxApplicationThread()) {
                        run()
                    } else {
                        Platform.runLater(this)
                    }
                }
            }
            return queuedRunnable
        }

        override fun run() {
            var qr = head.get()
            while (qr != null) {
                qr.run()
                head = qr
                qr = qr.get()
            }
        }
    }

    companion object {
        private val INSTANCE = JavaFxScheduler()
        fun platform(): JavaFxScheduler {
            return INSTANCE
        }

        private fun assertThatTheDelayIsValidForTheJavaFxTimer(delay: Long) {
            require(!(delay < 0 || delay > Int.MAX_VALUE)) { String.format("The JavaFx timer only accepts non-negative delays up to %d milliseconds.", Int.MAX_VALUE) }
        }
    }
}