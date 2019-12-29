enum class SpellingBeeFilterRule {
    AlphaOnly,
    Length4OrGreater,
    SevenOrFewerUnique
}

fun Sequence<String>.applySpellingBeeRules(
    vararg spellingBeeFilterRule: SpellingBeeFilterRule
): Sequence<String> {
    var seq = this
    spellingBeeFilterRule.forEach { rule ->
        seq = when (rule) {
            SpellingBeeFilterRule.AlphaOnly -> seq.filter(filterAlphaOnly)
            SpellingBeeFilterRule.Length4OrGreater -> seq.filter(filterLength4OrGreater)
            SpellingBeeFilterRule.SevenOrFewerUnique -> seq.filter(filterSevenOrFewerUnique)
        }
    }
    return seq
}

private val filterAlphaOnly = { word: String -> word.contains(Regex("^[A-z]+\$")) }
private val filterLength4OrGreater = { word: String -> word.length >= 4 }
private val filterSevenOrFewerUnique = { word: String -> UniqueCharSet(word).uniqueCount <= 7 }

class SpellingBeeBoard(val ucs: UniqueCharSet, val centerChar: Char) : Comparable<SpellingBeeBoard> {
    init {
        require(ucs.contains(centerChar))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpellingBeeBoard

        if (ucs != other.ucs) return false
        if (centerChar != other.centerChar) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ucs.hashCode()
        result = 31 * result + centerChar.hashCode()
        return result
    }

    override fun toString(): String {
        return "$ucs;$centerChar"
    }

    override fun compareTo(other: SpellingBeeBoard): Int {
        return toString().compareTo(other.toString())
    }
}

fun createAllSolutions(dictionary: Map<String, String>): Map<SpellingBeeBoard, Set<String>> {
    // Sequence of Webster's Dictionary words. This is the most simple dataset that we will modify.
    val wordSequence: Sequence<String> = dictionary.keys.asSequence()

    // Sequence of words that are valid entry's for a Spelling Bee game.
    val beeWordSequence = wordSequence
        .applySpellingBeeRules(
            SpellingBeeFilterRule.AlphaOnly,
            SpellingBeeFilterRule.Length4OrGreater,
            SpellingBeeFilterRule.SevenOrFewerUnique
        )

//    println("There are ${beeWordSequence.count()} valid Spelling Bee words")

    // Set of all possible Spelling Bee UniqueCharSets. This contains all possible Spelling Bee boards as well all
    // subsets.
    val beeUcsSet: Set<UniqueCharSet> = beeWordSequence.map { UniqueCharSet(it) }.toSet()

//    println("There are ${beeUcsSet.count()} valid Spelling Bee UniqueCharSets. (including subsets)")

    // Set of all possible Spelling Bee UCSs to a list of words containing exactly those unique characters
    val beeUcsToWordsMap: Map<UniqueCharSet, List<String>> = beeWordSequence.groupBy { UniqueCharSet(it) }

//    beeUcsToWordsMap.entries.take(5).forEach { println(it) }

    // All possible seven UniqueCharSets for a Spelling Bee board. This does not take into account a center character.
    val sevenUcsSet: Set<UniqueCharSet> = beeUcsSet.filter { it.uniqueCount == 7 }.toSet()

//    println("There are ${sevenUcsSet.size} valid 7-UniqueCharSets")

    // All possible Spelling Bee Boards
    val beeBoardsWithCenter: List<SpellingBeeBoard> = sevenUcsSet.flatMap { ucs ->
        IntRange(0, ucs.uniqueCount - 1).map { index ->
            SpellingBeeBoard(ucs, ucs.uniqueChars.elementAt(index))
        }
    }

//    println("There are ${beeBoardsWithCenter.size} valid Spelling Bee Boards")

//    beeBoardsWithCenter.take(14).forEach { println(it) }

    val startTime = System.currentTimeMillis()

    // Kind of hacky
    val beeBoardsSolutionMap: Map<SpellingBeeBoard, Set<String>> = beeBoardsWithCenter.map { beeBoard ->
        val allWords = beeBoard.ucs.uniqueCharSubsets
            .filter { subUcs -> subUcs.uniqueCount >= 4 }
            .filter { subUcs -> subUcs.contains(beeBoard.centerChar) }
            .flatMap { subUcs -> beeUcsToWordsMap[subUcs]?.asIterable() ?: emptyList() }
            .toSet()

        beeBoard to allWords
    }.toMap()

    val totalTime = System.currentTimeMillis() - startTime
    println("It took $totalTime ms to find all ${beeBoardsSolutionMap.size} solutions")

    return beeBoardsSolutionMap
}
