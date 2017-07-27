import org.junit.Before;
import org.junit.Test;

public class TecTextParserTest {


    private TecTextParser parser;
    String sample = "You are wearing:\n"+
            "<ul><li>some dark linen breeches\n"+
            "<li>a lorica hamata\n"+
            "<li>a leather belt with iron studs\n"+
            "<li>a small leather sheath\n"+
            "<ul><li>a boison dagger\n"+
            "</ul><li>a black homespun wool paenula edged with a row of blue crossed daggers\n"+
            "<li>some leather thigh greaves\n"+
            "<li>a leather arming jerkin\n"+
            "<li>some leather shoulder pteryges\n"+
            "<li>a large red woolen sack\n"+
            "<ul><li>a piece of parchment labeled 'Cruentus Laureola Recruitment'\n"+
            "<li>a large sack\n"+
            "<li>a large sack\n"+
            "<li>a large sack\n"+
            "<li>a large sack\n"+
            "<li>a stuffed toy esecarnus\n"+
            "<li>a stuffed toy goose\n"+
            "<li>a large sack\n"+
            "<li>a waterskin\n"+
            "<li>a blue ceramic lantern\n"+
            "<li>some coins\n"+
            "<li>a piece of raw squirrel meat\n"+
            "<li>a piece of raw squirrel meat\n"+
            "<li>a piece of raw squirrel meat\n"+
            "<li>some cordage\n"+
            "<li>some small dry pieces of deadwood\n"+
            "<li>a piece of raw squirrel meat\n"+
            "<li>a burnt piece of squirrel meat\n"+
            "<li>a tin dagger\n"+
            "<li>a torch\n"+
            "<li>some coins\n"+
            "</ul><li>some heavy hobnailed soldiers boots\n"+
            "<li>a bronze visored helmet\n"+
            "<li>a bronze greave\n"+
            "<li>a bronze greave\n"+
            "<li>some torn leather gloves with tarnished iron studs at the knuckles\n"+
            "</ul>";

    String fontSample = " </font>You are facing south. You see </font><font color=\"#646464\">Quiet Room A</font> to the </font><font color=\"#646464\">north</font>; a </font><font color=\"#646464\">walkway</font> to the </font><font color=\"#646464\">east</font> and to the </font><font color=\"#646464\">west</font>; </font><font color=\"#646464\">Quiet Room B</font> to the </font><font color=\"#646464\">south</font>; and an </font><font color=\"#646464\">obsidian staircase</font> leading </font><font color=\"#646464\">downwards</font>.</font>";
    @Before
    public void setup(){
        parser = new TecTextParser();
    }

    @Test
    public void testFonts(){
        parser.parseLine(fontSample);
    }

    @Test
    public void howDoesStylinWork(){

    }

}
