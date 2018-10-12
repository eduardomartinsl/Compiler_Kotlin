import constants.Constants.EOL
import constants.Constants.IDENTIFIER
import constants.Constants.INTEGER_TYPE
import constants.Constants.KEYWORD
import constants.Constants.OPERATOR
import constants.Constants.REAL_TYPE
import constants.Constants.TYPE
import models.ProcedureSymbol
import models.Symbol
import models.Token
import models.VarSymbol

public class Sintatic(val tokens: List<Token>) {

    private val token get() = tokens[index]
    private var index = 0
    private val isOver get() = tokens.size == index
    private var incompleteSymbols = mutableListOf<String>()
    private var paramCount = 0
    private val symbolTable = SymbolTable()
    private var currentProcedureArgs: ProcedureSymbol? = null
    private var identificadorOrigem: String = "Desconhecido"

    private fun checkParam(token: Token) {
        catchError {
            val proc = currentProcedureArgs

            if (proc == null) {
                throw Error("É necessário estar em um procedimento para declarar um argumento")
            }

            val symbol = symbolTable.search<Symbol>(token.value)

            if (symbol == null) {
                throw Error("O identificar ${token.value} não foi declarado")
            }

            proc.ensureParam(symbol.valueType, paramCount)

            paramCount++
        }
    }

    private fun checkVar(token: Token) : VarSymbol{
        var symbol : VarSymbol? = null

        catchError {
            symbol = symbolTable.search(token.value)
            if(symbol == null){
                errorIdNaoExistent(token.value)
            }
        }


        return symbol!!
    }

    private fun checkVarType(symbol: VarSymbol, requiredType: String){
        var type = requiredType
        catchError {
            if(symbol.varType == REAL_TYPE && type == INTEGER_TYPE){
                type = REAL_TYPE
            }
            if(symbol.varType != type){
                errorType(symbol.identifier, symbol.varType, type)

            }
        }
    }

    private fun checkProcedure(token: Token) : ProcedureSymbol{
        var symbol : ProcedureSymbol? = null

        catchError {
            symbol = symbolTable.search<ProcedureSymbol>(token.value)
            if(symbol == null){
                errorIdNaoExistent(token.value)
            }
        }


        return symbol!!
    }

    private fun errorType(name: String, type: String, wrongType: String){

        throw Error("O $name esperava um tipo $type e recebeu o tipo $wrongType")
    }

    private fun errorIdNaoExistent(name: String){
        throw Error("O identificador $name não foi declarado")
    }

    private fun createVarSymbol(id: String){
        incompleteSymbols.add(id)
    }

    private fun addVarSymbol(type: String){
        catchError {
            incompleteSymbols.forEach {

                symbolTable.insertVar(it, type)
            }
            incompleteSymbols.clear()
        }
    }

    private fun createParamSymbol(id: String){
        incompleteSymbols.add(id)
    }

    private fun addParamSymbol(type: String){
        catchError {
            incompleteSymbols.forEach {

                symbolTable.insertParams(it, type)
            }
            incompleteSymbols.clear()
        }
    }


    private fun createProcedureSymbol(id: String) {
        catchError {
            symbolTable.insertProc(id)
        }
    }

    private fun closeProcedureSymbol(){
        symbolTable.endProc()
    }



//    private fun createSymbol(id: String) {
//        incompleteSymbols.add(id)
//    }
//
//    private fun addSymbolType(type: String, scope: String = "Global", fatherProcedure: String? = null) {
//        incompleteSymbols.forEach {
//            if (symbolTable.exists(it)) {
//                error("O Simbolo $it já foi declarado!")
//            }
//            symbolTable.insert(it, type, scope, fatherProcedure)
//        }
//        incompleteSymbols.clear()
//    }

    fun analyze() {

        programa()

        println("Sintático concluido")

    }

    fun getSymbols(): Array<Symbol> {
        return symbolTable.values
    }

