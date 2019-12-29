import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.File
import java.lang.reflect.ParameterizedType

interface DictionaryTool {
    fun getDictionary(): Map<String, String>
}

class DictionaryToolImpl(private val moshi: Moshi) : DictionaryTool {
    private val typeAdapter: ParameterizedType = Types.newParameterizedType(
        Map::class.java,
        String::class.java,
        String::class.java
    )
    private val dictionaryAdapter = moshi.adapter<Map<String, String>>(typeAdapter)

    override fun getDictionary(): Map<String, String> {
        return readDictionaryFromFileToMap()
            .toSortedMap(Comparator { o1, o2 -> o1.compareTo(o2) })
    }

    private fun readDictionaryFromFileToMap(): Map<String, String> {
        val dictionaryFile = File(javaClass.classLoader.getResource("dictionary.json").file)
        val dictionaryJsonStr = dictionaryFile.readText()

        println("dictionaryFilePath: ${dictionaryFile.absolutePath}")

        return dictionaryAdapter.fromJson(dictionaryJsonStr)!!
    }
}

