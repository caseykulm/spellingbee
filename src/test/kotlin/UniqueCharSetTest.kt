import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.pow

internal class UniqueCharSetTest {
    data class EqualityTruthRow(val word1: String, val word2: String, val shouldBeEqual: Boolean)

    private val equalityTruthTable = listOf(
        EqualityTruthRow("foo", "oof", true),
        EqualityTruthRow("fo", "oof", true),
        EqualityTruthRow("foo", "loof", false),
        EqualityTruthRow("taco", "acot", true),
        EqualityTruthRow("taco", "acott", true),
        EqualityTruthRow("tacoe", "acot", false)
    )

    @Test
    internal fun `Given equalityTruthTable, When equal check, Then should be expected`() {
        equalityTruthTable.forEach { truthRow ->
            val subj1 = UniqueCharSet(truthRow.word1)
            val subj2 = UniqueCharSet(truthRow.word2)

            when (truthRow.shouldBeEqual) {
                true -> assertEquals(subj1, subj2, "Failed for: $truthRow")
                false -> assertNotEquals(subj1, subj2, "Failed for: $truthRow")
            }
        }
    }

    data class CountTruthRow(val word: String, val expectedCount: Int)

    private val countTruthTable = listOf(
        CountTruthRow("foo", 2),
        CountTruthRow("fo", 2),
        CountTruthRow("taco", 4),
        CountTruthRow("tacoe", 5)
    )

    @Test
    internal fun `Given countTruthTable, When count, Then should be expected`() {
        countTruthTable.forEach { truthRow ->
            val subj = UniqueCharSet(truthRow.word)

            assertEquals(truthRow.expectedCount, subj.uniqueCount, "Not the expected count for: $truthRow")
        }
    }

    data class WordSequenceTruthRow(
        val wordSequence: Sequence<String>,
        val expectedMapSize: Int
    )

    private val mapTruthTable = listOf(
        WordSequenceTruthRow(
            wordSequence = sequenceOf("care", "care"),
            expectedMapSize = 1
        ),
        WordSequenceTruthRow(
            wordSequence = sequenceOf("care", "race"),
            expectedMapSize = 1
        ),
        WordSequenceTruthRow(
            wordSequence = sequenceOf("care", "true"),
            expectedMapSize = 2
        )
    )

    @Test
    internal fun `Given mapTruthTable, When create map, Then expected size`() {
        mapTruthTable.forEach { truthRow ->
            val map: Map<UniqueCharSet, List<String>> = truthRow.wordSequence
                .groupBy { UniqueCharSet(it) }

            assertEquals(truthRow.expectedMapSize, map.size, "Map not expected size for $truthRow")
        }
    }

    data class WordsTruthRow(
        /**
         * For now, must be a set of unique characters.
         */
        val input: Set<Char>,

        /**
         * For now, output string will be in alphabetical order
         */
        val output: Set<Set<Char>>
    )

    private val wordsTruthTable = listOf(
        WordsTruthRow(
            input = "ab".toSet(),
            output = setOf("".toSet(), "a".toSet(), "b".toSet(), "ab".toSet())
        ),
        WordsTruthRow(
            input = "taco".toSet(),
            output = setOf(
                "".toSet(),
                "a".toSet(), "c".toSet(), "o".toSet(), "t".toSet(),
                "ac".toSet(), "ao".toSet(), "at".toSet(), "co".toSet(), "ct".toSet(), "ot".toSet(),
                "aco".toSet(), "act".toSet(), "aot".toSet(), "cot".toSet(),
                "taco".toSet()
            )
        )
    )

    @Test
    internal fun `Given wordsTruthTable, When process, Then output is correct`() {
        wordsTruthTable.forEach { truthRow ->
            val ucs = UniqueCharSet(truthRow.input.joinToString(separator = ""))
            val combos = ucs.uniqueCharCombos

            println("Input: ${truthRow.input}, Actual: $combos")

            assertEquals(truthRow.output, combos)
        }
    }
}