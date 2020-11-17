import com.squareup.moshi.Moshi
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
import serialization.SimpleBoardSolutionsDeserializer
import serialization.SimpleBoardSolutionsSerializer

fun main() {
    println("Spelling Bee booting up...")

    val ucsPool = SimpleUniqueCharSetPool()
    val dictionary: Map<String, String> = getDictionary(DictionarySource.Google10k)

    val mode = Mode.Write
    val serializationMethod = SerializationMethod.Simple
    val beeBoardsSolutionMap: Map<SpellingBeeBoard, Set<String>> =
        getBeeBoardsSolutionMap(mode, serializationMethod, dictionary, ucsPool)

    // Solve some board
    val filterBoard = SpellingBeeBoard(
        ucs = UniqueCharSet("history"),
        centerChar = 'i'
    )
    solveBoard(beeBoardsSolutionMap, filterBoard, ucsPool)
}

enum class Mode {
    Read, Write
}

enum class SerializationMethod {
    Simple, Buffered
}

private fun getBeeBoardsSolutionMap(
    mode: Mode,
    serializationMethod: SerializationMethod,
    dictionary: Dictionary,
    ucsPool: UniqueCharSetPool
): Map<SpellingBeeBoard, Set<String>> {
    return when (mode) {
        Mode.Read -> when (serializationMethod) {
            SerializationMethod.Simple -> {
                val boardSolutionsDeserializer = SimpleBoardSolutionsDeserializer()
                boardSolutionsDeserializer.deserialize(ucsPool)
            }
            SerializationMethod.Buffered -> TODO()
        }
        Mode.Write -> when (serializationMethod) {
            SerializationMethod.Simple -> {
                // Create solutions
                val boardSolutionsProcessor: BoardSolutionsProcessor = SimpleBoardSolutionProcessor(ucsPool = ucsPool, verboseLogging = false)
                val beeBoardsSolutionMap: Map<SpellingBeeBoard, Set<String>> = boardSolutionsProcessor.process(dictionary)

                // Write solutions
                val boardSolutionsPersister = SimpleBoardSolutionsSerializer()
                boardSolutionsPersister.serialize(beeBoardsSolutionMap, ucsPool)

                beeBoardsSolutionMap
            }
            SerializationMethod.Buffered -> TODO()
        }
    }
}

enum class DictionarySource {
    ReaganWebster,
    Google10k,
    Novig333k,
}

private fun getDictionary(dictionarySource: DictionarySource): Dictionary =
    getDictionaryParser(dictionarySource).parse(getDictionaryFileName(dictionarySource))

private fun getDictionaryParser(dictionarySource: DictionarySource): DictionaryParser {
    return when (dictionarySource) {
        DictionarySource.ReaganWebster -> SimpleMapDictionaryParser(Moshi.Builder().build())
        DictionarySource.Google10k -> WordPerLineParser()
        DictionarySource.Novig333k -> WordPerLineParser()
    }
}

private fun getDictionaryFileName(dictionarySource: DictionarySource): String {
    return when (dictionarySource) {
        DictionarySource.ReaganWebster -> getResourceFileName("reagan_webster_dictionary.json")
        DictionarySource.Google10k -> getResourceFileName("google-10000-english.txt")
        DictionarySource.Novig333k -> getResourceFileName("norvig_count_1w_no_freq.txt")
    }
}

private fun getResourceFileName(resourceName: String): String = ClassLoader.getSystemResource(resourceName).file
