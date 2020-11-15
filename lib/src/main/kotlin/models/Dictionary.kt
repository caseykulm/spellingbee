package models

typealias Dictionary = Map<String, String>

fun Dictionary.definition(word: String): String? = this[word]
val Dictionary.words get() = keys
