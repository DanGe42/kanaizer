open class PrefixTree private constructor(private val root: PrefixNode) {
    companion object {
        fun builder(): Builder = Builder()
    }

    abstract class BuilderBase<B> {
        private val pairs = mutableListOf<Pair<String, String>>()

        abstract fun self(): B

        /**
         * Put a sequence into the prefix tree. [sequence] cannot be empty.
         */
        fun put(sequence: String, element: String): B {
            if (sequence.isEmpty()) {
                // This ensures the root element is always empty.
                // This is useful because if getNext returns an empty list of elements, then it can indicate either the
                // end of input or invalid input.
                throw IllegalArgumentException("Cannot put empty sequence into tree")
            }
            pairs.add(Pair(sequence, element))
            return self()
        }

        fun build(): PrefixTree {
            val root = PrefixNode()
            for ((sequence, element) in pairs) {
                root.put(sequence, element)
            }
            return PrefixTree(root)
        }
    }

    /** The most basic builder. */
    class Builder: BuilderBase<Builder>() {
        override fun self(): Builder = this
    }

    /**
     * Retrieve the next element(s) and return it as part of a [Lookup]. The [Lookup] data object contains:
     *
     * - [Lookup.elements]: the next possible elements that matches the next prefix in the text
     * - [Lookup.nextIndex]: the start of the remainder of the text that has not yet been consumed
     *
     * If [Lookup.elements] is empty, then you have likely supplied invalid input.
     *
     * Note that this method is not whitespace or punctuation aware: make sure to strip them from input.
     */
    fun getNext(text: String, startIndex: Int = 0): Lookup {
        return root.getNext(text, startIndex)
    }
}

data class Lookup(val elements: List<String>, val nextIndex: Int)

internal class PrefixNode(private val _elements: MutableList<String>) {
    constructor() : this(mutableListOf<String>())

    private val prefixes = mutableMapOf<Char, PrefixNode>()
    val elements: List<String> get() = this._elements

    fun put(sequence: String, element: String) {
        if (sequence.isEmpty()) {
            _elements.add(element)
            return
        }

        val nextChar: Char = sequence.first()
        val remaining: String = sequence.substring(1)
        val nextNode = prefixes.computeIfAbsent(nextChar, { PrefixNode() })
        nextNode.put(remaining, element)
    }

    fun getNext(text: String, startIndex: Int): Lookup {
        // First base case: you're at (or past) the end of the string
        if (startIndex >= text.length) {
            return Lookup(elements, startIndex)
        }

        val nextChar: Char = text[startIndex]
        val nextNode = prefixes[nextChar]

        // Second base case: you cannot move on from this node, so this becomes the terminal node
        if (nextNode == null) {
            return Lookup(elements, startIndex)
        }

        return nextNode.getNext(text, startIndex + 1)
    }
}