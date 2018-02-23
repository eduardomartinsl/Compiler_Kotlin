data class Symbol(
        var varName: String,
        var type: String,
        var address: Int
){
    override fun toString(): String {
        return "Nome: $varName, Tipo: $type, Endereço: $address"
    }
}