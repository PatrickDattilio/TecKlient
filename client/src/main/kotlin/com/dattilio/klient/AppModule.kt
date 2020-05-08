package com.dattilio.klient

import com.dattilio.klient.api.SendCommand
import com.dattilio.klient.widget.*
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class AppModule(val app: App) {

//    @Provides
//    fun providesAppComponent(): com.dattilio.klient.AppComponent {
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
    fun providesMap(): MapWidget {
        return MapWidget()
    }

    @Singleton
    @Provides
    fun providesMacros(sendCommand: SendCommand): Macros {
        return Macros(sendCommand)
    }

    @Singleton
    @Provides
    fun providesStatus(sendCommand: SendCommand): StatusWidget {
        return StatusWidget(sendCommand)
    }

    @Singleton
    @Provides
    fun providesControls(map: MapWidget, compass: Compass, macros: Macros, status: StatusWidget): Controls {
        return Controls(map, compass, macros, status)
    }

    @Singleton
    @Provides
    fun providesView(controls: Controls): View {
        return View(controls)
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
    fun providesClient(
        sendCommand: SendCommand,
        pluginManager: PluginManager,
        okHttp: OkHttpClient,
        controls: Controls,
        view: View
    ): TecClient {
        return TecClient(sendCommand, pluginManager, okHttp, Login(okHttp),controls, view)
    }
}