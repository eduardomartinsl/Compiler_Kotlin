import Constants.EOL
import Constants.IDENTIFIER
import Constants.KEYWORD
import Constants.OPERATOR
import Constants.TYPE

public class Sintatic(val tokens: List<Token>) {

    private val token get() = tokens[index]
    private var index = 0
    private val isOver get() = tokens.size == index
    private var incompleteSymbols = mutableListOf<String>()

    val symbolTable = SymbolTable()

    private fun createSymbol(id: String){
        incompleteSymbols.add(id)
    }

    private fun addSymbolType(type: String){
        incompleteSymbols.forEach{
            if(symbolTable.exists(it)){
                error("O Simbolo $it já foi declarado!")
            }
            symbolTable.insert(it, type)
        }
        incompleteSymbols.clear()
    }

    fun analyze() {

        z()

        println("Sintático concluido")

    }

    fun getSymbols(): Array<Symbol>{
        return symbolTable.values
    }

    private fun error(message: String) {

        throw Exception(message)
    }

    private fun expect(type: String, value: String?, message: String) {
        if (token.type == type && (value == null || token.value == value)) {
            consumeToken()
        } else {
            error("É esperado $message")
        }
    }

    private fun `is`(type: String, value: String?): Boolean {
        return token.type == OPERATOR && (value == null || token.value == value)
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

    private fun identifier() : String {
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

    private fun backwardToken(){
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

        if(!l()) {

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

    private fun l() : Boolean {

        codeNotOver()

        val id = identifier()

        createSymbol(id)

        return x()
    }

    private fun x() : Boolean {

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

        if(!isOver) {
            endOfLine()
        }

    }

    private fun t() {

        codeNotOver()

        identifier()

    }

}