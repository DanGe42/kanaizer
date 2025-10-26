class KanaTreeBuilder(val sokuon: String): PrefixTree.BuilderBase<KanaTreeBuilder>() {
    override fun self(): KanaTreeBuilder = this

    fun putDoubled(sequence: String, element: String): KanaTreeBuilder {
        return put(sequence, element)
            .put(sequence[0] + sequence, sokuon + element)
    }
}