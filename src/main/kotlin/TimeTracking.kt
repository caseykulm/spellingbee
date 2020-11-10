fun <T> timing(message: String, pipe: () -> T): T {
    val startTime = System.currentTimeMillis()
    val any = pipe()
    val totalTime: Long = System.currentTimeMillis() - startTime
    val totalTimeStr = totalTime.toString().padStart(length = 5, padChar = ' ')
    println("$totalTimeStr ms to $message")
    return any
}