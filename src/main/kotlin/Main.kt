import com.squareup.moshi.Moshi
import models.SpellingBeeBoard
import parser.DictionaryParser
import parser.SimpleMapDictionaryParser
import processor.BoardSolutionsProcessor
import processor.SimpleBoardSolutionProcessor
import processor.SimpleUniqueCharSetPool

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

    // Create solutions
    val boardSolutionsProcessor: BoardSolutionsProcessor = SimpleBoardSolutionProcessor(ucsPool = ucsPool, verboseLogging = false)
    val beeBoardsSolutionMap: Map<SpellingBeeBoard, Set<String>> = boardSolutionsProcessor.process(dictionary)

    // Write solutions
    writeSolutionsToDisk(beeBoardsSolutionMap, ucsPool)

    // Read solutions
//    val beeBoardsSolutionMap: Map<models.SpellingBeeBoard, Set<String>> = readSolutionFromDisk()
//        ?: error("Could not read solutions")

    // Solve some board
//    val filterBoard = models.SpellingBeeBoard(ucs = UniqueCharSet("flatdwo"), centerChar = 't') // no solution for this
    val filterBoard = SpellingBeeBoard(ucs = UniqueCharSet("iuontcm"), centerChar = 't')
    solveBoard(beeBoardsSolutionMap, filterBoard, ucsPool)
}

