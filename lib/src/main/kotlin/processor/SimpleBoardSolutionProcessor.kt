package processor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import models.BoardSolutionEntry
import models.BoardSolutions
import models.Dictionary
import models.SpellingBeeBoard
import models.SpellingBeeFilterRule
import models.UniqueCharSet
import models.uniqueCharSubsets
import models.uniqueCount
import models.words
import timing

class SimpleBoardSolutionProcessor(
    private val ucsPool: UniqueCharSetPool,
    private val verboseLogging: Boolean
) : BoardSolutionsProcessor {
    override fun process(dictionary: Dictionary): BoardSolutions {
        val beeWordSequence = mapToBeeWordSequence(dictionary)
        val beeUcsSet: Set<UniqueCharSet> = mapToBeeUcsSet(beeWordSequence)
        val beeUcsToWordsMap: Map<UniqueCharSet, List<String>> = mapToBeeUcsToWordMap(beeWordSequence, ucsPool)
        val sevenUcsSet: Set<UniqueCharSet> = mapToSevenUcsSet(beeUcsSet)
        val beeBoardsWithCenter: List<SpellingBeeBoard> = mapToBeeBoardsWithCenter(sevenUcsSet)

        return mapToBeeBoardsSolutionsMap(beeBoardsWithCenter, beeUcsToWordsMap)
    }

    /**
     * Sequence of words that are valid entry's for a Spelling Bee game.
     */
    private fun mapToBeeWordSequence(
        dictionary: Dictionary
    ) = timing(message = "map dictionary to beeWordSequence") {
        // Sequence of Webster's Dictionary words. This is the most simple dataset that we will modify.
        val wordSequence: Sequence<String> = dictionary.words.asSequence()

        val beeWordSequence = wordSequence
            .filter { SpellingBeeFilterRule.AlphaOnly.predicate(it, ucsPool) }
            .filter { SpellingBeeFilterRule.Length4OrGreater.predicate(it, ucsPool) }
            .filter { SpellingBeeFilterRule.SevenOrFewerUnique.predicate(it, ucsPool) }

        if (verboseLogging) {
            println("There are ${beeWordSequence.count()} valid Spelling Bee words")
        }

        return@timing beeWordSequence
    }

    /**
     * Set of all possible Spelling Bee UniqueCharSets. This contains all possible Spelling Bee boards as well all subsets.
     */
    private fun mapToBeeUcsSet(
        beeWordSequence: Sequence<String>
    ) = timing(message = "map beeWordSequence to beeUcsSet") {
        val beeUcsSet: Set<UniqueCharSet> = beeWordSequence.map { ucsPool.getOrCreateUniqueCharSet(it) }.toSet()

        if (verboseLogging) {
            println("There are ${beeUcsSet.count()} valid Spelling Bee UniqueCharSets. (including subsets)")
        }

        return@timing beeUcsSet
    }

    /**
     * Set of all possible Spelling Bee UCSs to a list of words containing exactly those unique characters
     */
    private fun mapToBeeUcsToWordMap(
        beeWordSequence: Sequence<String>,
        ucsPool: UniqueCharSetPool
    ) = timing(message = "map beeWordSequence and ucsPool to beeUcsToWordMap") {
        val beeUcsToWordsMap: Map<UniqueCharSet, List<String>> = beeWordSequence.groupBy { ucsPool.getOrCreateUniqueCharSet(it) }

        if (verboseLogging) {
            beeUcsToWordsMap.entries.take(5).forEach { println(it) }
        }

        return@timing beeUcsToWordsMap
    }

    /**
     * All possible seven UniqueCharSets for a Spelling Bee board. This does not take into account a center character.
     */
    private fun mapToSevenUcsSet(
        beeUcsSet: Set<UniqueCharSet>
    ) = timing(message = "map beeUcsSet to sevenUcsSet") {
        val sevenUcsSet: Set<UniqueCharSet> = beeUcsSet.filter { it.uniqueCount == 7 }.toSet()

        if (verboseLogging) {
            println("There are ${sevenUcsSet.size} valid 7-UniqueCharSets")
        }

        return@timing sevenUcsSet
    }

    /**
     * All possible Spelling Bee Boards
     */
    private fun mapToBeeBoardsWithCenter(
        sevenUcsSet: Set<UniqueCharSet>
    ) = timing(message = "map sevenUcsSet to beeBoardsWithCenter") {
        val beeBoardsWithCenter: List<SpellingBeeBoard> = sevenUcsSet.flatMap { ucs ->
            IntRange(0, ucs.uniqueCount - 1).map { index ->
                SpellingBeeBoard(ucs, ucs.uniqueChars.elementAt(index))
            }
        }

        if (verboseLogging) {
            println("There are ${beeBoardsWithCenter.size} valid Spelling Bee Boards")
            beeBoardsWithCenter.take(14).forEach { println(it) }
        }

        return@timing beeBoardsWithCenter
    }

    private fun mapToBeeBoardsSolutionsMap(
        beeBoardsWithCenter: List<SpellingBeeBoard>,
        beeUcsToWordsMap: Map<UniqueCharSet, List<String>>
    ) = timing(message = "find all solutions") {
        return@timing runBlocking(Dispatchers.Default) {
            val boardSolutionList = beeBoardsWithCenter.map { beeBoard ->
                async { mapToBeeBoardSolution(beeUcsToWordsMap, beeBoard) }
            }.awaitAll()

            val boardSolutionTypedArray = boardSolutionList.toTypedArray()

            return@runBlocking mapOf(*boardSolutionTypedArray)
        }
    }

    private fun mapToBeeBoardSolution(
        beeUcsToWordsMap: Map<UniqueCharSet, List<String>>,
        beeBoard: SpellingBeeBoard
    ): BoardSolutionEntry {
        val allWords = beeBoard.ucs.uniqueCharSubsets(ucsPool)
            .filter { subUcs -> subUcs.uniqueCount >= 4 }
            .filter { subUcs -> subUcs.contains(beeBoard.centerChar) }
            .flatMap { subUcs -> beeUcsToWordsMap[subUcs]?.asIterable() ?: emptyList() }
            .toSet()

        return beeBoard to allWords
    }
}
