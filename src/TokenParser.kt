//G = ( {Z,I,D,L,X,K,O,S,E,R,T}, {var, : , id, , , integer, real, ; , :=, if, then,+}, P, Z)

class TokenParse{
    companion object{

        val OPERATORS_CHARS = ":=+"
        val VARIABLE_INDICATOR = "var"
        val PONCTUATION = ";,"

        val INT_TYPE = "integer"
        val REAL_TYPE = "Real"
        val IDENTIFIER_TYPE = "id"
        val WHITESPACE_TYPE = " "
        val KEYWORDS_CHARS = "ifthen"

        val KEYWORDS_VALUES = arrayOf("if","then")

    }

    var tokens = mutableListOf<Token>()

    var current_token: Token? = null

    var current_Type = ""

    fun consume(char: Char){
        val token = current_token

        if(token == null){
            current_token = Token(checkType(char), char.toString())
            tokens.add(current_token!!)
            return
        }

        val type = token.tipo

        if(!canBeConcatedInType(type, char)){
            if(type == IDENTIFIER_TYPE){
                token.convertType(convertIdentifierIfNeeded(token.valor))
            }
            current_token = null
            return consume(char)
        }

        token.concat(char)

    }

    fun end(){

    }

    private fun convertIdentifierIfNeeded(valor: String): String {
        if(INT_TYPE.contains(valor)){
            return INT_TYPE
        }

        if(REAL_TYPE.contains(valor)){
            return REAL_TYPE
        }

        if(KEYWORDS_VALUES.contains(valor)){
            return KEYWORDS_CHARS
        }

        TODO("checar veracidade de conversão de elemento")
    }

    private fun canBeConcatedInType(type: String, char: Char): Boolean {
        TODO("Checar concatenação")
    }

    private fun checkType(char: Char): String {

        val regex = Regex("[a-zA-Z_]")

        if(regex.matches(char.toString())) return IDENTIFIER_TYPE

        throw Exception("Token Invalidado")
    }

}