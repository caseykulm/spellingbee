package processor

import models.UniqueCharSet

interface UniqueCharSetPool {
    fun getOrCreateUniqueCharSet(word: String): UniqueCharSet
    fun logHitMissRate()
}
