fun main() {
    println("Spelling Bee booting up...")

    val dictionaryTool = DictionaryToolImpl()
    val dictionary: Map<String, String> = dictionaryTool.getDictionary()
    val wordSequence: Sequence<String> = dictionary.keys.asSequence()

    val beeWordSequence = wordSequence
        .applySpellingBeeRules(
            SpellingBeeFilterRule.AlphaOnly,
            SpellingBeeFilterRule.Length4OrGreater,
            SpellingBeeFilterRule.SevenOrFewerUnique
        )

//    val uniqueCharacterToWordsMap = beeWordSequence.groupBy {
//        UniqueCharacterSet(it)
//    }

    val ucsToWordMap = mutableMapOf<UniqueCharSet, MutableList<String>>()

    beeWordSequence.forEach { word ->
        val ucs = UniqueCharSet(word)
//        ucs.uniqueChars
        var wordList: MutableList<String>? = ucsToWordMap[ucs]

        if (wordList == null) {
            wordList = mutableListOf()
            ucsToWordMap[ucs] = wordList
        }

        wordList.add(word)
    }

    val beeUcsToWordMap = ucsToWordMap.filterKeys { it.uniqueCount == 7 }.toMutableMap()

    beeUcsToWordMap.keys.forEach { beeUcs ->
        // TODO: Form all subset UCS's possible,
        //  get their words from ucsToWordMap,
        //  add them to the lists for the beeUcs'
    }

//    ucsToWordMap.entries.take(100000).forEach { println("${it.key} : ${it.value}") }
    beeUcsToWordMap.entries.take(1).forEach { println("${it.key.uniqueCharCombos} : ${it.value}") }

    println("There's ${beeUcsToWordMap.size} possible sets")
}

