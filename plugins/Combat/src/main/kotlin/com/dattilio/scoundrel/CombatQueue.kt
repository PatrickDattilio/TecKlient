package com.dattilio.scoundrel

import java.util.*

class CombatQueue(val presenter: CombatPreProcessor) : PriorityQueue<Action>() {

    fun addAction(action: Action) {
        if (action !in this) {
            add(action)
            presenter.updateActionQueueText()
        }
    }

    fun removeAction(action: Action) {
        if (remove(action)) {
            presenter.updateActionQueueText()
        }
    }

}