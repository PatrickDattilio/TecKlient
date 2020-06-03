package com.dattilio.klient.client

import com.dattilio.klient.client.widget.Controls
import com.sun.javafx.tk.Toolkit
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.InlineCssTextArea
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

class View @Inject constructor(private val controls: Controls) : CoroutineScope {

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

    val key = Object()
    private val usernameHandler: EventHandler<KeyEvent> = EventHandler { event ->
        if (event.code == KeyCode.ENTER) {
            event.consume()
            Toolkit.getToolkit().exitNestedEventLoop(key, null)
        }
    }
    private val passwordHandler: EventHandler<KeyEvent> = EventHandler { event ->
        if (event.code == KeyCode.ENTER) {
            event.consume()
            Toolkit.getToolkit().exitNestedEventLoop(key, null)
        }
    }
    fun getLoginCredentials(): Login.Credentials {
        addText("Please enter your username:")
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, usernameHandler)
        Toolkit.getToolkit().enterNestedEventLoop(key)
        val username = getUsername()
        Toolkit.getToolkit().enterNestedEventLoop(key)
        val password = getPassword()
        return Login.Credentials(username, password)
    }

    private fun getUsername(): String {
        textArea.removeEventFilter(KeyEvent.KEY_PRESSED, usernameHandler)
        val user = textArea.text
        textArea.clear()
        addText("Please enter your password:")
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, passwordHandler)
        return user
    }

    private fun getPassword(): String {
        textArea.removeEventFilter(KeyEvent.KEY_PRESSED, passwordHandler)
        val pass = textArea.text
        textArea.clear()
        return pass

    }

    fun failedLogin() {
        addText("I'm sorry, that username or password was incorrect. Please try again.")
        getLoginCredentials()
    }

}