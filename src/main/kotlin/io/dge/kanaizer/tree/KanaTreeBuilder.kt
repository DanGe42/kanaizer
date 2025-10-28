package io.dge.kanaizer.tree

/**
 * Conveniently adds a builder method to [PrefixTree].
 *
 * @see KanaTreeBuilder
 */
fun PrefixTree.Companion.kanaBuilder(sokuon: String, choonpu: String? = null): KanaTreeBuilder =
    KanaTreeBuilder(sokuon, choonpu)

/**
 * Modifier flags for [KanaTreeBuilder.put].
 */
enum class KanaFlag {
    /**
     * Also generate prefixes to map double consonants to kana with the sokuon.
     * For example, the following expression:
     *
     *     put("ku", "く", ADD_DOUBLE_CONSONANT)
     *
     * is the equivalent of:
     *
     *     put("ku", "く")
     *     put("kku", "っく")
     *
     * This flag can be stacked with [ADD_LONG_VOWEL].
     */
    ADD_DOUBLE_CONSONANT,

    /**
     * Also generate prefixes to map long vowels to kana with the choonpu.
     * For example, the following expression:
     *
     *     put("ne", "ネ", ADD_LONG_VOWEL)
     *
     * is the equivalent of:
     *
     *     put("ne", "ネ")
     *     put("nee", "ネー")
     *
     * This flag can be stacked with [ADD_DOUBLE_CONSONANT].
     */
    ADD_LONG_VOWEL,
}

/**
 * Specialized [PrefixTree] builder that can conveniently generate prefix rules for double
 * consonants and long vowels.
 *
 * For further information, see:
 * - [sokuon](https://en.wikipedia.org/wiki/Sokuon)
 * - [chōonpu](https://en.wikipedia.org/wiki/Ch%C5%8Donpu)
 *
 * @see KanaFlag
 */
class KanaTreeBuilder(val sokuon: String, val choonpu: String? = null) : PrefixTree.BuilderBase<KanaTreeBuilder>() {
    override fun self(): KanaTreeBuilder = this

    /**
     * Put a prefix sequence and its element into the tree, allowing optional flags.
     *
     * Example uses:
     *
     *     put("n", "ン")
     *     put("byo", "ビョ", ADD_LONG_VOWEL)
     *     put("pa", "パ", ADD_DOUBLE_CONSONANT, ADD_LONG_VOWEL)
     *
     * @see KanaFlag
     */
    fun put(sequence: String, element: String, vararg flags: KanaFlag): KanaTreeBuilder {
        // Creating these lists makes this method a bit more robust by allowing us to call
        // addDoubleConsonants and addLongVowels in any order.
        val baseMapping = KanaMapping(sequence, element)
        val mappings = mutableListOf(baseMapping)

        if (KanaFlag.ADD_DOUBLE_CONSONANT in flags) {
            mappings.addAll(addDoubleConsonants(mappings))
        }

        if (KanaFlag.ADD_LONG_VOWEL in flags) {
            mappings.addAll(addLongVowels(mappings))
        }

        mappings.forEach { (s, e) -> put(s, e) }
        return this
    }

    private fun addDoubleConsonants(mappings: List<KanaMapping>): List<KanaMapping> {
        val additionalMappings = mutableListOf<KanaMapping>()
        for ((seq, el) in mappings) {
            additionalMappings.add(KanaMapping(seq[0] + seq, sokuon + el))
        }
        return additionalMappings
    }

    private fun addLongVowels(mappings: List<KanaMapping>): List<KanaMapping> {
        requireNotNull(choonpu, { "ADD_LONG_VOWEL cannot be used without a choonpu" })

        val additionalMappings = mutableListOf<KanaMapping>()
        for ((seq, el) in mappings) {
            additionalMappings.add(KanaMapping(seq + seq[seq.length - 1], el + choonpu!!))
        }
        return additionalMappings
    }
}

// A type definition to avoid obfuscating the code with generic Pair objects.
internal data class KanaMapping(val sequence: String, val element: String)
