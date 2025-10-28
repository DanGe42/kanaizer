package io.dge.kanaizer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import io.dge.kanaizer.translator.Translator
import io.dge.kanaizer.tree.HIRAGANA_MAPPINGS
import io.dge.kanaizer.tree.KATAKANA_MAPPINGS

class Kanaizer : CliktCommand() {
    val applyPimsleurFilter: Boolean by option("--pimsleur")
        .flag()
        .help("Apply Pimsleur filter. Useful for romaji coming from Pimsleur flashcards.")

    val kanaMode: String? by option("-m", "--mode")
        .choice("hiragana", "katakana", "mixed")
        .default("hiragana")
        .help("Kana mode. Indicates what type of input it will be.")

    val showAmbiguity: Boolean by option("-a", "--show-ambiguity")
        .flag()
        .help("Show ambiguity if a romaji sequence can result in multiple characters.")

    override fun run() {
        echo("Pimsleur filter = $applyPimsleurFilter")

        val translator = buildTranslator()

        var input: String? = readlnOrNull()
        while (input != null) {
            val translated = translator.translate(input)
            echo(translated)
            input = readlnOrNull()
        }
    }

    private fun buildTranslator(): Translator {
        return when (kanaMode) {
            "hiragana" -> Translator(HIRAGANA_MAPPINGS, showAmbiguity = showAmbiguity)
            "katakana" -> Translator(KATAKANA_MAPPINGS, showAmbiguity = showAmbiguity)
            "mixed" -> TODO("Not yet implemented")
            else -> throw IllegalArgumentException("Invalid mode `$kanaMode`")
        }
    }
}

fun main(args: Array<String>) {
    Kanaizer().main(args)
}
