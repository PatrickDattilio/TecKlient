import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.InlineCssTextArea
import widget.Controls

class View(val controls: Controls) {

    private val logger = LogManager.getLogger()
    val gameScreen = InlineCssTextArea()
    val textArea = TextArea()

    val scrollPane = VirtualizedScrollPane<InlineCssTextArea>(gameScreen)
    fun setupUI(primaryStage: Stage?) {
        val root = GridPane()
        val scene = Scene(root, 900.0, 900.0)
        textArea.style = "-fx-font-family: consolas"

        gameScreen.isEditable = false
        gameScreen.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE)
        gameScreen.isWrapText = true
        gameScreen.useInitialStyleForInsertion = true
        gameScreen.style = "-fx-font-family: consolas"

        textArea.isWrapText = true
        textArea.prefRowCount = 2

        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED

        val menuBar = MenuBar()
        val file = Menu("File")
        val exit = MenuItem("Exit")
        exit.setOnAction { System.exit(0) }
        file.items.add(exit)
        menuBar.menus.add(file)


        root.add(menuBar, 0, 0, 4, 1)
        root.add(scrollPane, 0, 1, 4, 4)
        root.add(textArea, 0, 5, 4, 1)
        root.add(controls, 5, 0, 1, 4)

        primaryStage?.title = "Hello World!"
        primaryStage?.scene = scene
        primaryStage?.show()
        //ScenicView.show(scene)
    }

    fun addText(text: String) {
        gameScreen.appendText(text + "\n")
        gameScreen.requestFollowCaret()
    }

    fun addTextWithStyle(textAndStyleList: ArrayList<TecTextParser.TextAndStyle>) {
        if (textAndStyleList.size > 0) {
            for (textAndStyle in textAndStyleList) {
                gameScreen.appendText(textAndStyle.text)
                if (textAndStyle.style.size > 0) {
                    val styleBuilder = StringBuilder()
                    for (style in textAndStyle.style) {
                        styleBuilder.append(style)
                    }
                    if (textAndStyle.alignment != null) {
                        gameScreen.setStyle(gameScreen.currentParagraph, textAndStyle.alignment)
                        gameScreen.setParagraphStyle(gameScreen.currentParagraph, "-fx-text-alignment: center;")
                    }
                    gameScreen.setStyle(gameScreen.length - textAndStyle.text.length, gameScreen.length, styleBuilder.toString())
                }
            }
            gameScreen.appendText("\n")
            gameScreen.requestFollowCaret()
        }
    }


}