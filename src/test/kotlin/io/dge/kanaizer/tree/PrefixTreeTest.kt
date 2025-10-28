package io.dge.kanaizer.tree

import io.dge.kanaizer.translator.Translator
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
        val translator = Translator(tree)

        val text = "aeiou"
        val expected = "あえいおう"
        assertEquals(expected, translator.translate(text))
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
        val translator = Translator(tree)

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
        val translator = Translator(tree)

        val text = ""
        val expected = ""
        assertEquals(expected, translator.translate(text))
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
        val translator = Translator(tree)

        mapOf(
            "nandesuka" to "なんですか",
            "tabemasu" to "たべます",
            "nomimasu" to "のみます",
            "kudasai" to "ください",
            "arimasen" to "ありません",
        ).forEach(verifyTranslations(translator))
    }

    @Test
    fun `ambiguous romaji`() {
        val tree = PrefixTree.builder().apply {
            put("ni", "に")
            put("gi", "ぎ")
            put("ri", "り")
            put("zu", "ず")
            put("zu", "づ")
            put("shi", "し")
        }.build()
        val translator = Translator(tree, showAmbiguity = true)

        val text = "nigirizushi"
        val expected = "にぎり[ず|づ]し"
        assertEquals(expected, translator.translate(text))
    }

    @Test
    fun `doubled consonants`() {
        val tree = KanaTreeBuilder(sokuon = "っ").apply {
            put("cho", "ちょ", KanaFlag.ADD_DOUBLE_CONSONANT)
            put("da", "だ")
            put("i", "い")
            put("i", "い")
            put("ki", "き", KanaFlag.ADD_DOUBLE_CONSONANT)
            put("ku", "く", KanaFlag.ADD_DOUBLE_CONSONANT)
            put("kyo", "きょ", KanaFlag.ADD_DOUBLE_CONSONANT)
            put("ma", "ま")
            put("me", "め")
            put("n", "ん")
            put("ne", "ね")
            put("ni", "に")
            put("no", "の")
            put("ra", "ら")
            put("sa", "さ", KanaFlag.ADD_DOUBLE_CONSONANT)
            put("sha", "しゃ", KanaFlag.ADD_DOUBLE_CONSONANT)
            put("shi", "し", KanaFlag.ADD_DOUBLE_CONSONANT)
            put("su", "す")
            put("ta", "た", KanaFlag.ADD_DOUBLE_CONSONANT)
            put("te", "て", KanaFlag.ADD_DOUBLE_CONSONANT)
            put("to", "と", KanaFlag.ADD_DOUBLE_CONSONANT)
            put("ya", "や")
        }.build()
        val translator = Translator(tree)

        mapOf(
            "chottomattekudasai" to "ちょっとまってください",
            "yakkyokuniittekimasu" to "やっきょくにいってきます",
            "nennotame" to "ねんのため",
            "irasshaimashita" to "いらっしゃいました",
        ).forEach(verifyTranslations(translator))
    }

    @Test
    fun `katakana with long vowels`() {
        val tree = KanaTreeBuilder(sokuon = "ッ", choonpu = "ー").apply {
            put("de", "デ", KanaFlag.ADD_LONG_VOWEL)
            put("fu", "フ", KanaFlag.ADD_LONG_VOWEL)
            put("i", "イ", KanaFlag.ADD_LONG_VOWEL)
            put("ka", "カ", KanaFlag.ADD_DOUBLE_CONSONANT, KanaFlag.ADD_LONG_VOWEL)
            put("ke", "ケ", KanaFlag.ADD_DOUBLE_CONSONANT, KanaFlag.ADD_LONG_VOWEL)
            put("ma", "マ", KanaFlag.ADD_LONG_VOWEL)
            put("na", "ナ", KanaFlag.ADD_LONG_VOWEL)
            put("pa", "パ", KanaFlag.ADD_DOUBLE_CONSONANT, KanaFlag.ADD_LONG_VOWEL)
            put("ra", "ラ", KanaFlag.ADD_LONG_VOWEL)
            put("re", "レ", KanaFlag.ADD_LONG_VOWEL)
            put("ri", "リ", KanaFlag.ADD_LONG_VOWEL)
            put("sa", "サ", KanaFlag.ADD_DOUBLE_CONSONANT, KanaFlag.ADD_LONG_VOWEL)
            put("su", "ス", KanaFlag.ADD_DOUBLE_CONSONANT, KanaFlag.ADD_LONG_VOWEL)
            put("to", "ト", KanaFlag.ADD_DOUBLE_CONSONANT, KanaFlag.ADD_LONG_VOWEL)
            put("za", "ザ", KanaFlag.ADD_LONG_VOWEL)
            put("n", "ン")
        }.build()
        val translator = Translator(tree)

        mapOf(
            "karee" to "カレー",
            "dezainaa" to "デザイナー",
            "furiiransu" to "フリーランス",
            "suupaamaaketto" to "スーパーマーケット",
            "sakkaa" to "サッカー",
        ).forEach(verifyTranslations(translator))
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
        ).forEach(verifyTranslations(translator))
    }

    private fun verifyTranslations(translator: Translator): (Map.Entry<String, String>) -> Unit {
        return fun(entry: Map.Entry<String, String>) {
            val (text, expected) = entry
            val actual = translator.translate(text)
            val message = "translate(\"$text\") -> \"$actual\", but it should be \"$expected\""
            assertEquals(expected, actual, message)
        }
    }
}
