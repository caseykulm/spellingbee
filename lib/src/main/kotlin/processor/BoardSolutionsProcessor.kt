package processor

import models.BoardSolutions
import models.Dictionary

interface BoardSolutionsProcessor {
    fun process(dictionary: Dictionary): BoardSolutions
}
