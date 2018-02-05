import java.io.File
import java.io.InputStream

fun main(args: Array<String>) {

    val inputStream : InputStream = File("Algoritm.txt").inputStream()

    val inputString = inputStream.bufferedReader().use { it.readText() }

    val parser = TokenParser()

    val tokens = parser.parse(inputString)

    val analyzer = Analyzer(tokens)

    analyzer.analyze()

}