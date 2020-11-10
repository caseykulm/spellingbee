package parser

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import models.Dictionary
import okio.buffer
import okio.source
import timing
import java.io.File
import java.lang.reflect.ParameterizedType

class SimpleMapDictionaryParser(private val moshi: Moshi) : DictionaryParserDecorator<Map<String, String>> {
    private val typeAdapter: ParameterizedType = Types.newParameterizedType(
        Map::class.java,
        String::class.java,
        String::class.java
    )
    private val dictionaryAdapter = moshi.adapter<Map<String, String>>(typeAdapter)

    override fun parse(filePath: String): Dictionary = timing(
        message = "parse file from disk to Dictionary"
    ) {
        val dictionary = modelToDictionary(stringToModel(filePathToString(filePath)))
        return@timing dictionary
    }

    override fun filePathToString(filePath: String): String {
        // javaClass.classLoader.getResource("dictionary.json").file
        val dictionaryFile = File(filePath)
        return dictionaryFile.source().buffer().readUtf8()
    }

    override fun stringToModel(str: String): Map<String, String> {
        return dictionaryAdapter.fromJson(str)!!
    }

    override fun modelToDictionary(model: Map<String, String>): Dictionary {
        return model
    }
}