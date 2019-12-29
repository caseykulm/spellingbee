fun main() {
    println("Spelling Bee booting up...")

    val dictionaryTool = DictionaryToolImpl()

    // Map of Webster's Dictionary words to definitions. We mostly won't use the definitions, but they will be here
    // if desired.
    val dictionary: Map<String, String> = dictionaryTool.getDictionary()

    val beeBoardsSolutionMap = createAllSolutions(dictionary)

    val filterBoard = SpellingBeeBoard(ucs = UniqueCharSet("dangrul"), centerChar = 'd')
    val filterSolutions: Set<String> = beeBoardsSolutionMap[filterBoard]!!
    val solutionsBySize: Map<Int, List<String>> = filterSolutions.groupBy { UniqueCharSet(it).uniqueCount }
    println("Solutions for $filterBoard are: $filterSolutions")
    println("$solutionsBySize")
}

