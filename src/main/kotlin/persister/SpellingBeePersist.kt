package persister

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import models.SpellingBeeBoard
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.InputStream
import models.BoardSolutions
import processor.UniqueCharSetPool
import timing

/*
    Sample json

    {
        "dangrul;d" : ["glad", "dang", ...],
        ...
    }
 */

private val setOfStringsType = Types.newParameterizedType(Set::class.java, String::class.java)

private class SpellingBeeBoardAdapter(private val ucsPool: UniqueCharSetPool) {
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

private val solutionsMapType = Types.newParameterizedType(
    Map::class.java,
    SpellingBeeBoard::class.java,
    setOfStringsType
)

fun writeSolutionsToDisk(solutions: BoardSolutions, ucsPool: UniqueCharSetPool) = timing(
    message = "write all ${solutions.size} solutions to disk"
) {
    val solutionsJsonStr: String = solutionsToJsonString(solutions, ucsPool)
    val resourceDirectory: File = File(SpellingBeeBoard::class.java.classLoader.getResource("").file)
    if (resourceDirectory.listFiles().none { it.name == "solutions.json" }) {
        File(resourceDirectory.path + "/solutions.json").createNewFile()
    }
    val solutionsFile = File(SpellingBeeBoard::class.java.classLoader.getResource("solutions.json").file)
    copyToFile(solutionsJsonStr.byteInputStream(), solutionsFile)
}

private fun solutionsToJsonString(solutions: BoardSolutions, ucsPool: UniqueCharSetPool): String {
    return Moshi.Builder().add(SpellingBeeBoardAdapter(ucsPool)).build()
        .adapter<Map<SpellingBeeBoard, Set<String>>>(solutionsMapType)
//        .indent("    ") // Can add this for smaller sizes, but this drastically increases file size
        .toJson(solutions)
}

private fun copyToFile(inputStream: InputStream, outputFile: File) {
    val source = inputStream.source().buffer()
    val sink = outputFile.sink().buffer()

    source.use { input ->
        sink.use { output ->
            output.writeAll(input)
        }
    }
}

fun readSolutionFromDisk(ucsPool: UniqueCharSetPool): BoardSolutions? = timing(
    message = "read all solutions from disk"
) {
    val solutionsFile = File(SpellingBeeBoard::class.java.classLoader.getResource("solutions.json").file)
    Moshi.Builder().add(SpellingBeeBoardAdapter(ucsPool)).build()
        .adapter<Map<SpellingBeeBoard, Set<String>>>(solutionsMapType)
        .fromJson(solutionsFile.source().buffer())
}