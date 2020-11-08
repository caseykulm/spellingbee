package models

import processor.UniqueCharSetPool
import uniqueCount

enum class SpellingBeeFilterRule(val predicate: (String, UniqueCharSetPool) -> Boolean) {
    AlphaOnly(predicate = { word: String, _: UniqueCharSetPool -> word.contains(Regex("^[A-z]+\$")) }),
    Length4OrGreater(predicate = { word: String, _ -> word.length >= 4 }),
    SevenOrFewerUnique(predicate = { word: String, ucsPool: UniqueCharSetPool ->
        ucsPool.getOrCreateUniqueCharSet(word).uniqueCount <= 7 }
    )
}