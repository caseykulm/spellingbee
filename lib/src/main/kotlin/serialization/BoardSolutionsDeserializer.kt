package serialization

import models.BoardSolutions
import processor.UniqueCharSetPool

interface BoardSolutionsDeserializer {
    fun deserialize(ucsPool: UniqueCharSetPool): BoardSolutions
}
