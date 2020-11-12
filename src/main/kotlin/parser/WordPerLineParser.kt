package parser

import java.io.File
import models.Dictionary
import okio.buffer
import okio.source

class WordPerLineParser : DictionaryParser {
    override fun parse(filePath: String): Dictionary {
        val file = File(filePath)
        val source = file.source()
        val fileStr = source.buffer().readUtf8()
        val words = fileStr.split(Regex("\n"))

        return words.associateWith { "" }
    }
}