import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.InlineCssTextArea

class View {

    val gameScreen = InlineCssTextArea()
    val textArea = TextArea()

    val scrollPane = VirtualizedScrollPane<InlineCssTextArea>(gameScreen)
    fun setupUI(primaryStage: Stage?) {
        val root = GridPane()
        val scene = Scene(root, 900.0, 900.0)

        gameScreen.isEditable = false
        gameScreen.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE)
        gameScreen.isWrapText = true
        gameScreen.useInitialStyleForInsertion = true

        textArea.isWrapText = true
        textArea.prefRowCount = 2
        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED

        root.add(scrollPane, 0, 0, 4, 4)
        root.add(textArea, 0, 5, 4, 1)

        primaryStage?.title = "Hello World!"
        primaryStage?.setScene(scene)
        primaryStage?.show()
    }


    fun addText(text: String) {
        gameScreen.appendText(text + "\n")
        gameScreen.requestFollowCaret()
    }

    fun addTextWithStyle(textAndStyleList: ArrayList<TecTextParser.TextAndStyle>) {
        for (textAndStyle in textAndStyleList) {
            gameScreen.appendText(textAndStyle.text)
            if (textAndStyle.style != null) {
                gameScreen.setStyle(gameScreen.length - textAndStyle.text.length, gameScreen.length,textAndStyle.style)
            }
        }

        gameScreen.appendText("\n")
    }


}