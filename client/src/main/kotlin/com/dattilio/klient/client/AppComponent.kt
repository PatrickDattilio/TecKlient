package com.dattilio.klient.client

import com.dattilio.klient.client.App
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent{
    fun getTecClient(): TecClient
    fun getView(): View
    fun inject(app: App)
}