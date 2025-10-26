import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains
import kotlin.test.assertEquals

class PrefixTreeTest {
    @Test
    fun `basic test with single letter romaji`() {
        val tree = PrefixTree.builder().apply {
            put("a", "あ")
            put("i", "い")
            put("u", "う")
            put("e", "え")
            put("o", "お")
        }.build()

        val text = "aeiou"
        val expected = "あえいおう"
        assertEquals(expected, Translator(tree).translate(text))
    }

    @Test
    fun `bad text`() {
        val tree = PrefixTree.builder().apply {
            put("a", "あ")
            put("i", "い")
            put("u", "う")
            put("e", "え")
            put("o", "お")
        }.build()

        val ex: IllegalArgumentException = assertThrows<IllegalArgumentException> {
            Translator(tree).translate("aeibadtext")
        }
        assertEquals(ex.message, "provided text is invalid. Next 4 remaining characters: badt")
    }

    @Test
    fun `empty input`() {
        val tree = PrefixTree.builder().apply {
            put("a", "あ")
            put("i", "い")
            put("u", "う")
            put("e", "え")
            put("o", "お")
        }.build()

        val text = ""
        val expected = ""
        assertEquals(expected, Translator(tree).translate(text))
    }

    @Test
    fun `put an empty string into the tree`() {
        val ex: IllegalArgumentException = assertThrows<IllegalArgumentException> {
            PrefixTree.builder().apply {
                put("", "asdf")
            }.build()
        }

        assertContains(ex.message!!, "empty sequence")
    }

    @Test
    fun `basic test with a mix of single and double letters`() {
        val tree = PrefixTree.builder().apply {
            put("a", "あ")
            put("i", "い")
            put("n", "ん")
            put("be", "べ")
            put("da", "だ")
            put("de", "で")
            put("ka", "か")
            put("ku", "く")
            put("ma", "ま")
            put("mi", "み")
            put("na", "な")
            put("no", "の")
            put("ri", "り")
            put("sa", "さ")
            put("se", "せ")
            put("su", "す")
            put("ta", "た")
        }.build()

        mapOf(
            "nandesuka" to "なんですか",
            "tabemasu" to "たべます",
            "nomimasu" to "のみます",
            "kudasai" to "ください",
            "arimasen" to "ありません",
        ).forEach { (text, expected) ->
            val actual = Translator(tree).translate(text)
            val message = "translate(\"$text\") -> \"$actual\", but it should be \"$expected\""
            assertEquals(expected, actual, message)
        }
    }

    @Test
    fun `doubled consonants`() {
        val tree = KanaTreeBuilder(sokuon = "っ").apply {
            putDoubled("cho", "ちょ")
            putDoubled("to", "と")
            put("ma", "ま")
            putDoubled("te", "て")
            putDoubled("ku", "く")
            put("da", "だ")
            putDoubled("sa", "さ")
            put("i", "い")
            put("ya", "や")
            putDoubled("kyo", "きょ")
            put("ni", "に")
            put("i", "い")
            putDoubled("ki", "き")
            put("su", "す")
            put("ne", "ね")
            put("no", "の")
            putDoubled("ta", "た")
            put("me", "め")
            put("n", "ん")
        }.build()

        mapOf(
            "chottomattekudasai" to "ちょっとまってください",
            "yakkyokuniittekimasu" to "やっきょくにいってきます",
            "nennotame" to "ねんのため",
        ).forEach { (text, expected) ->
            val actual = Translator(tree).translate(text)
            val message = "translate(\"$text\") -> \"$actual\", but it should be \"$expected\""
            assertEquals(expected, actual, message)
        }
    }
}