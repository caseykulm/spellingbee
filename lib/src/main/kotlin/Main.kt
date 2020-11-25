import com.squareup.moshi.Moshi
import models.BoardSolutionQuery
import models.BoardSolutions
import models.Dictionary
import models.SpellingBeeBoard
import models.UniqueCharSet
import models.uniqueCount
import parser.DictionaryParser
import parser.SimpleMapDictionaryParser
import parser.WordPerLineParser
import processor.BoardSolutionsProcessor
import processor.SimpleBoardSolutionProcessor
import processor.SimpleUniqueCharSetPool
import processor.UniqueCharSetPool
import serialization.DatabaseBoardSolutionQuery
import serialization.DatabaseBoardSolutionsSerializer

fun main() {
    println("Spelling Bee booting up...")

    val ucsPool = SimpleUniqueCharSetPool()
    val dictionarySource = DictionarySource.ReaganWebster
    val dictionary: Map<String, String> = getDictionary(dictionarySource)

    val mode = Mode.Read
    val serializationMethod = SerializationMethod.Database
    val boardSolutionQuery: BoardSolutionQuery = getBeeBoardsSolutionQuery(
        mode,
        serializationMethod,
        dictionary,
        dictionarySource,
        ucsPool
    )

    solveSampleBoards(boardSolutionQuery)
    ucsPool.logHitMissRate()
}

private fun solveSampleBoards(boardSolutionQuery: BoardSolutionQuery) {
    sampleBoards.forEach { sampleBoard ->
        val boardSolutions: Set<String> = boardSolutionQuery.getSolutions(sampleBoard)
        println("Solutions for $sampleBoard are: $boardSolutions")
        val pangrams: Set<String> = boardSolutions.filter { UniqueCharSet(it).uniqueCount == 7 }.toSet()
        println("\tPangrams for $sampleBoard are: $pangrams")
    }
}

val sampleBoards: List<SpellingBeeBoard> = listOf(
    // mannequin
    SpellingBeeBoard(UniqueCharSet("aeimnqu"), 'm'),

    // friction
    SpellingBeeBoard(UniqueCharSet("cfinort"), 'i'),

    // guilted
    SpellingBeeBoard(UniqueCharSet("degiltu"), 'g')
)

enum class Mode {
    Read, Write
}

enum class SerializationMethod {
    Json, Database
}

private fun getBeeBoardsSolutionQuery(
    mode: Mode,
    serializationMethod: SerializationMethod,
    dictionary: Dictionary,
    dictionarySource: DictionarySource,
    ucsPool: UniqueCharSetPool
): BoardSolutionQuery {
    return when (mode) {
        Mode.Read -> when (serializationMethod) {
            SerializationMethod.Json -> TODO()
            SerializationMethod.Database -> {
                DatabaseBoardSolutionQuery(dictionarySource.name.toLowerCase())
            }
        }
        Mode.Write -> when (serializationMethod) {
            SerializationMethod.Json -> TODO()
            SerializationMethod.Database -> {
                val boardSolutionQuery = DatabaseBoardSolutionQuery(dictionarySource.name.toLowerCase())
                val boardSolutions: BoardSolutions = createSolutions(ucsPool, dictionary)

                val boardSolutionsPersister = DatabaseBoardSolutionsSerializer(dictionarySource.name.toLowerCase())
                boardSolutionsPersister.serialize(boardSolutions, ucsPool)

                boardSolutions
                boardSolutionQuery
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
