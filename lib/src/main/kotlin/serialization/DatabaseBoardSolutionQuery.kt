package serialization

import com.caseykulm.spellingbee.SpellingBeeDatabase
import com.caseykulm.spellingbee.solutions.BoardSolution
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import models.BoardSolutionQuery
import models.SpellingBeeBoard
import java.io.File
import java.lang.IllegalStateException

class DatabaseBoardSolutionQuery(private val dictionaryName: String) : BoardSolutionQuery {
    override fun getSolutions(spellingBeeBoard: SpellingBeeBoard): Set<String> {
        val solutionFileAbsolutePath = createDbSolutionFileAbsolutePath(dictionaryName)
        val solutionFile = File(solutionFileAbsolutePath)
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$solutionFileAbsolutePath")

        if (!solutionFile.exists()) {
            solutionFile.createNewFile()
            SpellingBeeDatabase.Schema.create(driver)
        }

        val database = SpellingBeeDatabase(driver)

        return runBlocking {
            withContext(Dispatchers.IO) {
                val boardSolution: BoardSolution = database.boardSolutionQueries
                    .selectByBoard(spellingBeeBoard.toString())
                    .executeAsOneOrNull()
                    ?: throw IllegalStateException("No solution found for $spellingBeeBoard")

                return@withContext boardSolution.solutions.split(",").toSet()
            }
        }
    }
}
