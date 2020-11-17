package processor

import com.caseykulm.spellingbee.proto.ProtoBoardSolution
import com.caseykulm.spellingbee.proto.ProtoBoardSolutions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import models.Dictionary
import models.SpellingBeeBoard
import models.SpellingBeeFilterRule
import models.UniqueCharSet
import models.uniqueCharSubsets
import models.uniqueCount
import models.words
import timing

class SimpleProtoBoardSolutionsProcessor(
    private val ucsPool: UniqueCharSetPool
) : ProtoBoardSolutionsProcessor {
    override fun process(dictionary: Dictionary): ProtoBoardSolutions {
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

        return@timing beeWordSequence
    }

    /**
     * Set of all possible Spelling Bee UniqueCharSets. This contains all possible Spelling Bee boards as well all subsets.
     */
    private fun mapToBeeUcsSet(
        beeWordSequence: Sequence<String>
    ) = timing(message = "map beeWordSequence to beeUcsSet") {
        return@timing beeWordSequence.map { ucsPool.getOrCreateUniqueCharSet(it) }.toSet()
    }

    /**
     * Set of all possible Spelling Bee UCSs to a list of words containing exactly those unique characters
     */
    private fun mapToBeeUcsToWordMap(
        beeWordSequence: Sequence<String>,
        ucsPool: UniqueCharSetPool
    ) = timing(message = "map beeWordSequence and ucsPool to beeUcsToWordMap") {
        return@timing beeWordSequence.groupBy { ucsPool.getOrCreateUniqueCharSet(it) }
    }

    /**
     * All possible seven UniqueCharSets for a Spelling Bee board. This does not take into account a center character.
     */
    private fun mapToSevenUcsSet(
        beeUcsSet: Set<UniqueCharSet>
    ) = timing(message = "map beeUcsSet to sevenUcsSet") {
        return@timing beeUcsSet.filter { it.uniqueCount == 7 }.toSet()
    }

    /**
     * All possible Spelling Bee Boards
     */
    private fun mapToBeeBoardsWithCenter(
        sevenUcsSet: Set<UniqueCharSet>
    ) = timing(message = "map sevenUcsSet to beeBoardsWithCenter") {
        return@timing sevenUcsSet.flatMap { ucs ->
            IntRange(0, ucs.uniqueCount - 1).map { index ->
                SpellingBeeBoard(ucs, ucs.uniqueChars.elementAt(index))
            }
        }
    }

    private fun mapToBeeBoardsSolutionsMap(
        beeBoardsWithCenter: List<SpellingBeeBoard>,
        beeUcsToWordsMap: Map<UniqueCharSet, List<String>>
    ) = timing(message = "find all solutions") {
        return@timing runBlocking(Dispatchers.Default) {
            val boardSolutionList = beeBoardsWithCenter.map { beeBoard ->
                async { mapToBeeBoardSolution(beeUcsToWordsMap, beeBoard) }
            }.awaitAll()

            return@runBlocking ProtoBoardSolutions(boardSolutionList.toMap())
        }
    }

    private fun mapToBeeBoardSolution(
        beeUcsToWordsMap: Map<UniqueCharSet, List<String>>,
        beeBoard: SpellingBeeBoard
    ): Pair<String, ProtoBoardSolution> {
        val allWords = beeBoard.ucs.uniqueCharSubsets(ucsPool)
            .filter { subUcs -> subUcs.uniqueCount >= 4 }
            .filter { subUcs -> subUcs.contains(beeBoard.centerChar) }
            .flatMap { subUcs -> beeUcsToWordsMap[subUcs]?.asIterable() ?: emptyList() }
            .toList()

        return beeBoard.toString() to ProtoBoardSolution(allWords)
    }
}
