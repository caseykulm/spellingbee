package processor

import UniqueCharSet

interface UniqueCharSetPool {
    fun getOrCreateUniqueCharSet(word: String): UniqueCharSet
    fun logHitMissRate()
}
