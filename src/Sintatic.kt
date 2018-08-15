import Constants.EOL
import Constants.IDENTIFIER
import Constants.INTEGER_TYPE
import Constants.KEYWORD
import Constants.OPERATOR
import Constants.REAL_TYPE
import Constants.TYPE

public class Sintatic(val tokens: List<Token>) {

    private val token get() = tokens[index]
    private var index = 0
    private val isOver get() = tokens.size == index
    private var incompleteSymbols = mutableListOf<String>()

    val symbolTable = SymbolTable()

    private fun createSymbol(id: String) {
        incompleteSymbols.add(id)
    }

    private fun addSymbolType(type: String) {
        incompleteSymbols.forEach {
            if (symbolTable.exists(it)) {
                error("O Simbolo $it já foi declarado!")
            }
            symbolTable.insert(it, type)
        }
        incompleteSymbols.clear()
    }

    fun analyze() {

        programa()

        println("Sintático concluido")

    }

    fun getSymbols(): Array<Symbol> {
        return symbolTable.values
    }

    private fun error(message: String) {

        throw Exception(message)
    }

    private fun expect(type: String, value: String?, message: String) {
        if (token.type == type && (value == null || token.value == value)) {
            consumeToken()
        } else {
            error("É esperado $message, encontrado ${token.type} (${token.value}) na linha ${token.lineNumber}")
        }
    }

    private fun `is`(type: String, value: String?): Boolean {
        return token.type == type && (value == null || token.value == value)
    }

    private fun operator(value: String) {
        expect(OPERATOR, value, "o operador '$value'")
    }

    private fun isOperator(value: String): Boolean {
        return `is`(OPERATOR, value)
    }

    private fun isKeyword(value: String): Boolean {
        return `is`(KEYWORD, value)
    }

    private fun keyword(value: String) {
        expect(KEYWORD, value, "uma palavra-chave  '$value'")
    }

    private fun endOfLine() {
        expect(EOL, null, "fim de linha")
    }

    private fun ignoreEndOfLines() {

        while (token.type == EOL) {
            consumeToken()
        }

    }

    private fun identifier(): String {
        val id = token.value
        expect(IDENTIFIER, null, "identificador")
        return id
    }

    private fun consumeToken() {
        index++
        if (isOver) {
            //fazer algo... o q ? nao sei
            //... nada :D
        }
    }

    private fun backwardToken() {
        index--
    }

    private fun codeNotOver() {

        if (isOver) {
            error("final inesperado")
        }

    }

    private fun z() {

        i()

        s()

    }

    private fun i() {

        codeNotOver()

        keyword("var")


        d()

    }

    private fun d() {

        codeNotOver()

        endOfLine()

        if (!l()) {

            if (isOperator(":=")) {
                incompleteSymbols.clear()
                backwardToken()
                return
            }

        }

        operator(":")

        k()

        o()
    }

    private fun l(): Boolean {

        codeNotOver()

        val id = identifier()

        createSymbol(id)

        return x()
    }

    private fun x(): Boolean {

        codeNotOver()

        if (isOperator(",")) {

            operator(",")

            l()

            return true
        }

        return false
    }

    private fun k() {

        codeNotOver()

        if (token.type == TYPE) {

            addSymbolType(token.value)

            if (token.value == "integer") {
                consumeToken()
                return
            }

            if (token.value == "real") {
                consumeToken()
                return
            }

        }

        error("Era esperando um tipo (integer ou real)")

    }

    private fun o() {

        codeNotOver()

        if (isOperator(";")) {
            //Realizar a inserção na tabela de símbolos
            //Busca também? Tirar dúvida
            operator(";")

            d()

            return
        }

        endOfLine()

    }

    private fun s() {

        if (isOver) {
            //acabou
            return
        }

        if (isKeyword("if")) {

            keyword("if")

            e()

            keyword("then")

            s()

            return
        }

        identifier()

        operator(":=")

        e()

    }

    private fun e() {

        codeNotOver()

        t()

        r()

    }

    private fun r() {

        codeNotOver()

        if (isOperator("+")) {

            operator("+")

            t()

            r()

            return
        }

        operator(";")

        if (!isOver) {
            endOfLine()
        }

    }

    private fun t() {

        codeNotOver()

        identifier()

    }

//TODO remover sintático antigo

    private fun programa() {
        codeNotOver()

        keyword("program")
        identifier()
        corpo()
        operator(".")
    }

    private fun corpo() {

        codeNotOver()


        dc()

        keyword("begin")

        comandos()

        keyword("end")

    }

    private fun dc() {


        ignoreEndOfLines()
//        if(!areVariableDeclared){
//            dc_v()
//            mais_dc()
//        }else{
//            dc_p()
//            mais_dc()
//        }
        when {
            isKeyword("var") -> dc_v()
            isKeyword("procedure") -> dc_p()
            else -> return
        }

        //continua ou nao :D
        mais_dc()

    }

