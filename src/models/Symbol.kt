package models

data class Symbol(
        var varName: String,
        var type: String,
        var address: Int,
        var scope: String?,
        var fatherProcedure: String?

){
    override fun toString(): String {
        if(fatherProcedure == null)
            return "Nome: $varName, Tipo: $type, Escopo: $scope, Endereço: $address"
        return "Nome: $varName, Tipo: $type, Escopo: $scope, Endereço: $address, Procedimento pai: $fatherProcedure"

    }
}