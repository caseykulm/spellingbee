import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import okio.BufferedSource
import okio.Okio
import java.io.File
import java.io.InputStream

/*
    Sample json

    {
        "dangrul;d" : ["glad", "dang", ...],
        ...
    }
 */

private val setOfStringsType = Types.newParameterizedType(Set::class.java, String::class.java)

private class SpellingBeeBoardAdapter {
    @ToJson
    fun toJson(spellingBeeBoard: SpellingBeeBoard): String {
        return "$spellingBeeBoard"
    }
    @FromJson
    fun fromJson(jsonStr: String): SpellingBeeBoard {
        val strList: List<String> = jsonStr.split(";").toList()
        return SpellingBeeBoard(UniqueCharSet(strList[0]), strList[1][0])
    }

}

private val solutionsMapType = Types.newParameterizedType(
    Map::class.java,
    SpellingBeeBoard::class.java,
    setOfStringsType
)

fun writeSolutionsToDisk(solutions: Map<SpellingBeeBoard, Set<String>>) {
    val startTime = System.currentTimeMillis()

    val solutionsJsonStr: String = solutionsToJsonString(solutions)
    val resourceDirectory: File = File(DictionaryTool::class.java.classLoader.getResource("").file)
    if (resourceDirectory.listFiles().none { it.name == "solutions.json" }) {
        File(resourceDirectory.path + "/solutions.json").createNewFile()
    }
    val solutionsFile = File(DictionaryTool::class.java.classLoader.getResource("solutions.json").file)
    copyToFile(solutionsJsonStr.byteInputStream(), solutionsFile)

    val totalTime = System.currentTimeMillis() - startTime
    println("It took $totalTime ms to write all ${solutions.size} solutions to disk")
}

private fun solutionsToJsonString(solutions: Map<SpellingBeeBoard, Set<String>>): String {
    return Moshi.Builder().add(SpellingBeeBoardAdapter()).build()
        .adapter<Map<SpellingBeeBoard, Set<String>>>(solutionsMapType)
//        .indent("    ") // Can add this for smaller sizes, but this drastically increases file size
        .toJson(solutions)
}

private fun copyToFile(inputStream: InputStream, outputFile: File) {
    val source = Okio.buffer(Okio.source(inputStream))
    val sink = Okio.buffer(Okio.sink(outputFile))

    source.use { input ->
        sink.use { output ->
            output.writeAll(input)
        }
    }
}

fun readSolutionFromDisk(): Map<SpellingBeeBoard, Set<String>>? {
    val solutionsFile = File(DictionaryTool::class.java.classLoader.getResource("solutions.json").file)
    return Moshi.Builder().add(SpellingBeeBoardAdapter()).build()
        .adapter<Map<SpellingBeeBoard, Set<String>>>(solutionsMapType)
        .fromJson(Okio.buffer(Okio.source(solutionsFile)))
}