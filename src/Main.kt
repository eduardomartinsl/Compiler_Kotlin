import java.io.File
import java.io.InputStream

fun main(args: Array<String>) {
    //leitura, bufferização e conversão de arquivo para string
    val inputStream : InputStream = File("Algoritm.txt").inputStream()
    val inputString = inputStream.bufferedReader().use { it.readText() }

    //analisador léxico
    val tokens = Tokenizer().parse(inputString)

    //print dos tokens identificados
    for(tokens in tokens){
        println(tokens)
    }

    //analisador sintático
    val ast = Sintatic(tokens)
    ast.analyze()


    val symbols = ast.getSymbols()
    for(symbol in symbols){
        println(symbol)
    }


}