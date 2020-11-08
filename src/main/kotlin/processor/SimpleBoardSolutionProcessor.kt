package processor

import UniqueCharSet
import models.BoardSolution
import models.BoardSolutions
import models.Dictionary
import models.SpellingBeeBoard
import models.SpellingBeeFilterRule
import models.words
import uniqueCharSubsets
import uniqueCount

class SimpleBoardSolutionProcessor(
    private val ucsPool: UniqueCharSetPool,
    private val verboseLogging: Boolean
) : BoardSolutionsProcessor {
    override fun process(dictionary: Dictionary): BoardSolutions {
        val beeWordSequence = createBeeWordSequence(dictionary)
        val beeUcsSet: Set<UniqueCharSet> = mapToBeeUcsSet(beeWordSequence)
        val beeUcsToWordsMap: Map<UniqueCharSet, List<String>> = mapToBeeUcsToWordMap(beeWordSequence, ucsPool)
        val sevenUcsSet: Set<UniqueCharSet> = mapToSevenUcsSet(beeUcsSet)
        val beeBoardsWithCenter: List<SpellingBeeBoard> = mapToBeeBoardsWithCenter(sevenUcsSet)

        return mapToBeeBoardsSolutionsMap(beeBoardsWithCenter, beeUcsToWordsMap)
    }

    /**
     * Sequence of words that are valid entry's for a Spelling Bee game.
      */
    private fun createBeeWordSequence(dictionary: Dictionary): Sequence<String> {
        // Sequence of Webster's Dictionary words. This is the most simple dataset that we will modify.
        val wordSequence: Sequence<String> = dictionary.words.asSequence()

        val beeWordSequence = wordSequence
            .filter { SpellingBeeFilterRule.AlphaOnly.predicate(it, ucsPool) }
            .filter { SpellingBeeFilterRule.Length4OrGreater.predicate(it, ucsPool) }
            .filter { SpellingBeeFilterRule.SevenOrFewerUnique.predicate(it, ucsPool) }

        if (verboseLogging) {
            println("There are ${beeWordSequence.count()} valid Spelling Bee words")
        }

        return beeWordSequence
    }

    /**
     * Set of all possible Spelling Bee UniqueCharSets. This contains all possible Spelling Bee boards as well all subsets.
     */
    private fun mapToBeeUcsSet(beeWordSequence: Sequence<String>): Set<UniqueCharSet> {
        val beeUcsSet: Set<UniqueCharSet> = beeWordSequence.map { ucsPool.getOrCreateUniqueCharSet(it) }.toSet()

        if (verboseLogging) {
            println("There are ${beeUcsSet.count()} valid Spelling Bee UniqueCharSets. (including subsets)")
        }

        return beeUcsSet
    }

    /**
     * Set of all possible Spelling Bee UCSs to a list of words containing exactly those unique characters
     */
    private fun mapToBeeUcsToWordMap(
        beeWordSequence: Sequence<String>,
        ucsPool: UniqueCharSetPool
    ): Map<UniqueCharSet, List<String>> {
        val beeUcsToWordsMap: Map<UniqueCharSet, List<String>> = beeWordSequence.groupBy { ucsPool.getOrCreateUniqueCharSet(it) }

        if (verboseLogging) {
            beeUcsToWordsMap.entries.take(5).forEach { println(it) }
        }

        return beeUcsToWordsMap
    }

    /**
     * All possible seven UniqueCharSets for a Spelling Bee board. This does not take into account a center character.
     */
    private fun mapToSevenUcsSet(beeUcsSet: Set<UniqueCharSet>): Set<UniqueCharSet> {
        val sevenUcsSet: Set<UniqueCharSet> = beeUcsSet.filter { it.uniqueCount == 7 }.toSet()

        if (verboseLogging) {
            println("There are ${sevenUcsSet.size} valid 7-UniqueCharSets")
        }

        return sevenUcsSet
    }

    /**
     * All possible Spelling Bee Boards
     */
    private fun mapToBeeBoardsWithCenter(sevenUcsSet: Set<UniqueCharSet>): List<SpellingBeeBoard> {
        val beeBoardsWithCenter: List<SpellingBeeBoard> = sevenUcsSet.flatMap { ucs ->
            IntRange(0, ucs.uniqueCount - 1).map { index ->
                SpellingBeeBoard(ucs, ucs.uniqueChars.elementAt(index))
            }
        }

        if (verboseLogging) {
            println("There are ${beeBoardsWithCenter.size} valid Spelling Bee Boards")
            beeBoardsWithCenter.take(14).forEach { println(it) }
        }

        return beeBoardsWithCenter
    }

    private fun mapToBeeBoardsSolutionsMap(
        beeBoardsWithCenter: List<SpellingBeeBoard>,
        beeUcsToWordsMap: Map<UniqueCharSet, List<String>>
    ): BoardSolutions {
        val startTime = System.currentTimeMillis()

        // Kind of hacky
        val beeBoardsSolutionMap: BoardSolutions = beeBoardsWithCenter.map {
            mapToBeeBoardSolution(beeUcsToWordsMap, it)
        }.toMap()

        val totalTime = System.currentTimeMillis() - startTime
        println("It took $totalTime ms to find all ${beeBoardsSolutionMap.size} solutions")

        return beeBoardsSolutionMap
    }

    private fun mapToBeeBoardSolution(
        beeUcsToWordsMap: Map<UniqueCharSet, List<String>>,
        beeBoard: SpellingBeeBoard
    ): BoardSolution {
        val allWords = beeBoard.ucs.uniqueCharSubsets(ucsPool)
            .filter { subUcs -> subUcs.uniqueCount >= 4 }
            .filter { subUcs -> subUcs.contains(beeBoard.centerChar) }
            .flatMap { subUcs -> beeUcsToWordsMap[subUcs]?.asIterable() ?: emptyList() }
            .toSet()

        return beeBoard to allWords
    }
}