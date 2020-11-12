import com.squareup.moshi.Moshi
import models.Dictionary
import models.SpellingBeeBoard
import models.UniqueCharSet
import parser.DictionaryParser
import parser.SimpleMapDictionaryParser
import persister.readSolutionFromDisk
import persister.writeSolutionsToDisk
import processor.BoardSolutionsProcessor
import processor.SimpleBoardSolutionProcessor
import processor.SimpleUniqueCharSetPool
import processor.UniqueCharSetPool

private val moshi = Moshi.Builder().build()

fun main() {
    println("Spelling Bee booting up...")

    val ucsPool = SimpleUniqueCharSetPool()
    val simpleMapDictionaryAdapter: DictionaryParser = SimpleMapDictionaryParser(moshi)

    // Map of Webster's Dictionary words to definitions. We mostly won't use the definitions, but they will be here
    // if desired.
    val dictionaryFileName = simpleMapDictionaryAdapter.javaClass
        .classLoader
        .getResource("dictionary.json")
        .file
    val dictionary: Map<String, String> = simpleMapDictionaryAdapter.parse(dictionaryFileName)

    val mode = Mode.Write
    val beeBoardsSolutionMap: Map<SpellingBeeBoard, Set<String>> = getBeeBoardsSolutionMap(mode, dictionary, ucsPool)

    // Solve some board
//    val filterBoard = models.SpellingBeeBoard(ucs = UniqueCharSet("flatdwo"), centerChar = 't') // no solution for this
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

