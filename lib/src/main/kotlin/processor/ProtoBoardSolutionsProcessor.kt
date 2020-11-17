package processor

import com.caseykulm.spellingbee.proto.ProtoBoardSolutions
import models.Dictionary

interface ProtoBoardSolutionsProcessor {
    fun process(dictionary: Dictionary): ProtoBoardSolutions
}
