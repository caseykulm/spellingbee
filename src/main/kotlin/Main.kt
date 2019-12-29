fun main() {
    println("Spelling Bee booting up...")

    val dictionaryTool = DictionaryToolImpl()

    // Map of Webster's Dictionary words to definitions. We mostly won't use the definitions, but they will be here
    // if desired.
    val dictionary: Map<String, String> = dictionaryTool.getDictionary()

    // Sequence of Webster's Dictionary words. This is the most simple dataset that we will modify.
    val wordSequence: Sequence<String> = dictionary.keys.asSequence()

    // Sequence of words that are valid entry's for a Spelling Bee game.
    val beeWordSequence = wordSequence
        .applySpellingBeeRules(
            SpellingBeeFilterRule.AlphaOnly,
            SpellingBeeFilterRule.Length4OrGreater,
            SpellingBeeFilterRule.SevenOrFewerUnique
        )

    println("There are ${beeWordSequence.count()} valid Spelling Bee words")

    // Set of all possible Spelling Bee UniqueCharSets. This contains all possible Spelling Bee boards as well all
    // subsets.
    val beeUcsSet: Set<UniqueCharSet> = beeWordSequence.map { UniqueCharSet(it) }.toSet()

    println("There are ${beeUcsSet.count()} valid Spelling Bee UniqueCharSets. (including subsets)")

    // Set of all possible Spelling Bee UCSs to a list of words containing exactly those unique characters
    val beeUcsToWordsMap: Map<UniqueCharSet, List<String>> = beeWordSequence.groupBy { UniqueCharSet(it) }

    beeUcsToWordsMap.entries.take(5).forEach { println(it) }

    // All possible seven UniqueCharSets for a Spelling Bee board. This does not take into account a center character.
    val sevenUcsSet: Set<UniqueCharSet> = beeUcsSet.filter { it.uniqueCount == 7 }.toSet()

    println("There are ${sevenUcsSet.size} valid 7-UniqueCharSets")

    // All possible Spelling Bee Boards
    val beeBoardsWithCenter: List<SpellingBeeBoard> = sevenUcsSet.flatMap { ucs ->
        IntRange(0, ucs.uniqueCount - 1).map { index ->
            SpellingBeeBoard(ucs, ucs.uniqueChars.elementAt(index))
        }
    }

    println("There are ${beeBoardsWithCenter.size} valid Spelling Bee Boards")

    beeBoardsWithCenter.take(14).forEach { println(it) }

    val startTime = System.currentTimeMillis()

    // Kind of hacky
    val beeBoardsSolutionMap: Map<SpellingBeeBoard, Set<String>> = beeBoardsWithCenter.map { beeBoard ->
        val allWords = beeBoard.ucs.uniqueCharCombos
            .filter { set -> set.size >= 4 }
            .filter { set -> set.contains(beeBoard.centerChar) }
            .map { UniqueCharSet(it.joinToString(separator = "")) }
            .flatMap { ucs: UniqueCharSet -> beeUcsToWordsMap[ucs]?.asIterable() ?: emptyList() }
            .toSet()

        beeBoard to allWords
    }.toMap()

    val totalTime = System.currentTimeMillis() - startTime
    println("It took $totalTime ms to find all ${beeBoardsSolutionMap.size} solutions")

    val filterBoard = SpellingBeeBoard(ucs = UniqueCharSet("dangrul"), centerChar = 'd')
    val filterSolutions: Set<String> = beeBoardsSolutionMap[filterBoard]!!
    val solutionsBySize: Map<Int, List<String>> = filterSolutions.groupBy { UniqueCharSet(it).uniqueCount }
    println("Solutions for $filterBoard are: $filterSolutions")
    println("$solutionsBySize")
}

