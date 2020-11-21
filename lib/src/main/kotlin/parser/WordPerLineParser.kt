package parser

import models.Dictionary
import okio.buffer
import okio.source
import java.io.File

class WordPerLineParser(val lineLimit: Int) : DictionaryParser {
    override fun parse(filePath: String): Dictionary {
        val file = File(filePath)
        val source = file.source()
        val fileStr = source.buffer().readUtf8()
        val words = fileStr.split(Regex("\n"))

        return words
            .take(lineLimit)
            .associateWith { "" }
    }
}
