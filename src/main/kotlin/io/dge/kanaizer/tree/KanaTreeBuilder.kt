package io.dge.kanaizer.tree

enum class KanaFlag {
    ADD_DOUBLE_CONSONANT,
    ADD_LONG_VOWEL,
}

class KanaTreeBuilder(val sokuon: String, val choonpu: String? = null) : PrefixTree.BuilderBase<KanaTreeBuilder>() {
    override fun self(): KanaTreeBuilder = this

    fun put(sequence: String, element: String, vararg flags: KanaFlag): KanaTreeBuilder {
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

internal data class KanaMapping(val sequence: String, val element: String)
