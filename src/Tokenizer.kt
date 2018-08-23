import constants.Constants.COMMENT
import constants.Constants.EOL
import constants.Constants.EOL_CHAR
import constants.Constants.IDENTIFIER
import constants.Constants.INTEGER_TYPE
import constants.Constants.KEYWORD
import constants.Constants.KEYWORDS_LIST
import constants.Constants.NUMBER_CHARS
import constants.Constants.NUMBER_TYPE
import constants.Constants.OPERATOR
import constants.Constants.OPERATORS_CHARS
import constants.Constants.REAL_TYPE
import constants.Constants.REGEX_IDENTIFIER
import constants.Constants.REGEX_START_IDENTIFIER
import constants.Constants.TYPE
import constants.Constants.TYPES_LIST
import constants.Constants.WHITE_SPACE
import models.Token


class Tokenizer {

    private var lineNumber = 1

    private val _tokens = mutableListOf<Token>()

    var tokenAtual : Token? = null
    val tokens : List<Token>
        get (){
            return identifyKeywordsAndTypes(_tokens)
        }
    val tokensSemComment = mutableListOf<Token>()

    fun parse(code: String): List<Token> {
        for(char in code){
            consume(char)
        }

        for (token in tokens){
            if (token.type != COMMENT && token.type != EOL) tokensSemComment.add(token)
        }
        //TODO avaliar lista de tokens sem comentários
//        return tokens
        return tokensSemComment
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
            if(token.type == NUMBER_TYPE){
                if(token.value.contains(".")){
                    token.type = REAL_TYPE
                }else{
                    token.type = INTEGER_TYPE
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

            tokenAtual = Token(type, char.toString(), lineNumber)
            _tokens.add(tokenAtual!!)
            return
        }

        val token = tokenAtual!!

        if(token.type == OPERATOR && char == '*'){
            token.type = COMMENT
        }

        if(
                (token.type == COMMENT && token.value.contains("}")) ||
                (token.type == COMMENT && token.value.contains("*/"))
        ){
            tokenAtual = null
            return consume(char)
        }

        if(!canConcat(token.type,token.value, char.toString())){

            tokenAtual = null
            return consume(char)
        }

        token.value += char
    }

    private fun resolveType(value : String): String {

        if(NUMBER_CHARS.contains(value))
            return NUMBER_TYPE

        if(value.matches(Regex(REGEX_START_IDENTIFIER))){
            return IDENTIFIER
        }
        if(OPERATORS_CHARS.contains(value)){
            if(value == "{"){
                return  COMMENT
            }
            return OPERATOR
        }
        if(value == EOL_CHAR){
            lineNumber++
            return EOL
        }
        throw error("models.Token Invalido: $value na linha $lineNumber")
    }

    fun canConcat(type : String, value : String, char: String) : Boolean{

        when(type){
            COMMENT ->{
                if(value[value.length -1] == '*' && char == "/"){
                    return true
                }
                return true
            }
            IDENTIFIER -> {
                if(char.matches(Regex(REGEX_IDENTIFIER))){
                    return true
                }
            }

            OPERATOR -> {
                when(value) {
                    "<" -> if(char == ">"
                            || char =="=") return true
                    ">" -> if(char == "=") return true
                    ":" -> if(char == "=") return true
                    "/" -> if(char == "*") return true
                    "*" -> if(char == "/") return true
                }
            }
            EOL -> {
                if(char == EOL_CHAR || char == "\r"){
                    lineNumber = 0
                    return true
                }
            }
            NUMBER_TYPE -> {
                if(NUMBER_CHARS.contains(char) || char == ".") {
                    return true
                }
            }
        }
        return false
    }
}