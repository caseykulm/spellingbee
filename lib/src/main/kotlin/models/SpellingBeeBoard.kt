package models

import processor.UniqueCharSetPool

class SpellingBeeBoard(val ucs: UniqueCharSet, val centerChar: Char) : Comparable<SpellingBeeBoard> {
    init {
        require(ucs.contains(centerChar))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpellingBeeBoard

        if (ucs != other.ucs) return false
        if (centerChar != other.centerChar) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ucs.hashCode()
        result = 31 * result + centerChar.hashCode()
        return result
    }

    override fun toString(): String {
        return "$ucs;$centerChar"
    }

    override fun compareTo(other: SpellingBeeBoard): Int {
        return toString().compareTo(other.toString())
    }
}

fun String.toSpellingBeeBoard(ucsPool: UniqueCharSetPool): SpellingBeeBoard {
    return this.split(";").let {
        SpellingBeeBoard(
            ucsPool.getOrCreateUniqueCharSet(it[0]),
            it[1].toCharArray()[0]
        )
    }
}
