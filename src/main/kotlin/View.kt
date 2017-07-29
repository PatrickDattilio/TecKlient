import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.InlineCssTextArea
import org.scenicview.ScenicView

class View(send: (String) -> Unit) {

    private val logger = LogManager.getLogger()
    val send = send
    val gameScreen = InlineCssTextArea()
    val textArea = TextArea()
    val mapCanvas = Canvas(120.0, 120.0)
    val map = mapCanvas.graphicsContext2D

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


        val controls = VBox(setupMacros(),
                mapCanvas)
//        ,
//                setupCompass(),
//                setupStatus())


        root.add(menuBar, 0, 0, 4, 1)
        root.add(scrollPane, 0, 1, 4, 4)
        root.add(textArea, 0, 5, 4, 1)
        root.add(controls, 5, 0, 1, 4)

        primaryStage?.title = "Hello World!"
        primaryStage?.scene = scene
        primaryStage?.show()
        ScenicView.show(scene)
    }

    private fun updateMap(skoot: String) {

    }

    private fun setupMacros(): GridPane {
        val macros = GridPane()
        fun Button(text: String, command: String): Button {
            val button = Button(text)
            button.setOnAction { send(command) }
            return button
        }
        macros.add(Button("I", "fe1"), 0, 0)
        macros.add(Button("II", "fe2"), 1, 0)
        macros.add(Button("III", "fe3"), 2, 0)
        macros.add(Button("IV", "fe4"), 3, 0)
        macros.add(Button("V", "fe5"), 4, 0)

        macros.add(Button("VI", "fe6"), 0, 1)
        macros.add(Button("VII", "fe7"), 1, 1)
        macros.add(Button("VIII", "fe8"), 2, 1)
        macros.add(Button("IX", "fe9"), 3, 1)
        macros.add(Button("X", "fe10"), 4, 1)

        macros.add(Button("XI", "fe11"), 0, 2)
        macros.add(Button("XII", "fe12"), 1, 2)
        macros.add(Button("XIII", "fe13"), 2, 2)
        macros.add(Button("XIV", "fe14"), 3, 2)
        macros.add(Button("XV", "fe15"), 4, 2)


        return macros
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