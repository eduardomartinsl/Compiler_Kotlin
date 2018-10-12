package models

import java.text.FieldPosition

open class Symbol(
        val identifier: String,
        val scope: String,
        val symbolType: String
) {
    val valueType : String  by lazy {

         if(this is VarSymbol) {
            this.varType
        }else if(this is ParamSymbol){
            this.paramType
        }else {
             "none"
        }

    }
}


class VarSymbol (
         identifier: String,
         scope: String,
         val varType: String
) : Symbol(
        identifier,
        scope,
        "var"
) {
    override fun toString(): String {
        return "[VarSymbol: $identifier : $varType]"
    }
}

class ParamSymbol (
        identifier: String,
        scope: String,
        val paramType: String
) : Symbol(
        identifier,
        scope,
        "parameter"
){


    override fun toString(): String {
        return "[ParamSymbol: $identifier : $paramType]"
    }
}

class ProcedureSymbol (
        identifier: String,
        scope: String
) : Symbol(
        identifier,
        scope,
        "procedure"
) {

    val symbolTable =  mutableMapOf<String, Symbol>()

    val params by lazy { symbolTable.filter { it.value is ParamSymbol }.map { it.value as ParamSymbol } }

    fun ensureParam(paramType: String, position: Int) {

        if(params.size <= position){
            throw Error("O procedimento $identifier só aceita ${params.size} parametros e foi informado um ${position + 1} parametro ")
        }

        if(params[position].paramType != paramType){
            throw Error("O parametro ${params[position].identifier} do procedimento $identifier é de um tipo incompativel")
        }

        //uhm tudo deu bom !

    }

    override fun toString(): String {
        var str = "[ProcedureSymbol: $identifier \n"

        symbolTable.forEach { str += "\t ${it.value} \n" }

        return  "$str \n]"
    }
}