package io.dge.kanaizer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import io.dge.kanaizer.translator.Translator
import io.dge.kanaizer.tree.HIRAGANA_MAPPINGS

class Kanaizer : CliktCommand() {
    val applyPimsleurFilter: Boolean by option("--pimsleur")
        .flag()
        .help("Apply Pimsleur filter. Useful for romaji coming from Pimsleur flashcards.")

    override fun run() {
        echo("Pimsleur filter = $applyPimsleurFilter")

        val translator = Translator(HIRAGANA_MAPPINGS)

        var input: String? = readlnOrNull()
        while (input != null) {
            val translated = translator.translate(input)
            echo(translated)
            input = readlnOrNull()
        }
    }
}

fun main(args: Array<String>) {
    Kanaizer().main(args)
}
