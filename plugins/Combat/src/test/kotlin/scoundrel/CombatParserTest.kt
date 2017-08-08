package scoundrel

import com.dattilio.scoundrel.CombatParser
import com.dattilio.scoundrel.CombatPreProcessor
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test

class CombatParserTest {
    internal var string = "[Success: 95, Roll: 91] A pale white rat with crimson splotches misses you with its claws. You dodge a pale white rat with crimson splotches's attack.\n"

    internal var mockPresenter: CombatPreProcessor = mock()
    private var processor: CombatParser = CombatParser(mockPresenter)

    @Test
    @Throws(Exception::class)
    fun updateEngaged() {

        processor.parseOpponent(string,true)
    }
}