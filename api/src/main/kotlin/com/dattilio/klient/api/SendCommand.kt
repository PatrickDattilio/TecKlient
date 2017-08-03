package com.dattilio.klient.api

import io.reactivex.subjects.BehaviorSubject

//Handles telling various parts of the application commands that are to be sent.

class SendCommand {
    val commands: BehaviorSubject<String> = BehaviorSubject.create<String>()

    fun send(command: String) {
        commands.onNext(command)
    }
}