import models.Symbol

class SymbolTable{

    val symbols = mutableMapOf<String, Symbol>()

    fun insert(id: String, type: String, scope: String){
        if(exists(id)) throw Error("ERRROU")
        val address = symbols.size + 1
        symbols.put(id, Symbol(id, type, address, scope, null))
    }

    fun exists(id: String): Boolean {
        return symbols.containsKey(id)
    }

    val values: Array<Symbol>
        get() = symbols.values.toTypedArray()
}