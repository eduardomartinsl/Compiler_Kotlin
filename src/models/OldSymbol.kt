package models

data class OldSymbol(
        var varName: String,
        var type: String,
        var address: Int,
        var scope: String?,
        var parentProcedure: String?

){
    override fun toString(): String {
        if(parentProcedure == null)
            return "Nome: $varName, Tipo: $type,    Escopo: $scope, Endereço: $address"
        return "Nome: $varName, Tipo: $type,    Escopo: $scope, Endereço: $address, Procedimento pai: $parentProcedure"

    }
}