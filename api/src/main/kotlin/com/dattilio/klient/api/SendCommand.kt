package com.dattilio.klient.api

import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject


//Handles telling various parts of the application commands that are to be sent.

class SendCommand @Inject constructor(){
    val commands: BehaviorSubject<String> = BehaviorSubject.create<String>()

    fun send(command: String) {
        commands.onNext(command)
    }
}