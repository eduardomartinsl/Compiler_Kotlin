class SymbolTable{

    val symbols = mutableMapOf<String, Symbol>()

    fun insert(id: String, type: String){
        if(exists(id)) throw Error("ERRROU")
        val address = symbols.size + 1
        symbols.put(id, Symbol(id,type, address))
    }

    fun exists(id: String): Boolean {
        return symbols.containsKey(id)
    }

    val values: Array<Symbol>
        get() = symbols.values.toTypedArray()
}