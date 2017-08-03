package com.dattilio.klient

import dagger.Component
import ro.fortsoft.pf4j.Plugin
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent{
    fun getTecClient():TecClient
    fun getView():View
    fun inject(plugin:Plugin)
}