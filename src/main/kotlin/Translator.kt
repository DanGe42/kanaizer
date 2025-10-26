class Translator(
    val prefixTree: PrefixTree,
) {
    fun translate(text: String): String {
        var startIndex = 0
        val translated = StringBuilder()
        while (startIndex < text.length) {
            val (elements, nextIndex) = prefixTree.getNext(text, startIndex)
            if (elements.isEmpty()) {
                throw IllegalArgumentException("provided text is invalid. Next 4 remaining characters: ${text.substring(nextIndex, nextIndex + 4)}")
            }
            translated.append(elements.first())
            startIndex = nextIndex
        }
        return translated.toString()
    }
}