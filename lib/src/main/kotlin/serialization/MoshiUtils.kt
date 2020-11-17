package serialization

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import models.SpellingBeeBoard
import processor.UniqueCharSetPool

/*
    Sample json

    {
        "dangrul;d" : ["glad", "dang", ...],
        ...
    }
 */

val setOfStringsType = Types.newParameterizedType(Set::class.java, String::class.java)

class SpellingBeeBoardAdapter(private val ucsPool: UniqueCharSetPool) {
    @ToJson
    fun toJson(spellingBeeBoard: SpellingBeeBoard): String {
        return "$spellingBeeBoard"
    }
    @FromJson
    fun fromJson(jsonStr: String): SpellingBeeBoard {
        val strList: List<String> = jsonStr.split(";").toList()
        return SpellingBeeBoard(ucsPool.getOrCreateUniqueCharSet(strList[0]), strList[1][0])
    }
}

val solutionsMapType = Types.newParameterizedType(
    Map::class.java,
    SpellingBeeBoard::class.java,
    setOfStringsType
)
