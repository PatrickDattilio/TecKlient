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

    @Singleton
    @Provides
    fun providesCompass(sendCommand: SendCommand): Compass {
        return Compass(sendCommand)
    }

    @Singleton
    @Provides
    fun providesMap(): Map {
        return Map()
    }

    @Singleton
    @Provides
    fun providesMacros(sendCommand: SendCommand): Macros {
        return Macros(sendCommand)
    }

    @Singleton
    @Provides
    fun providesStatus(sendCommand: SendCommand): Status {
        return Status(sendCommand)
    }

    @Singleton
    @Provides
    fun providesControls(map: Map, compass: Compass, macros: Macros, status: Status): Controls {
        return Controls(map, compass, macros, status)
    }

    @Singleton
    @Provides
    fun providesView(controls: Controls): View {
        return View(controls)
    }

    @Singleton
    @Provides
    fun providesLogger(): Logger {
        return LogManager.getLogger()
    }

    @Singleton
    @Provides
    fun providesOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
                .followRedirects(false)
                .build()
    }

    @Singleton
    @Provides
    fun providesClient(sendCommand: SendCommand,
                       logger: Logger,
                       okHttp: OkHttpClient,
                       controls: Controls,
                       view: View): TecClient {
        return TecClient(sendCommand, logger, okHttp, controls, view)
    }
}