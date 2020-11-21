import com.squareup.moshi.Moshi
import models.BoardSolutions
import models.Dictionary
import models.SpellingBeeBoard
import models.UniqueCharSet
import parser.DictionaryParser
import parser.SimpleMapDictionaryParser
import parser.WordPerLineParser
import processor.BoardSolutionsProcessor
import processor.SimpleBoardSolutionProcessor
import processor.SimpleUniqueCharSetPool
import processor.UniqueCharSetPool
import serialization.BufferedBoardSolutionsSerializer
import serialization.SimpleBoardSolutionsDeserializer
import serialization.SimpleBoardSolutionsSerializer

fun main() {
    println("Spelling Bee booting up...")

    val ucsPool = SimpleUniqueCharSetPool()
    val dictionarySource = DictionarySource.Novig333k
    val dictionary: Map<String, String> = getDictionary(dictionarySource)

    val mode = Mode.Write
    val serializationMethod = SerializationMethod.Database
    val beeBoardsSolutionMap: Map<SpellingBeeBoard, Set<String>> =
        getBeeBoardsSolutionMap(mode, serializationMethod, dictionary, dictionarySource, ucsPool)

    // Solve some board
    val filterBoard = SpellingBeeBoard(
        ucs = UniqueCharSet("amtolbh"),
        centerChar = 'o'
    )
    solveBoard(beeBoardsSolutionMap, filterBoard, ucsPool)
}

enum class Mode {
    Read, Write
}

enum class SerializationMethod {
    Json, Database
}

private fun getBeeBoardsSolutionMap(
    mode: Mode,
    serializationMethod: SerializationMethod,
    dictionary: Dictionary,
    dictionarySource: DictionarySource,
    ucsPool: UniqueCharSetPool
): Map<SpellingBeeBoard, Set<String>> {
    return when (mode) {
        Mode.Read -> when (serializationMethod) {
            SerializationMethod.Json -> {
                val boardSolutionsDeserializer = SimpleBoardSolutionsDeserializer()
                boardSolutionsDeserializer.deserialize(ucsPool)
            }
            SerializationMethod.Database -> TODO()
        }
        Mode.Write -> when (serializationMethod) {
            SerializationMethod.Json -> {
                val boardSolutions: BoardSolutions = createSolutions(ucsPool, dictionary)

                // Write solutions
                val boardSolutionsPersister = SimpleBoardSolutionsSerializer()
                boardSolutionsPersister.serialize(boardSolutions, ucsPool)

                boardSolutions
            }
            SerializationMethod.Database -> {
                val boardSolutions: BoardSolutions = createSolutions(ucsPool, dictionary)

                val boardSolutionsPersister = BufferedBoardSolutionsSerializer(dictionarySource.name.toLowerCase())
                boardSolutionsPersister.serialize(boardSolutions, ucsPool)

                boardSolutions
            }
        }
    }
}

enum class DictionarySource {
    ReaganWebster,
    Google10k,
    Novig333k,
    Dwyl
}

private fun createSolutions(ucsPool: UniqueCharSetPool, dictionary: Dictionary): BoardSolutions {
    // Create solutions
    val boardSolutionsProcessor: BoardSolutionsProcessor =
        SimpleBoardSolutionProcessor(ucsPool = ucsPool, verboseLogging = false)
    return boardSolutionsProcessor.process(dictionary)
}

private fun getDictionary(dictionarySource: DictionarySource): Dictionary =
    getDictionaryParser(dictionarySource).parse(getDictionaryFileName(dictionarySource))

private fun getDictionaryParser(dictionarySource: DictionarySource): DictionaryParser {
    return when (dictionarySource) {
        DictionarySource.ReaganWebster -> SimpleMapDictionaryParser(Moshi.Builder().build())
        DictionarySource.Google10k -> WordPerLineParser(300000)
        DictionarySource.Novig333k -> WordPerLineParser(300000)
        DictionarySource.Dwyl -> WordPerLineParser(500000)
    }
}

private fun getDictionaryFileName(dictionarySource: DictionarySource): String {
    return when (dictionarySource) {
        DictionarySource.ReaganWebster -> getResourceFileName("reagan_webster_dictionary.json")
        DictionarySource.Google10k -> getResourceFileName("google-10000-english.txt")
        DictionarySource.Novig333k -> getResourceFileName("norvig_count_1w_no_freq.txt")
        DictionarySource.Dwyl -> getResourceFileName("dwyl_words_alpha.txt")
    }
}

private fun getResourceFileName(resourceName: String): String = ClassLoader.getSystemResource(resourceName).file
