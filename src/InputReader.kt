import java.io.File
import java.io.InputStream

fun main(args: Array<String>) {

    val inputStream : InputStream = File("Algoritm.txt").inputStream()

    val inputString = inputStream.bufferedReader().use { it.readText() }

    val tokenizador = Tokenizador()

    val retorno = tokenizador.tokenizador(inputString)

}