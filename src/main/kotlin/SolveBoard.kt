fun solveBoard(solutions: Map<SpellingBeeBoard, Set<String>>, board: SpellingBeeBoard) {
    val filterSolutions: Set<String> = solutions[board]!!
    val solutionsBySize: Map<Int, List<String>> = filterSolutions.groupBy { UniqueCharSet(it).uniqueCount }
    println("Solutions for $board are: $filterSolutions")
    println("$solutionsBySize")
}