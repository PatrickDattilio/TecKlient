package com.dattilio.klient

import com.dattilio.klient.api.SendCommand
import com.dattilio.klient.widget.Compass
import com.dattilio.klient.widget.Controls
import com.dattilio.klient.widget.Macros
import com.dattilio.klient.widget.Map
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Module
class AppModule(val app: App) {

//    @Provides
//    fun providesAppComponent(): AppComponent {
//        return app.getAppComponent()
//    }

    @Provides
    fun providesSendCommand(): SendCommand {
        return SendCommand()
    }

    @Provides
    fun providesCompass(sendCommand: SendCommand): Compass {
        return Compass(sendCommand)
    }

    @Provides
    fun providesMap(): Map {
        return Map()
    }

    @Provides
    fun providesMacros(sendCommand: SendCommand): Macros {
        return Macros(sendCommand)
    }

    @Provides
    fun providesControls(map: Map, compass: Compass, macros: Macros): Controls {
        return Controls(map, compass, macros)
    }

    @Provides
    fun providesView(controls: Controls): View {
        return View(controls)
    }

    @Provides
    fun providesLogger(): Logger {
        return LogManager.getLogger()
    }

    @Provides
    fun providesOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
                .followRedirects(false)
                .build()
    }

    @Provides
    fun providesClient(sendCommand: SendCommand,
                       logger: Logger,
                       okHttp: OkHttpClient,
                       controls: Controls,
                       view: View,
                       appComponent: AppComponent): TecClient {
        return TecClient(sendCommand, logger, okHttp, controls, view, appComponent)
    }
}