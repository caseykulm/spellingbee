import com.squareup.moshi.Moshi

private val moshi = Moshi.Builder().build()

fun main() {
    println("Spelling Bee booting up...")

    val dictionaryTool = DictionaryToolImpl(moshi)

    // Map of Webster's Dictionary words to definitions. We mostly won't use the definitions, but they will be here
    // if desired.
    val dictionary: Map<String, String> = dictionaryTool.getDictionary()

    // Create solutions
//    val beeBoardsSolutionMap: Map<SpellingBeeBoard, Set<String>> = createAllSolutions(dictionary)

    // Write solutions
//    writeSolutionsToDisk(beeBoardsSolutionMap)

    // Read solutions
    val beeBoardsSolutionMap: Map<SpellingBeeBoard, Set<String>> = readSolutionFromDisk()
        ?: error("Could not read solutions")

    // Solve some board
    val filterBoard = SpellingBeeBoard(ucs = UniqueCharSet("aklmnow"), centerChar = 'n')
    solveBoard(beeBoardsSolutionMap, filterBoard)
}

