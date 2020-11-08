package processor

import UniqueCharSet

class SimpleUniqueCharSetPool : UniqueCharSetPool {
    private var cacheHitCount = 0
    private var cacheMissCount = 0
    private val percentHit: Float get() = (cacheHitCount.toFloat() / (cacheHitCount + cacheMissCount)) * 100
    private val wordToUcsMap: MutableMap<String, UniqueCharSet> = mutableMapOf()

    override fun getOrCreateUniqueCharSet(word: String): UniqueCharSet {
        return if (wordToUcsMap.containsKey(word)) {
            cacheHitCount++
            wordToUcsMap[word]!!
        } else {
            cacheMissCount++
            wordToUcsMap[word] = UniqueCharSet(word)
            wordToUcsMap[word]!!
        }
    }

    override fun logHitMissRate() {
        println("Hits: $cacheHitCount, Misses: $cacheMissCount, Percent Hit: $percentHit%")
    }
}