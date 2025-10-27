import KanaFlag.ADD_DOUBLE_CONSONANT
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
        val translator = Translator(
            PrefixTree.builder().apply {
                put("a", "あ")
                put("i", "い")
                put("u", "う")
                put("e", "え")
                put("o", "お")
            }.build()
        )

        var ex: IllegalArgumentException = assertThrows<IllegalArgumentException> {
            translator.translate("aeibadtext")
        }
        assertEquals(ex.message, "Invalid token at start=3 end=3. Input[start..end+4] = \"badt\"")

        ex = assertThrows<IllegalArgumentException> {
            translator.translate("aexx")
        }
        assertEquals(ex.message, "Invalid token at start=2 end=2. Input[start..end+4] = \"xx\"")
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
            put("cho", "ちょ", ADD_DOUBLE_CONSONANT)
            put("da", "だ")
            put("i", "い")
            put("i", "い")
            put("ki", "き", ADD_DOUBLE_CONSONANT)
            put("ku", "く", ADD_DOUBLE_CONSONANT)
            put("kyo", "きょ", ADD_DOUBLE_CONSONANT)
            put("ma", "ま")
            put("me", "め")
            put("n", "ん")
            put("ne", "ね")
            put("ni", "に")
            put("no", "の")
            put("ra", "ら")
            put("sa", "さ", ADD_DOUBLE_CONSONANT)
            put("sha", "しゃ", ADD_DOUBLE_CONSONANT)
            put("shi", "し", ADD_DOUBLE_CONSONANT)
            put("su", "す")
            put("ta", "た", ADD_DOUBLE_CONSONANT)
            put("te", "て", ADD_DOUBLE_CONSONANT)
            put("to", "と", ADD_DOUBLE_CONSONANT)
            put("ya", "や")
        }.build()

        mapOf(
            "chottomattekudasai" to "ちょっとまってください",
            "yakkyokuniittekimasu" to "やっきょくにいってきます",
            "nennotame" to "ねんのため",
            "irasshaimashita" to "いらっしゃいました",
        ).forEach { (text, expected) ->
            val actual = Translator(tree).translate(text)
            val message = "translate(\"$text\") -> \"$actual\", but it should be \"$expected\""
            assertEquals(expected, actual, message)
        }
    }

    // -- These tests use the full hiragana mappings -- //

    @Test
    fun `hiragana translations with punctuation and disambiguation`() {
        val translator = Translator(HIRAGANA_MAPPINGS)
        mapOf(
            "pan'ya ni ikimasu." to "ぱんや に いきます.",
            "panya ni ikimasu." to "ぱにゃ に いきます.",
            "nishukanimasu." to "にしゅかにます.",
            "nishukan'imasu." to "にしゅかんいます.",
            "nan nichi nihon ni imasu ka?" to "なん にち にほん に います か?",
            "sumimasen.  kyoto eki made ichi mai, onegai shimasu。" to "すみません.  きょと えき まで いち まい, おねがい します。"
        ).forEach { (text, expected) ->
            val actual = translator.translate(text)
            val message = "translate(\"$text\") -> \"$actual\", but it should be \"$expected\""
            assertEquals(expected, actual, message)
        }
    }
}
