package models

data class Symbol(
        var varName: String,
        var type: String,
        var address: Int,
        var scope: String,
        var fatherProcedure: String?

){
    override fun toString(): String {
        return "Nome: $varName, Tipo: $type, Escopo: $scope, Endereço: $address"
    }
}