import models.ParamSymbol
import models.ProcedureSymbol
import models.Symbol
import models.VarSymbol

class SymbolTable {

    val symbols = mutableMapOf<String, Symbol>()

    var currentProcedure: ProcedureSymbol? = null

    fun insertVar(id: String, varType: String): VarSymbol {


        if (currentProcedure != null) {
            val symbol = VarSymbol(id, "local", varType)
            insert(id, symbol, currentProcedure!!.symbolTable)
            return symbol
        }

        val symbol = VarSymbol(id, "global", varType)

        insert(id, symbol)

        return symbol
    }

    fun insertProc(id: String): ProcedureSymbol {

        if(currentProcedure != null)
            throw Error("Nao é possivel declarar um procedimento dentro de outro procedimento")

        val symbol = ProcedureSymbol(id, "global")

        insert(id, symbol)

        currentProcedure = symbol

        return symbol
    }

    fun endProc(){
        currentProcedure = null
    }

    fun insertParams(id: String, paramType: String): ParamSymbol {

        val symbol = ParamSymbol(id, "local", paramType)

        insert(id, symbol, currentProcedure!!.symbolTable)
        return symbol

    }

    private fun insert(id: String, symbol: Symbol, symbols: MutableMap<String, Symbol> = this.symbols) {
        if (symbols.containsKey(id)) {
            throw Error("O Simbolo \"$id\" já foi declarado!")
        }

        symbols[id] = symbol

    }

    fun exists(id: String): Boolean {
        val symbols = currentProcedure?.symbolTable ?: this.symbols
        return symbols.containsKey(id)
    }

    fun <T : Symbol> search(id: String): T? {
        val symbols = currentProcedure?.symbolTable ?: this.symbols


        if(!exists(id)){
            return null
        }

        return symbols[id] as T

    }

    val values: Array<Symbol>
        get() = symbols.values.toTypedArray()
}