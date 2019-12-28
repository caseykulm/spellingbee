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

class SpellingBeeBoard(val ucs: UniqueCharSet, val centerChar: Char) {
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
        return "SpellingBeeBoard(ucs=$ucs, centerChar=$centerChar)"
    }
}
