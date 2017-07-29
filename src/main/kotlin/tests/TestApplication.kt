package tests

import TecTextParser
import View
import javafx.application.Application
import javafx.stage.Stage

class TestApplication : Application() {

    var sample = "You are wearing:\n" +
            "<ul><li>some dark linen breeches\n" +
            "<li>a lorica hamata\n" +
            "<li>a leather belt with iron studs\n" +
            "<li>a small leather sheath\n" +
            "<ul><li>a boison dagger\n" +
            "</ul><li>a black homespun wool paenula edged with a row of blue crossed daggers\n" +
            "<li>some leather thigh greaves\n" +
            "<li>a leather arming jerkin\n" +
            "<li>some leather shoulder pteryges\n" +
            "<li>a large red woolen sack\n" +
            "<ul><li>a piece of parchment labeled 'Cruentus Laureola Recruitment'\n" +
            "<li>a large sack\n" +
            "<li>a large sack\n" +
            "<li>a large sack\n" +
            "<li>a large sack\n" +
            "<li>a stuffed toy esecarnus\n" +
            "<li>a stuffed toy goose\n" +
            "<li>a large sack\n" +
            "<li>a waterskin\n" +
            "<li>a blue ceramic lantern\n" +
            "<li>some coins\n" +
            "<li>a piece of raw squirrel meat\n" +
            "<li>a piece of raw squirrel meat\n" +
            "<li>a piece of raw squirrel meat\n" +
            "<li>some cordage\n" +
            "<li>some small dry pieces of deadwood\n" +
            "<li>a piece of raw squirrel meat\n" +
            "<li>a burnt piece of squirrel meat\n" +
            "<li>a tin dagger\n" +
            "<li>a torch\n" +
            "<li>some coins\n" +
            "</ul><li>some heavy hobnailed soldiers boots\n" +
            "<li>a bronze visored helmet\n" +
            "<li>a bronze greave\n" +
            "<li>a bronze greave\n" +
            "<li>some torn leather gloves with tarnished iron studs at the knuckles\n" +
            "</ul>"


