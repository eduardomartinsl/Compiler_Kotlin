data class Token(
        var type: String,
        var value: String,
        val lineNumber: Int
){
    override fun toString(): String {

        return ("$type($value)")
    }
}