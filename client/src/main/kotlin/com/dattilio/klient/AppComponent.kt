package com.dattilio.klient

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent{
    fun getTecClient(): TecClient
    fun getView(): View
    fun inject(app: App)
}