    val view = View({ text -> System.out.println(text) })
    val parser = TecTextParser()
    override fun start(primaryStage: Stage?) {
        view.setupUI(primaryStage)

        view.addTextWithStyle(parser.parseLine(test3))
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(TestApplication::class.java)
        }
    }

    var centerTest = "<center>*** Login successful (24.49.42.149) ***</center>"
    val test3 = "Please enter your username:\n" +
            "Please enter your password:\n" +
            "SKOTOS Zealous 0.7.12.2\n" +
            "\n" +
            "===============================================================================\n" +
            "                          Welcome to The Eternal City\n" +
            "                           (c)1996-2013 Skotos Tech\n" +
            "                       --------------------------------\n" +
            "                         Welcome to The Eternal City!\n" +
            "                                       \n" +
            "          If you are having any password or other connection troubles\n" +
            "                       please send mail to ce@skotos.net\n" +
            "===============================================================================\n" +
            "Login: \n" +
            "USER patrickdtec\n" +
            "SECRET NONE\n" +
            "HASH 6eb467734fe5bfdf1ca085a2be4f2b4c\n" +
            "CHAR \n" +
            "\n" +
            "Enter your password: <xch_page clear=\"text\" />\n" +
            "The Eternal City uses email to notify you of several things.  If you wish to receive these notifications please set an e-mail address using the @email command.\n" +
            "\n" +
            "\n" +
            "<center>*** Login successful (24.49.42.149) ***</center>" +
            "<center>** Last connection at 11:50 pm, Thursday, July 27, 2017 **</center>" +
            "<center>** You are the fourteen million, eight hundred seventy-five thousand, three hundred eighty-sixth login to date **</center>\n" +
            "Welcome to the Welcome Room! This cozy little place might be ideal, whether you're just passing through or stopping by for a chat. A fountain trickles nearby, surrounded by several comfortable-looking chairs. \n" +
            "No new forum messages found.\n" +
            "\n" +
            "\n" +
            "\n" +
            "play\n" +
            "</pre><pre><font size=+1>Character Manager\n" +
            "<hr>\n" +
            " Select a character with which to enter the game:\n" +
            "\n" +
            " [1] Crait\n" +
            " [2] Sahar\n" +
            "\n" +
            " Or choose from the following options:\n" +
            "\n" +
            " [C] Create a new character\n" +
            " [D] Delete a character\n" +
            " [S] Spend Role-points\n" +
            " [P] Spend Story-Points\n" +
            "</font>\n" +
            "<hr>\n" +
            "</pre>character&gt; \n" +
            "1\n" +
            "It is early morning on the 14th day of Jemros in the 283rd Year of the Republic.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "*** Login successful (24.49.42.149) ***\n" +
            "** Last connection at 11:50 pm, Thursday, July 27, 2017 **\n" +
            "** You are the fourteen million, eight hundred seventy-five thousand, three hundred eighty-seventh login to date **\n" +
            "You see a wide cobbled road.\n" +
            "\n" +
            "\n" +
            "l\n" +
            "You are at a wide cobbled road. The rubble of a stone building collapsed in on itself lies right to the south. \n" +
            "You are facing south. You see a wide cobbled road to the north, to the east, and to the southwest.\n" +
            " The area looks deserted.\n" +
            "i\n" +
            "You are wearing:\n" +
            "    * some dark linen breeches\n" +
            "    * a lorica hamata\n" +
            "    * a leather belt with iron studs\n" +
            "    * a small leather sheath\n" +
            "        * a boison dagger\n" +
            "    * a black homespun wool paenula edged with a row of blue crossed daggers\n" +
            "    * some leather thigh greaves\n" +
            "    * a leather arming jerkin\n" +
            "    * some leather shoulder pteryges\n" +
            "    * a large red woolen sack\n" +
            "        * a piece of parchment labeled 'Cruentus Laureola Recruitment'\n" +
            "        * a large sack\n" +
            "        * a large sack\n" +
            "        * a large sack\n" +
            "        * a large sack\n" +
            "        * a stuffed toy esecarnus\n" +
            "        * a stuffed toy goose\n" +
            "        * a large sack\n" +
            "        * a waterskin\n" +
            "        * a blue ceramic lantern\n" +
            "        * some coins\n" +
            "        * a piece of raw squirrel meat\n" +
            "        * a piece of raw squirrel meat\n" +
            "        * a piece of raw squirrel meat\n" +
            "        * some cordage\n" +
            "        * some small dry pieces of deadwood\n" +
            "        * a piece of raw squirrel meat\n" +
            "        * a burnt piece of squirrel meat\n" +
            "        * a tin dagger\n" +
            "        * a torch\n" +
            "        * some coins\n" +
            "    * some heavy hobnailed soldiers boots\n" +
            "    * a bronze visored helmet\n" +
            "    * a bronze greave\n" +
            "    * a bronze greave\n" +
            "    * some torn leather gloves with tarnished iron studs at the knuckles\n" +
            "</ul>\n" +
            "You are holding a somewhat crude torch in your right hand and nothing in your left hand.\n" +
            "Dylios walks in from a wide cobbled road.\n" +
            "se\n" +
            "You don't see a &quot;se&quot; here.\n" +
            "Dylios walks away to a wide cobbled road, to the southwest.\n" +
            "l\n" +
            "You are at a wide cobbled road. The rubble of a stone building collapsed in on itself lies right to the south. \n" +
            "You are facing south. You see a wide cobbled road to the north, to the east, and to the southwest.\n" +
            " The area looks deserted.\n" +
            "walk to gil\n" +
            "You head towards Gilven.\n" +
            "You walk to a wide cobbled road, to the southwest.\n" +
            "You arrive at a wide cobbled road. You are facing southwest. You see a wide cobbled road to the northeast and to the southwest; a river walk to the southeast; and a wide flagstone plaza to the northwest.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a wide cobbled road, to the southwest.\n" +
            "You arrive at a wide cobbled road. You are facing southwest. You see a wide cobbled road to the northeast and to the west; and a wide stone bridge to the southwest.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a wide stone bridge, to the southwest.\n" +
            "You arrive at a wide stone bridge. You are facing southwest. You see a wide cobbled road to the northeast and a wide stone bridge to the southwest.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a wide stone bridge, to the southwest.\n" +
            "You arrive at a wide stone bridge. You are facing southwest. You see a wide stone bridge to the northeast and the base of a wide stone bridge to the southwest.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to the base of a wide stone bridge, to the southwest.\n" +
            "You arrive at the base of a wide stone bridge. You are facing southwest. You see a wide stone bridge to the northeast and a wide plaza paved with red cobblestone to the southwest.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a wide plaza paved with red cobblestone, to the southwest.\n" +
            "You arrive at a wide plaza paved with red cobblestone. You are facing southwest. You see the base of a wide stone bridge to the northeast and a wide plaza paved with red cobblestone to the south.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a wide plaza paved with red cobblestone, to the south.\n" +
            "You arrive at a wide plaza paved with red cobblestone. You are facing south. You see a wide plaza paved with red cobblestone to the north and to the west; a broad avenue paved with red cobblestone to the east; a wide cobblestone avenue to the southeast; and a wide cobblestone avenue and a wooden gate to a small yard in which several small carriages are lined up to the south.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "skills\n" +
            "</pre><pre><font size=+1>Skills and Actions:\n" +
            "<hr>\n" +
            " General Skill Points:     5.0\n" +
            " You are using 4 skill slot(s), out of a total of 4.\n" +
            "Skills/Actions           Rank                     Rank Bonus  Skill Points\n" +
            "-----------------------  -----------------------  ----------  ------------\n" +
            "Combat Maneuvers         100       grand master   115         10.54\n" +
            "Basic Dodge              20        familiar       102\n" +
            "Duck                     20        familiar       102\n" +
            "Sidestep                 20        familiar       102\n" +
            "Jump                     20        familiar       80\n" +
            "Killing Blow             1         novice         15\n" +
            "Leg Dodge                20        familiar       80\n" +
            "Recovery                 20        familiar       26\n" +
            "Swaying Dodge            20        familiar       80\n" +
            "Rolling Dodge            5         novice         32\n" +
            "Simple Rolling Rise      9         novice         55\n" +
            "Footwork                 76        adept          28\n" +
            "Reflexes                 90        grand master   30\n" +
            "Skills/Actions           Rank                     Rank Bonus  Skill Points\n" +
            "-----------------------  -----------------------  ----------  ------------\n" +
            "Knives                   100       grand master   115         50.06\n" +
            "Knife Chop               20        familiar       34\n" +
            "Knife Jab                20        familiar       34\n" +
            "Knife Overhead Strike    11        practiced      29\n" +
            "Knife Cross Block        10        practiced      65\n" +
            "Knife Feint              1         novice         15\n" +
            "Knife Round Strike       11        practiced      22\n" +
            "Knife Short Block        10        practiced      65\n" +
            "Knife Simple Stab        50        outstanding    36\n" +
            "Knife Slash              50        outstanding    36\n" +
            "Knife Stab and Slash     50        outstanding    36\n" +
            "Knife Underhand Stab     50        outstanding    36\n" +
            "Knife Whirling Slash     50        outstanding    36\n" +
            "Knife Wrist-Dancing      1         novice         60\n" +
            "Knife Long Block         10        practiced      44\n" +
            "Knife Step and Lunge     1         novice         7\n" +
            "Knives Accuracy          50        outstanding    29\n" +
            "Knives Grip              100       grand master   31\n" +
            "Skills/Actions           Rank                     Rank Bonus  Skill Points\n" +
            "-----------------------  -----------------------  ----------  ------------\n" +
            "Hunting                  20        familiar       50          139.13\n" +
            "Basic Skinning           10        practiced      67\n" +
            "Deadfall Snares          10        practiced      67\n" +
            "Forester Dismantling     1         novice         40\n" +
            "Pole Fishing             2         novice         43\n" +
            "Cord Snares              13        practiced      61\n" +
            "Intermediate Skinning    19        practiced      73\n" +
            "Advanced Skinning        20        familiar       62\n" +
            "Sapling Snares           1         novice         15\n" +
            "Skills/Actions           Rank                     Rank Bonus  Skill Points\n" +
            "-----------------------  -----------------------  ----------  ------------\n" +
            "Cineran Knife Fighting   1         novice         3           0.0\n" +
            "CKF Screnaca Coranadin St1         novice         1\n" +
            "Languages                Rank                     Rank Bonus  Language Points\n" +
            "-----------------------  -----------------------  ----------  ------------\n" +
            "Languages                13        practiced      36          SPs: 0.0\n" +
            "Spoken Cineran           50        outstanding    117         0\n" +
            "Time left until the next training cycle: 5 days\n" +
            "For action syntax and use, type: skills ?\n" +
            "<hr>\n" +
            "</pre>You walk to a broad avenue paved with red cobblestone, to the east.\n" +
            "You arrive at a broad avenue paved with red cobblestone. You are facing east. You see a broad avenue paved with red cobblestone to the east, a solid pine door to a long orange painted insula to the south, and a wide plaza paved with red cobblestone to the west.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a broad avenue paved with red cobblestone, to the east.\n" +
            "You arrive at a broad avenue paved with red cobblestone. You are facing east. You see a broad avenue paved with red cobblestone to the east and to the west; and a wide stone arch to the south.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a broad avenue paved with red cobblestone, to the east.\n" +
            "You arrive at a broad avenue paved with red cobblestone. You are facing east. You see a broad avenue paved with red cobblestone to the east and to the west; and a door to the south.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a broad avenue paved with red cobblestone, to the east.\n" +
            "You arrive at a broad avenue paved with red cobblestone. You are facing east. You see a broad avenue paved with red cobblestone to the east and to the west.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a broad avenue paved with red cobblestone, to the east.\n" +
            "You arrive at a broad avenue paved with red cobblestone. You are facing east. You see a broad avenue paved with red cobblestone to the east and to the west; and a wide cobblestone avenue to the south.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a broad avenue paved with red cobblestone, to the east.\n" +
            "You arrive at a broad avenue paved with red cobblestone. You are facing east. You see a broad avenue paved with red cobblestone to the east and to the west; and a door painted in rainbow swirls to a building built of gray brick and rough timbers to the south.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a broad avenue paved with red cobblestone, to the east.\n" +
            "You arrive at a broad avenue paved with red cobblestone. You are facing east. You see a broad avenue paved with red cobblestone to the east and to the west; and a cobbled side street to the south.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a broad avenue paved with red cobblestone, to the east.\n" +
            "You arrive at a broad avenue paved with red cobblestone. You are facing east. You see a broad avenue paved with red cobblestone to the east and to the west; and a wide oak door to the south.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a broad avenue paved with red cobblestone, to the east.\n" +
            "You arrive at a broad avenue paved with red cobblestone. You are facing east. You see a broad avenue paved with red cobblestone to the east and to the west; and a wooden door to an enormous windowless structure made of white brick to the south.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a broad avenue paved with red cobblestone, to the east.\n" +
            "You arrive at a broad avenue paved with red cobblestone. You are facing east. You see a broad avenue paved with red cobblestone to the northeast and to the west; a door to the east; and a wide side street to the southeast.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "You walk to a wide side street, to the southeast.\n" +
            "You arrive at a wide side street. You are facing southeast. You see a narrow wooden door to a small blue brick building to the east, a wide side street to the south, and a broad avenue paved with red cobblestone to the northwest.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "stats\n" +
            "</pre><pre><font size=+1>Character Sheet for Crait\n" +
            "<hr>\n" +
            "You stop walking, having reached your destination.\n" +
            "Character Background\n" +
            "Name: Crait Dinus                            Homeland : Cineran\n" +
            "Marital Status: Single\n" +
            "Citizenship Status: Foreigner\n" +
            "Popularity:  0\n" +
            "Age: 16\n" +
            "Physical Characteristics\n" +
            "Height: 5' 7&quot;            Weight: 140 lbs.    Handed: Right\n" +
            "Eyes: raven              Hair: blue-black    Complexion: bronze\n" +
            "Health Points: 110/110   Fatigue: 100%       State: conscious\n" +
            "Load: 54 lbs.\n" +
            "Encumbrance: You are bearing a moderate load.\n" +
            "Position: standing\n" +
            "Attributes:\n" +
            "Agility:    fairly good                Appearance: average                 \n" +
            "Charisma:   below average              Dexterity:  great                   \n" +
            "Empathy:    poor                       Endurance:  average                 \n" +
            "Judgement:  slightly below average     Memory:     average                 \n" +
            "Perception: fairly good                Reasoning:  below average           \n" +
            "Speed:      good                       Strength:   average                 \n" +
            "Willpower:  average                 \n" +
            "To see a list of skills and actions your character knows, type: skills\n" +
            "<hr>\n" +
            "skills ?\n" +
            "</pre></pre><pre><font size=+1>Skill templates\n" +
            "<hr>\n" +
            "Advanced Skinning        :  N/A\n" +
            "Basic Attack             :  attack &lt;target&gt;\n" +
            "Basic Dodge              :  N/A\n" +
            "Basic Skinning           :  skin &lt;item&gt; from corpse\n" +
            "CKF Screnaca Coranadin St:  ckf\n" +
            "Cord Snares              :  setup cord snare\n" +
            "Deadfall Snares          :  setup deadfall snare\n" +
            "Duck                     :  N/A\n" +
            "Footwork                 :  &lt;automatic&gt;\n" +
            "Forester Dismantling     :  dismantle &lt;snare&gt;\n" +
            "Intermediate Skinning    :  N/A\n" +
            "Jump                     :  N/A\n" +
            "Killing Blow             :  kill &lt;character&gt;\n" +
            "Knife Chop               :  chop &lt;character&gt;\n" +
            "Knife Cross Block        :  N/A\n" +
            "Knife Feint              :  feint &lt;character&gt;\n" +
            "Knife Jab                :  jab &lt;character&gt;\n" +
            "Knife Long Block         :  N/A\n" +
            "Knife Overhead Strike    :  strike &lt;character&gt;\n" +
            "Knife Round Strike       :  round &lt;character&gt;\n" +
            "Knife Short Block        :  N/A\n" +
            "Knife Simple Stab        :  stab &lt;character&gt;\n" +
            "Knife Slash              :  slash &lt;character&gt;\n" +
            "Knife Stab and Slash     :  double?cut &lt;character&gt;\n" +
            "Knife Step and Lunge     :  lunge &lt;character&gt;\n" +
            "Knife Underhand Stab     :  ustab &lt;character&gt;\n" +
            "Knife Whirling Slash     :  whirls?lash &lt;target&gt;\n" +
            "Knife Wrist-Dancing      :  wristdance\n" +
            "Knives Accuracy          :  &lt;automatic&gt;\n" +
            "Knives Grip              :  &lt;automatic&gt;\n" +
            "Leg Dodge                :  N/A\n" +
            "Pole Fishing             :  cast &lt;fishing pole&gt;\n" +
            "Recovery                 :  N/A\n" +
            "Reflexes                 :  &lt;automatic&gt;\n" +
            "Rolling Dodge            :  N/A\n" +
            "Sapling Snares           :  setup sapling snare\n" +
            "Sidestep                 :  N/A\n" +
            "Simple Rolling Rise      :  N/A\n" +
            "Spoken Cineran           :  speak cineran &lt;text&gt;\n" +
            "Swaying Dodge            :  N/A\n" +
            "<hr>\n" +
            "</pre>&lt;Nicasia thinks aloud: Alright.. I'm bored.&gt;"
}