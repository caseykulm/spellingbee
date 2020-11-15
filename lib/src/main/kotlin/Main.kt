import com.squareup.moshi.Moshi
import models.Dictionary
import models.SpellingBeeBoard
import models.UniqueCharSet
import parser.DictionaryParser
import parser.SimpleMapDictionaryParser
import parser.WordPerLineParser
import persister.readSolutionFromDisk
import persister.writeSolutionsToDisk
import processor.BoardSolutionsProcessor
import processor.SimpleBoardSolutionProcessor
import processor.SimpleUniqueCharSetPool
import processor.UniqueCharSetPool

fun main() {
    println("Spelling Bee booting up...")

    val ucsPool = SimpleUniqueCharSetPool()
    val dictionary: Map<String, String> = getDictionary(DictionarySource.ReaganWebster)

    val mode = Mode.Write
    val beeBoardsSolutionMap: Map<SpellingBeeBoard, Set<String>> = getBeeBoardsSolutionMap(mode, dictionary, ucsPool)

    // Solve some board
    val filterBoard = SpellingBeeBoard(ucs = UniqueCharSet("gdprona"), centerChar = 'o')
    solveBoard(beeBoardsSolutionMap, filterBoard, ucsPool)
}

enum class Mode {
    Read, Write
}

private fun getBeeBoardsSolutionMap(
    mode: Mode,
    dictionary: Dictionary,
    ucsPool: UniqueCharSetPool
): Map<SpellingBeeBoard, Set<String>> {
    return when (mode) {
        Mode.Read -> {
            // Read solutions
            readSolutionFromDisk(ucsPool) ?: error("Could not read solutions")
        }
        Mode.Write -> {
            // Create solutions
            val boardSolutionsProcessor: BoardSolutionsProcessor = SimpleBoardSolutionProcessor(ucsPool = ucsPool, verboseLogging = false)
            val beeBoardsSolutionMap: Map<SpellingBeeBoard, Set<String>> = boardSolutionsProcessor.process(dictionary)

            // Write solutions
            writeSolutionsToDisk(beeBoardsSolutionMap, ucsPool)

            beeBoardsSolutionMap
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
        DictionarySource.ReaganWebster -> getResourceFileName(dictionarySource.javaClass.classLoader, "reagan_webster_dictionary.json")
        DictionarySource.Google10k -> getResourceFileName(dictionarySource.javaClass.classLoader, "google-10000-english.txt")
        DictionarySource.Novig333k -> getResourceFileName(dictionarySource.javaClass.classLoader, "norvig_count_1w_no_freq.txt")
    }
}

private fun getResourceFileName(classLoader: ClassLoader, resourceName: String): String = classLoader.getResource(resourceName).file
