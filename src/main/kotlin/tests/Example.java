package tests;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.ArrayList;
import java.util.List;

public class Example extends Application {

    public static void main(String args[]) {
        launch(Example.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane root = new GridPane();
        Scene scene = new Scene(root, 900.0, 900.0);
        InlineCssTextArea area = new InlineCssTextArea();
        area.setEditable(false);
        area.setPrefSize(900.0, 900.0);
        area.setWrapText(true);
        root.add(area, 0, 5, 4, 1);
        primaryStage.setTitle("tests.Example");
        primaryStage.setScene(scene);
        primaryStage.show();
//        ScenicView.show(scene);

        area.appendText("Test");

        area.setParagraphStyle(area.getCurrentParagraph(), "-fx-text-alignment: center;");
        area.setStyle(area.getCurrentParagraph(), "-fx-text-alignment: center;");
        area.appendText("Test 2\n");


        // Compare the layout widths of two strings. One string is composed
        // of "thin" characters, the other of "wide" characters. In mono-spaced
        // fonts the widths should be the same.

        final Text thinTxt = new Text("1 l"); // note the space
        final Text thikTxt = new Text("MWX");

        List<String> fontFamilyList = Font.getFamilies();
        List<String> monoFamilyList = new ArrayList<>();

        Font font;

        for (String fontFamilyName : fontFamilyList) {
            font = Font.font(fontFamilyName, FontWeight.NORMAL, FontPosture.REGULAR, 14.0d);
            thinTxt.setFont(font);
            thikTxt.setFont(font);
            if (thinTxt.getLayoutBounds().getWidth() == thikTxt.getLayoutBounds().getWidth()) {
                monoFamilyList.add(fontFamilyName);
            }
        }

        FXCollections.observableArrayList(monoFamilyList);
    }
}
