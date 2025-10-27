import kotlin.math.min

class Translator(
    val prefixTree: PrefixTree,
    val disambiguator: Char = '\'',
    val validPunctuation: String = " ,.-。?"
) {
    fun translate(text: String): String {
        var startIndex = 0
        val translated = StringBuilder()
        while (startIndex < text.length) {
            when (val result = processNext(text, startIndex)) {
                is TranslateNextResult.Disambiguator ->
                    startIndex = result.nextIndex

                is TranslateNextResult.Punctuation -> {
                    translated.append(result.punctuation)
                    startIndex = result.nextIndex
                }

                is TranslateNextResult.Translated -> {
                    translated.append(result.elements.first())
                    startIndex = result.nextIndex
                }

                is TranslateNextResult.Invalid -> {
                    val endIndexForSubstring = min(result.endIndex + 4, text.length)
                    throw IllegalArgumentException(
                        "Invalid token at start=${result.startIndex} end=${result.endIndex}. " +
                                "Input[start..end+4] = \"${text.substring(result.startIndex, endIndexForSubstring)}\""
                    )
                }
            }
        }
        return translated.toString()
    }

    private fun processNext(text: String, startIndex: Int): TranslateNextResult {
        when (val nextChar = text[startIndex]) {
            disambiguator -> {
                return TranslateNextResult.Disambiguator(startIndex + 1)
            }

            in validPunctuation -> {
                return TranslateNextResult.Punctuation(nextChar, startIndex + 1)
            }

            else -> {
                val (elements, nextIndex) = prefixTree.getNext(text, startIndex)
                if (elements.isEmpty()) {
                    return TranslateNextResult.Invalid(startIndex, nextIndex)
                }

                return TranslateNextResult.Translated(elements, nextIndex)
            }
        }
    }
}

internal sealed interface TranslateNextResult {
    data class Disambiguator(val nextIndex: Int) : TranslateNextResult
    data class Punctuation(val punctuation: Char, val nextIndex: Int) : TranslateNextResult
    data class Translated(val elements: List<String>, val nextIndex: Int) : TranslateNextResult
    data class Invalid(val startIndex: Int, val endIndex: Int) : TranslateNextResult
}
