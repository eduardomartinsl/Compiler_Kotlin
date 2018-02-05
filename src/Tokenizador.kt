class Tokenizador{
    fun tokenizador(entrada: String) : Array<Token>{

        val parser = TokenParse()

        for (char in entrada) parser.consume(char)

        parser.end()

        return parser.tokens.toTypedArray()

    }
}