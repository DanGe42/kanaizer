class PrefixTree {
    private val root = PrefixNode()

    /**
     * Put a sequence into the prefix tree. [sequence] cannot be empty.
     */
    fun put(sequence: String, element: String) {
        if (sequence.isEmpty()) {
            // This ensures the root element is always empty.
            // This is useful because if getNext returns an empty list of elements, then it can indicate either the
            // end of input or invalid input.
            throw IllegalArgumentException("Cannot put empty sequence into tree")
        }
        root.put(sequence, element)
    }

    /**
     * Retrieve the next element(s) and return it as part of a [Lookup]. The [Lookup] data object contains:
     *
     * - [Lookup.elements]: the next possible elements that matches the next prefix in the text
     * - [Lookup.remaining]: remainder of the text
     *
     * If [Lookup.elements] is empty, then you either have reached the end of input or have supplied invalid input.
     *
     * Note that this method is not whitespace or punctuation aware: make sure to strip them from input.
     */
    fun getNext(text: String): Lookup {
        return root.getNext(text)
    }
}

fun PrefixTree.translate(text: String): String {
    // We need a dummy seed to start with. Otherwise, we have to use a mutable variable outside the lambda to
    // keep track of the remaining text and update this variable within the lambda, which confuses IntelliJ
    // into thinking that we're setting the variable inside the lambda but not using it later.
    val sequenceSeed = Lookup(elements = emptyList(), remaining = text)

    val translated: Sequence<Lookup> = generateSequence(sequenceSeed) { prev ->
        val lookup = getNext(prev.remaining)
        when {
            lookup.isInvalid() -> throw IllegalArgumentException(
                    "provided text is invalid. Next 4 remaining characters: ${lookup.remaining.take(4)}" )
            lookup.isEndOfInput() -> null
            else -> lookup
        }
    }

    return translated
        .drop(1) // Drop the translation for the dummy seed
        .map { it.elements.first() }
        .joinToString("")
}

data class Lookup(val elements: List<String>, val remaining: String) {
    fun isEndOfInput(): Boolean = elements.isEmpty() && remaining.isEmpty()
    fun isInvalid(): Boolean = elements.isEmpty() && !remaining.isEmpty()
}

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

    fun getNext(text: String): Lookup {
        // First base case
        if (text.isEmpty()) {
            return Lookup(elements, text)
        }

        val nextChar: Char = text.first()
        val nextNode = prefixes[nextChar]

        // Second base case
        if (nextNode == null) {
            return Lookup(elements, text)
        }

        val remaining: String = text.substring(1)
        return nextNode.getNext(remaining)
    }
}