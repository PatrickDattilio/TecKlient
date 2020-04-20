package com.dattilio.klient

import com.dattilio.klient.TecClient
import com.dattilio.klient.TecTextParser
import com.dattilio.klient.widget.Controls
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
//import org.apache.logging.log4j.LogManager
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.InlineCssTextArea
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

class View @Inject constructor(val controls: Controls) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx

    //    private val logger = LogManager.getLogger()
    val gameScreen = InlineCssTextArea()
    val textArea = TextArea()
    private val reconnect = MenuItem("Reconnect")
    private val disconnect = MenuItem("Disconnect")
    private val scrollPane = VirtualizedScrollPane(gameScreen)

    @ExperimentalCoroutinesApi
    fun setupUI(primaryStage: Stage?, presenter: TecClient) {
        val root = GridPane()
        val scene = Scene(root, 900.0, 900.0)
        textArea.style = "-fx-font-family: consolas"

        gameScreen.isEditable = false
        gameScreen.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE)
        gameScreen.isWrapText = true
        gameScreen.useInitialStyleForInsertion = true
        gameScreen.style = "-fx-font-family: consolas"
        gameScreen.background= Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))


        textArea.isWrapText = true
        textArea.prefRowCount = 2

        scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED

        val menuBar = MenuBar()
        val file = Menu("File")
        val exit = MenuItem("Exit")
        exit.setOnAction { exitProcess(0) }
        reconnect.isVisible = false
        reconnect.setOnAction { presenter.reconnect() }
        disconnect.isVisible = false
        disconnect.setOnAction { presenter.disconnect() }
        file.items.addAll(reconnect, disconnect, exit)
        menuBar.menus.add(file)


        root.add(menuBar, 0, 0, 4, 1)
        root.add(scrollPane, 0, 1, 4, 4)
        root.add(textArea, 0, 5, 4, 1)
        root.add(controls, 5, 0, 1, 4)

        primaryStage?.title = "TecKlient"
        primaryStage?.scene = scene
        primaryStage?.show()
    }

    fun addText(text: String) {
        launch {
            gameScreen.appendText(text + "\n")
            gameScreen.requestFollowCaret()
        }
    }

    fun addTextWithStyle(textAndStyleList: ArrayList<TecTextParser.TextAndStyle>) {
        launch {
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
                        gameScreen.setStyle(
                            gameScreen.length - textAndStyle.text.length,
                            gameScreen.length,
                            styleBuilder.toString()
                        )
                    }
                }
                gameScreen.appendText("\n")
                gameScreen.requestFollowCaret()
            }
        }
    }

    fun isConnected(connected: Boolean) {
        reconnect.isVisible = !connected
        disconnect.isVisible = connected
    }
}