import kotlin.math.pow

class UniqueCharSet(word: String) : Comparable<UniqueCharSet> {
    val uniqueChars = word.toCharArray().apply { sort() }.toSet()
    val uniqueCount: Int = uniqueChars.size
    val uniqueCharCombos: Set<Set<Char>> by lazy { mapToAllCombinations(uniqueChars) }

    fun contains(char: Char): Boolean = uniqueChars.contains(char)

    private fun mapToAllCombinations(input: Set<Char>): Set<Set<Char>> {
        val combos = mutableSetOf<Set<Char>>()
        val inputList = input.toList()

        val bits = input.size
        val start = 0
        val end = 2.0.pow(bits).toInt()

        /*
        Examplue input: taco
        bits = 4
        start = 0
        exclusiveEnd = 16

        Int - Binary String - Reversed - Combo
        0 - 0 - 0 - ""
        1 - 1 - 1 - "a"
        2 - 10 - 01 - "c"
        3 - 11 - 11 - "ac"
        4 - 100 - 001 - "o"
        5 - 101 - 101 - "ao"
        6 - 110 - 011 - "co"
        7 - 111 - 111 - "aco"
        8 - 1000 - 0001 - "t"
        9 - 1001 - 1001 - "at"
        10 - 1010 - 0101 - "ct"
        11 - 1011 - 1101 - "act"
        12 - 1100 - 0011 - "ot"
        13 - 1101 - 1011 - "aot"
        14 - 1110 - 0111 - "cot"
        15 - 1111 - 1111 - "acot"
         */

        for (counter in start until end) {
            val combo = mutableSetOf<Char>()

            counter.toString(radix = 2)
                .reversed() // This helps deal with the loss of leading 0's being trimmed by toString
                .forEachIndexed { index, c ->
                    if (c == '1') {
                        combo.add(inputList[index])
                    }
                }

            combos.add(combo)
        }

        return combos.toSet()
    }

    override fun equals(other: Any?): Boolean {
        return (other as? UniqueCharSet)?.let {
            this.uniqueChars == it.uniqueChars
        } ?: false
    }

    override fun hashCode(): Int {
        var result = uniqueChars.hashCode()
        result = 31 * result + uniqueCount
        return result
    }

    override fun toString(): String =  uniqueChars.joinToString(separator = "")

    override fun compareTo(other: UniqueCharSet): Int {
        return toString().compareTo(other.toString())
    }
}