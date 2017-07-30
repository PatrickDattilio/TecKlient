import ro.fortsoft.pf4j.DefaultPluginManager
import ro.fortsoft.pf4j.Plugin
import ro.fortsoft.pf4j.PluginState

class Api {

    fun preProcessLine(line: String): String {
        //React to a line BEFORE it has been displayed, potentially intercepting and
        // preventing the display.
        return line
    }

    fun postProcessLine(line: String) {
        //React to a line that has been displayed.
    }

    val statusPlugins: HashMap<Status, ArrayList<Plugin>> = HashMap()

    fun statusUpdate(status: Status) {
        var pluginManager = DefaultPluginManager()
        for (plugin in statusPlugins[status]!!) {
            if (plugin.wrapper.pluginState == PluginState.STARTED) {

            }
        }
    }
}

enum class Status { HEALTH, FATIGUE, ENCUMBRANCE, SATIATION }
