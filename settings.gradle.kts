rootProject.buildFileName = "build.gradle.kts"
rootProject.name = "Klient"
include("client")
include("api")
include("plugins")
include("plugins:combat")
include("plugins:courses")
//include("plugins:hunting")
include("plugins:map")
include("headless")
include("headless:courses")
include("headless:combat")
include("headless:locksmithing")
include("headless:client")
