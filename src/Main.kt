import java.io.File
import java.io.InputStream

fun main(args: Array<String>) {
    //leitura, bufferização e conversão de arquivo para string
    val inputStream : InputStream = File("Algoritm.txt").inputStream()
    val inputString = inputStream.bufferedReader().use { it.readText() }

    //analisador léxico
    val tokens = TokenParser().parse(inputString)
    for(tokens in tokens){
        println(tokens)
    }

    //analisador sintático
    Analyzer(tokens).analyze()

}