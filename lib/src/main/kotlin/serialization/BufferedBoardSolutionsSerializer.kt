package serialization

import com.caseykulm.spellingbee.SpellingBeeDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.io.File
import java.time.Instant
import java.time.ZoneId
import models.BoardSolutions
import processor.UniqueCharSetPool
import timing

/**
 * Will serialize into
 */
class BufferedBoardSolutionsSerializer(private val dictionaryName: String) : BoardSolutionsSerializer {
    override fun serialize(boardSolutions: BoardSolutions, ucsPool: UniqueCharSetPool) = timing(
        message = "serializing ${boardSolutions.size} boardSolutions to database"
    ) {
        val solutionFileName = solutionFileName
        val solutionFileAbsolutePath = "${solutionsDir.absolutePath}/${solutionFileName}"
        val solutionFile = File(solutionFileAbsolutePath)
        solutionFile.createNewFile()

        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$solutionFileAbsolutePath")
        SpellingBeeDatabase.Schema.create(driver)
        val database = SpellingBeeDatabase(driver)

        database.boardSolutionQueries.transaction {
            boardSolutions.forEach {
                database.boardSolutionQueries.insert(
                    board = it.key.toString(),
                    solutions = it.value.joinToString(separator = ",")
                )
            }
        }
    }

    private val solutionsDir = File("${System.getProperty("user.dir")}/solutions")

    private val solutionFileName: String
        get() {
            return "spelling_bee_db_$dictionaryName.db"
        }
}