    private fun mais_dc() {
        if (token.type == OPERATOR) {
            operator(";")
            dc()
        }
    }

    private fun dc_v() {
        keyword("var")
        variaveis()
        operator(":")
        tipo_var()
    }

    private fun tipo_var() {
        codeNotOver()

        if (token.type == TYPE) {

            //addSymbolType(token.value)

            if (token.value == "integer") {
                consumeToken()
                return
            }

            if (token.value == "real") {
                consumeToken()
                return
            }

        }

        error("Era esperando um tipo (integer ou real)")

    }

    private fun variaveis() {
        identifier()
        mais_var()
    }

    private fun mais_var() {
        if (`is`(OPERATOR, ",")) {
            operator(",")
            variaveis()
        }
    }

    private fun dc_p() {
        keyword("procedure")

        identifier()

        parametros()

        corpo_p()
    }

    private fun parametros() {
        if (token.type == OPERATOR) {
            operator("(")
            lista_par()
            operator(")")
        }
    }

    private fun lista_par() {

        variaveis()

        operator(":")

        tipo_var()

        mais_par()
    }

    private fun mais_par() {
        if (`is`(OPERATOR, ";")) {
            operator(";")

            lista_par()
        }
    }

    private fun corpo_p() {
        dc_loc()

        keyword("begin")

        comandos()

        keyword("end")
    }

    private fun dc_loc() {

        ignoreEndOfLines()

        if (!isKeyword("var")) return

        dc_v()

        mais_dcloc()
    }

    private fun mais_dcloc() {
        if (token.type == OPERATOR) {
            operator(";")
            dc_loc()
        }
    }

    private fun lista_arg() {
        if (token.type == OPERATOR) {
            operator("(")
            argumentos()
            operator(")")
            return
        }
    }

    private fun argumentos() {
        identifier()
        mais_ident()
    }

    private fun mais_ident() {
        if (`is`( OPERATOR, ";" ) ) {
            operator(";")
            argumentos()
            return
        }
    }

    private fun pfalsa() {
        if (`is`(  KEYWORD, "else") ) {
            keyword("else")
            comandos()
            return
        }
    }

    private fun comandos() {

        comando()

        mais_comandos()
    }

    private fun mais_comandos() {
        if (token.type == OPERATOR) {
            operator(";")
            comandos()
            return
        }
    }

    private fun comando() {
        if (token.type == KEYWORD) {
            when (token.value) {
                "read" -> {
                    consumeToken()

                    operator("(")

                    variaveis()

                    operator(")")
                }
                "write" -> {
                    consumeToken()

                    operator("(")

                    variaveis()

                    operator(")")
                }
                "while" -> {
                    consumeToken()

                    condicao()

                    keyword("do")

                    comandos()

                    keyword("$")
                }
                "if" -> {
                    consumeToken()

                    condicao()

                    keyword("then")

                    comandos()

                    pfalsa()

                    keyword("$")
                }
            }
        }
        if (token.type == IDENTIFIER) {
            identifier()
            restoIdent()
        }
    }

    private fun restoIdent() {
        if (`is`( token.type , ":=") ) {
            operator(":=")

            expressao()
            return
        }

        lista_arg()
    }

    private fun condicao() {

        expressao()

        relacao()

        expressao()
    }

    private fun relacao() {
        if (token.type == OPERATOR) {
            when (token.value) {
                "=" -> consumeToken()
                "<>" -> consumeToken()
                ">=" -> consumeToken()
                "<=" -> consumeToken()
                ">" -> consumeToken()
                "<" -> consumeToken()
                else -> error("É esperado um operador de relação")
            }
        }
    }

    private fun expressao() {
        termo()

        outros_termos()
    }

    private fun op_un() {
        if (isOperator("+") || isOperator("-")) {
            consumeToken()
        }
    }

    private fun outros_termos() {

        if (isOperator("+") || isOperator("-")) {


            op_ad()

            termo()

            outros_termos()

            //TODO análise de first e follow

        }
    }

    private fun op_ad() {
        if (token.type == OPERATOR) {
            if (token.value == "+") {
                consumeToken()
                return
            }
            if (token.value == "-") {
                consumeToken()
                return
            }
        }
    }

    private fun termo() {

        op_un()

        fator()

        mais_fatores()

    }

    private fun mais_fatores() {

        if (isOperator("*") || isOperator("/")) {

            op_mul()

            fator()

            mais_fatores()

        }

    }

    private fun op_mul() {
        if (token.type == OPERATOR) {
            if (token.value == "*") {
                consumeToken()
                return
            }
            if (token.value == "/") {
                consumeToken()
                return
            }
        }
    }

    private fun fator() {


        when (token.type) {
            IDENTIFIER -> consumeToken()
            REAL_TYPE -> consumeToken()
            INTEGER_TYPE -> consumeToken()
            OPERATOR -> {
                operator("(")

                expressao()

                operator(")")
            }
        }

    }

}