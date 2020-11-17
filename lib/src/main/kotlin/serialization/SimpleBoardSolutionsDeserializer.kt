package serialization

import com.squareup.moshi.Moshi
import models.BoardSolutions
import models.SpellingBeeBoard
import okio.buffer
import okio.source
import processor.UniqueCharSetPool
import timing
import java.io.File

class SimpleBoardSolutionsDeserializer : BoardSolutionsDeserializer {
    override fun deserialize(ucsPool: UniqueCharSetPool): BoardSolutions = timing(
        message = "deserialize all solutions"
    ) {
        val solutionsFile = File(ClassLoader.getSystemResource("solutions.json").file)

        return@timing Moshi.Builder().add(SpellingBeeBoardAdapter(ucsPool)).build()
            .adapter<Map<SpellingBeeBoard, Set<String>>>(solutionsMapType)
            .fromJson(solutionsFile.source().buffer())!!
    }
}
