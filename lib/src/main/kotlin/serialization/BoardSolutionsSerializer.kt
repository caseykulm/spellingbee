package serialization

import models.BoardSolutions
import processor.UniqueCharSetPool

interface BoardSolutionsSerializer {
    fun serialize(boardSolutions: BoardSolutions, ucsPool: UniqueCharSetPool)
}
