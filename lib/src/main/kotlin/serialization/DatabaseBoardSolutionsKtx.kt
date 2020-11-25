package serialization

import models.SpellingBeeBoard
import java.io.File

val dbSolutionsDir = File("${System.getProperty("user.dir")}/solutions")
fun createDbSolutionFileAbsolutePath(dictionaryName: String) = "${dbSolutionsDir.absolutePath}/${createDbSolutionFileName(dictionaryName)}"

fun createDbSolutionFileName(dictionaryName: String): String = "spelling_bee_db_$dictionaryName.db"

val Map.Entry<SpellingBeeBoard, Set<String>>.dbBoardValue: String get() = this.key.toString()
val Map.Entry<SpellingBeeBoard, Set<String>>.dbSolutionsValue: String get() = this.value.joinToString(separator = ",")
