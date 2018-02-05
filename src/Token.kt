data class Token(
        var type: String,
        var value: String
){
    override fun toString(): String {

        return ("$type($value)")
    }
}