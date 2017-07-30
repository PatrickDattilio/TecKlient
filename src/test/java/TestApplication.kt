import javafx.application.Application
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import ro.fortsoft.pf4j.DefaultPluginManager
import ro.fortsoft.pf4j.RuntimeMode
import widget.Controls
import java.io.File

class TestApplication : Application() {
    private val logger = LogManager.getLogger()
    val controls: Controls = Controls {
        text: String ->
        System.out.println(text)
    }
    val view = View(controls)
    val api = Api()
    val parser = TecTextParser(controls, api)
    val pluginManager = MyDefaultPluginManager()

    class MyDefaultPluginManager : DefaultPluginManager() {
        override fun getRuntimeMode(): RuntimeMode {
            return RuntimeMode.DEVELOPMENT
        }
    }

    override fun start(primaryStage: Stage?) {
        pluginManager.loadPlugins()
        pluginManager.startPlugins()

        logger.info("Plugindirectory: ");
        logger.info("\t" + System.getProperty("pf4j.pluginsDir", "plugins") + "\n")
        view.setupUI(primaryStage)
        val classLoader = javaClass.classLoader
        val greetings = pluginManager.getExtensions(api.LinePreprocessor::class.java)
        val file = File(classLoader.getResource("skootTestData")!!.file)
        file.forEachLine { parser.parseLine(it) }
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(TestApplication::class.java)
        }
    }

}