package serialization

import com.caseykulm.spellingbee.SpellingBeeDatabase
import com.caseykulm.spellingbee.solutions.BoardSolution
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import models.BoardSolutions
import models.toSpellingBeeBoard
import processor.UniqueCharSetPool
import timing
import java.io.File

class DatabaseBoardSolutionsDeserializer(private val dictionaryName: String) : BoardSolutionsDeserializer {
    override fun deserialize(ucsPool: UniqueCharSetPool): BoardSolutions = timing(
        message = "deserialize all solutions"
    ) {
        val solutionFileAbsolutePath = createDbSolutionFileAbsolutePath(dictionaryName)
        val solutionFile = File(solutionFileAbsolutePath)
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$solutionFileAbsolutePath")

        if (!solutionFile.exists()) {
            solutionFile.createNewFile()
            SpellingBeeDatabase.Schema.create(driver)
        }

        val database = SpellingBeeDatabase(driver)

        return@timing runBlocking {
            withContext(Dispatchers.IO) {
                val queryList: List<BoardSolution> = database.boardSolutionQueries
                    .also { println("selectAll()") }
                    .selectAll()
                    .also { println("executeAsList()") }
                    .executeAsList()

                return@withContext queryList
                    .also { println("associating") }
                    .associate { boardSolution: BoardSolution ->
                        boardSolution.board.toSpellingBeeBoard(ucsPool) to boardSolution.solutions.split(",").toSet()
                    }
            }
        }
    }
}
