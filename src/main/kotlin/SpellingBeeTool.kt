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
