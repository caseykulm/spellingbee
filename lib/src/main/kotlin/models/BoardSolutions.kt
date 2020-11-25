package models

typealias BoardSolutions = Map<SpellingBeeBoard, Set<String>>
typealias BoardSolutionEntry = Pair<SpellingBeeBoard, Set<String>>

interface BoardSolutionQuery {
    fun getSolutions(spellingBeeBoard: SpellingBeeBoard): Set<String>
}
