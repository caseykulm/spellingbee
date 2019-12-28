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

    // All possible Spelling Bee boards. A Spelling Bee board is a Spelling Bee UniqueCharSet with 7 unique characters.
    val beeBoards: Set<UniqueCharSet> = beeUcsSet.filter { it.uniqueCount == 7 }.toSet()

//    beeBoards.forEach { println(it) }

    // TODO: Generate a mapping of Spelling Bee boards to a list of all correct words for board.

    println("There are ${beeBoards.size} valid Spelling Bee boards")
}

