package parser

import models.Dictionary

interface DictionaryParser {
    fun parse(filePath: String): Dictionary
}

interface DictionaryParserDecorator<Model> : DictionaryParser {
    fun filePathToString(filePath: String): String
    fun stringToModel(str: String): Model
    fun modelToDictionary(model: Model): Dictionary
}
