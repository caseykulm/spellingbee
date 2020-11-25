package serialization

import com.caseykulm.spellingbee.SpellingBeeDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import models.BoardSolutions
import processor.UniqueCharSetPool
import timing
import java.io.File

/**
 * Will serialize into
 */
class DatabaseBoardSolutionsSerializer(private val dictionaryName: String) : BoardSolutionsSerializer {
    override fun serialize(boardSolutions: BoardSolutions, ucsPool: UniqueCharSetPool) = timing(
        message = "serializing ${boardSolutions.size} boardSolutions to database"
    ) {
        val solutionFileAbsolutePath = createDbSolutionFileAbsolutePath(dictionaryName)
        val solutionFile = File(solutionFileAbsolutePath)
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$solutionFileAbsolutePath")

        if (!solutionFile.exists()) {
            solutionFile.createNewFile()
        }

        SpellingBeeDatabase.Schema.create(driver)
        val database = SpellingBeeDatabase(driver)

        database.boardSolutionQueries.transaction {
            boardSolutions.forEach {
                database.boardSolutionQueries.insert(
                    board = it.dbBoardValue,
                    solutions = it.dbSolutionsValue
                )
            }
        }
    }
}
