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

    // Set of all possible Spelling Bee UniqueCharSets. This contains all possible Spelling Bee boards as well all
    // subsets.
    val beeUcsSet: Set<UniqueCharSet> = beeWordSequence.map { UniqueCharSet(it) }.toSet()

    // All possible Spelling Bee boards. A subset of the beeUcsSet, filtering for only UniqueCharSets which a unique
    // count of 7 characters.
    val beeBoards: Set<UniqueCharSet> = beeUcsSet.filter { it.uniqueCount == 7 }.toSet()

    beeBoards.forEach { println(it) }

    println("There's ${beeBoards.size} possible boards")
}

