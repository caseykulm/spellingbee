package serialization

import com.squareup.moshi.Moshi
import models.BoardSolutions
import models.SpellingBeeBoard
import okio.buffer
import okio.sink
import okio.source
import processor.UniqueCharSetPool
import timing
import java.io.File
import java.io.InputStream

class SimpleBoardSolutionsSerializer : BoardSolutionsSerializer {
    override fun serialize(boardSolutions: BoardSolutions, ucsPool: UniqueCharSetPool) = timing(
        message = "serialize all ${boardSolutions.size} solutions"
    ) {
        val solutionsJsonStr: String = solutionsToJsonString(boardSolutions, ucsPool)
        val resourceDirectory: File = File(ClassLoader.getSystemResource("").file)
        if (resourceDirectory.listFiles().none { it.name == "solutions.json" }) {
            File(resourceDirectory.path + "/solutions.json").createNewFile()
        }
        val solutionsFile = File(ClassLoader.getSystemResource("solutions.json").file)
        copyToFile(solutionsJsonStr.byteInputStream(), solutionsFile)
    }

    // oom here on toJson
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
}
