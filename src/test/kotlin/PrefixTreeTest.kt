import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains
import kotlin.test.assertEquals

class PrefixTreeTest {
    @Test
    fun `basic test with single letter romaji`() {
        val tree = PrefixTree().apply {
            put("a", "あ")
            put("i", "い")
            put("u", "う")
            put("e", "え")
            put("o", "お")
        }

        val text = "aeiou"
        val expected = "あえいおう"
        assertEquals(expected, tree.translate(text))
    }

    @Test
    fun `bad text`() {
        val tree = PrefixTree().apply {
            put("a", "あ")
            put("i", "い")
            put("u", "う")
            put("e", "え")
            put("o", "お")
        }

        val ex: IllegalArgumentException = assertThrows<IllegalArgumentException> {
            tree.translate("aeibadtext")
        }
        assertEquals(ex.message, "provided text is invalid. Next 4 remaining characters: badt")
    }

    @Test
    fun `empty input`() {
        val tree = PrefixTree().apply {
            put("a", "あ")
            put("i", "い")
            put("u", "う")
            put("e", "え")
            put("o", "お")
        }

        val text = ""
        val expected = ""
        assertEquals(expected, tree.translate(text))
    }

    @Test
    fun `put an empty string into the tree`() {
        val ex: IllegalArgumentException = assertThrows<IllegalArgumentException> {
            PrefixTree().apply {
                put("", "asdf")
            }
        }

        assertContains(ex.message!!, "empty sequence")
    }

    @Test
    fun `basic test with a mix of single and double letters`() {
        val tree = PrefixTree().apply {
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
        }

        mapOf(
            "nandesuka" to "なんですか",
            "tabemasu" to "たべます",
            "nomimasu" to "のみます",
            "kudasai" to "ください",
            "arimasen" to "ありません",
        ).forEach { (text, expected) ->
            val actual = tree.translate(text)
            val message = "translate(\"$text\") -> \"$actual\", but it should be \"$expected\""
            assertEquals(expected, actual, message)
        }
    }
}