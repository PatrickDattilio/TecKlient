package widget

import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape

class Compass(width: Double, height: Double, val sendCommand: (String) -> Unit) : Pane() {
    val compass = HashMap<String, Shape>()
    val size = 36.0

    val nw = Rectangle(2.0, 2.0, size, size, "nw")
    val n = Rectangle(40.0, 2.0, size, size, "n")
    val ne = Rectangle(78.0, 2.0, size, size, "ne")

    val w = Rectangle(2.0, 40.0, size, size, "w")
    val u = Polygon(40.0, 40.0,
            40.0 + size, 40.0,
            40.0, 40.0 + size)
    val d = Polygon(40.0, 40.0 + size,
            40.0 + size, 40.0 + size,
            40.0 + size, 40.0)
    val e = Rectangle(78.0, 40.0, size, size, "e")


    val sw = Rectangle(2.0, 78.0, size, size, "sw")
    val s = Rectangle(40.0, 78.0, size, size, "s")
    val se = Rectangle(78.0, 78.0, size, size, "se")


    private fun Rectangle(x: Double, y: Double, width: Double, height: Double, command: String): Rectangle {
        val rect = Rectangle(x, y, width, height)
        rect.setOnMouseClicked { sendCommand(command) }
        compass.put(command, rect)
        return rect
    }

    init {
        this.height = height
        this.width = width

        background = Background(BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY))

        u.setOnMouseClicked { sendCommand("u") }
        compass.put("u", u)

        d.setOnMouseClicked { sendCommand("d") }
        compass.put("d", d)
        children.addAll(nw, n, ne, w, u, d, e, sw, s, se)

    }

    fun updateCompass(skoot: String) {
        val compassUpdates = Regex("\\W+").split(skoot)
        for (i in (0..compassUpdates.size - 1) step 2) {
            val fill = if (compassUpdates[i + 1] == "show") Color.WHITE else Color.DARKGREY
            compass.get(compassUpdates[i])?.fill = fill
        }
    }
}