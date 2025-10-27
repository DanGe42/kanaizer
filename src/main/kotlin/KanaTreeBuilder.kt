enum class KanaFlag {
    ADD_DOUBLE_CONSONANT,
}

class KanaTreeBuilder(val sokuon: String) : PrefixTree.BuilderBase<KanaTreeBuilder>() {
    override fun self(): KanaTreeBuilder = this

    fun put(sequence: String, element: String, vararg flags: KanaFlag): KanaTreeBuilder {
        val baseMapping = KanaMapping(sequence, element)
        val mappings = mutableListOf(baseMapping)

        if (KanaFlag.ADD_DOUBLE_CONSONANT in flags) {
            mappings.addAll(addDoubleConsonants(mappings))
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
}

internal data class KanaMapping(val sequence: String, val element: String)