    private fun <T> catchError(action: ()->T): T{
        try{
            return action()
        }catch (e: Error){
            error("${e.message} (linha ${token.lineNumber})")
            throw e
        }
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

    private fun ignoreEndOfLines() {

        while (token.type == EOL) {
            consumeToken()
        }

    }

    private fun identifier(idType: String = "none"): String {
        val id = token.value
        val consumedToken = token


        expect(IDENTIFIER, null, "identificador")

        if(idType == "arg"){
            checkParam(consumedToken)
        }

        return id
    }

    private fun consumeToken() {
        index++
    }

    private fun backwardToken() {
        index--
    }

    private fun codeNotOver() {

        if (isOver) {
            error("final inesperado")
        }

    }


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
        when {
            isKeyword("var") -> dc_v()
            isKeyword("procedure") -> dc_p()
            else -> return
        }

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

    private fun tipo_var(isParameter: Boolean = false) {
        codeNotOver()
        if (token.type == TYPE) {

            if (token.value == "integer" || token.value == "real") {
                if(isParameter){
                    addParamSymbol(token.value)
                }else{
                    addVarSymbol(token.value)
                }
                consumeToken()
                return
            }

        }

        error("Era esperando um tipo (integer ou real)")

    }

    private fun variaveis(isParameter: Boolean = false) {
        val s = identifier()
        if(isParameter){
            createParamSymbol(s)
        }else{
            createVarSymbol(s)
        }
        mais_var()

    }

    private fun mais_var(){
        if (`is`(OPERATOR, ",")) {
            operator(",")
            variaveis()
        }
    }

    private fun dc_p() {
        keyword("procedure")

        val s = identifier()
        createProcedureSymbol(s)

        parametros()

        corpo_p()

        closeProcedureSymbol()
    }

    private fun parametros(){
        if (token.type == OPERATOR) {
            operator("(")
            lista_par()
            operator(")")
        }
    }

    private fun lista_par(){


        variaveis(true)

        operator(":")

        tipo_var(true)
        mais_par()
    }

    private fun mais_par(){
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


    private fun lista_arg(proc: ProcedureSymbol) {
        if (token.type == OPERATOR) {
            currentProcedureArgs = proc
            operator("(")
            argumentos()
            operator(")")
            if (proc.params.size != paramCount){
                throw Error("O procedimento ${proc.identifier} aceita ${proc.params.size} parametros e foi informado ${paramCount} parametro(s) ")
            }
            currentProcedureArgs = null
            paramCount = 0

        }
    }

    private fun argumentos( ) {
        identifier("arg")
        mais_ident( )
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
            val idToken = token
            identifier()


            restoIdent(idToken)
        }
    }

    private fun restoIdent(idToken: Token) {
        if (`is`( token.type , ":=") ) {

            val varSymbol = checkVar(idToken)
            val identificadorAnterior = identificadorOrigem
            identificadorOrigem = idToken.value
            operator(":=")

            val exprType = expressao()

            checkVarType(varSymbol, exprType)

            identificadorOrigem = identificadorAnterior
            return
        }


        val proc = checkProcedure(idToken)



        lista_arg(proc)
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

    private fun expressao() : String{
        val termType = termo()

        val oTermType = outros_termos()

        catchError {
            if(oTermType != null){
                if(termType != oTermType){
                    errorType(identificadorOrigem, termType, oTermType)
                }
            }
        }

        return termType
    }

    private fun op_un() {
        if (isOperator("+") || isOperator("-")) {
            consumeToken()
        }
    }

    private fun outros_termos(): String? {

        if (isOperator("+") || isOperator("-")) {

            op_ad()

            val termType = termo()

            val oTermType = outros_termos()

            //TODO: temos que continuar esse maloqui aqui :D
            catchError {
                if(oTermType != null){
                    if(termType != oTermType){
                        errorType(identificadorOrigem, termType, oTermType)
                    }
                }
            }

            return termType

        }
        return null
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

    private fun termo() : String{

        op_un()

        var fatorType = fator()

        val maisFatoresType = mais_fatores()
        catchError {
            if(maisFatoresType != null){
                if(fatorType != maisFatoresType){
//                    errorType(identificadorOrigem, fatorType, maisFatoresType)
                    fatorType = REAL_TYPE

                }
            }
        }
        return fatorType
    }

    private fun mais_fatores(): String? {

        if (isOperator("*") || isOperator("/")) {

            op_mul()

            var fatorType = fator()

            val maisFatoresType = mais_fatores()
            catchError {
                if(maisFatoresType != null){
                    if(fatorType != maisFatoresType){
//                        errorType(identificadorOrigem, fatorType, maisFatoresType)
                        fatorType = REAL_TYPE
                    }
                }
            }
            return fatorType

        }
        return null
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

    private fun fator(): String{


        return when (token.type) {
            IDENTIFIER ->{
                val symbol = checkVar(token)
                consumeToken()
                return symbol.varType
                //TODO tem que ver esse cara aqui
            }
            REAL_TYPE -> {
                consumeToken()
                return REAL_TYPE
            }
            INTEGER_TYPE -> {
                consumeToken()
                return INTEGER_TYPE
            }
            OPERATOR -> {
                operator("(")

                val type = expressao()

                operator(")")

                return type
            }
            else-> catchError {
                throw Error("Erro inesperado")
            }
        }

    }

}