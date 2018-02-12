import Constants.EOL
import Constants.EOL_CHAR
import Constants.IDENTIFIER
import Constants.KEYWORD
import Constants.KEYWORDS_LIST
import Constants.OPERATOR
import Constants.OPERATORS_CHARS
import Constants.REGEX_IDENTIFIER
import Constants.REGEX_START_IDENTIFIER
import Constants.TYPE
import Constants.TYPES_LIST
import Constants.WHITE_SPACE
import Constants.WHITE_SPACE_WITHOUT_R

class Tokenizer {

    private val _tokens = mutableListOf<Token>()

    var tokenAtual : Token? = null
    val tokens : List<Token>
        get (){
            return identifyKeywordsAndTypes(_tokens)
        }

    fun parse(code: String): List<Token> {
        for(char in code){
            consume(char)
        }

        return tokens
    }

    fun identifyKeywordsAndTypes(tokens: List<Token>): List<Token> {
        for(token in tokens){
            if(token.type == IDENTIFIER){
                if(KEYWORDS_LIST.contains(token.value)){
                    token.type = KEYWORD
                }
                if(TYPES_LIST.contains(token.value)){
                    token.type = TYPE
                }
            }
        }
        return tokens
    }

    fun consume(char: Char){
        if(tokenAtual == null){
            if(WHITE_SPACE.contains(char)){
                return
            }
            
            val type = resolveType(char.toString())
            
            tokenAtual = Token(type, char.toString())
            _tokens.add(tokenAtual!!)
            return
        }

        val token = tokenAtual!!

        if(WHITE_SPACE_WITHOUT_R.contains(char)){
            tokenAtual = null
            return
        }
        if(!canConcat(token.type,token.value, char.toString())){
            tokenAtual = null
            return consume(char)
        }
        token.value += char

    }

    private fun resolveType(value : String): String {
        if(value.matches(Regex(REGEX_START_IDENTIFIER))){
            return IDENTIFIER
        }
        if(OPERATORS_CHARS.contains(value)){
            return OPERATOR
        }
        if(value == EOL_CHAR){
            return EOL
        }
        throw error("Token Invalido")
    }

    fun canConcat(type : String, value : String, char: String) : Boolean{
        if(type == IDENTIFIER){
            if(char.matches(Regex(REGEX_IDENTIFIER))){
                return true
            }
        }
        if(type == OPERATOR ){
            if(value == ":" && char == "=") return true
        }
        if(type == EOL){
            if(char == EOL_CHAR || char == "\r") return true
        }
        return false
    }
}