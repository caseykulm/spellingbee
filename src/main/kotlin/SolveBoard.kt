import models.BoardSolutions
import models.SpellingBeeBoard
import processor.UniqueCharSetPool

fun solveBoard(
    solutions: BoardSolutions,
    board: SpellingBeeBoard,
    ucsPool: UniqueCharSetPool
) {
    val filterSolutions: Set<String> = solutions[board] ?: error("No solutions found for $board")
    val solutionsBySize: Map<Int, List<String>> = filterSolutions.groupBy {
        ucsPool.getOrCreateUniqueCharSet(it).uniqueCount
    }
    ucsPool.logHitMissRate()
    println("Solutions for $board are: $filterSolutions")
    println("$solutionsBySize")
}