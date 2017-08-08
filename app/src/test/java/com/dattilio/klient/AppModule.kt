package com.dattilio.klient

import com.dattilio.klient.api.SendCommand
import com.dattilio.klient.widget.*
import com.dattilio.klient.widget.Map
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import javax.inject.Singleton

@Singleton
@Module
class AppModule(val app: App) {

//    @Provides
//    fun providesAppComponent(): AppComponent {
//        return app.getAppComponent()
//    }

    @Singleton
    @Provides
    fun providesSendCommand(): SendCommand {
        return SendCommand()
    }
}