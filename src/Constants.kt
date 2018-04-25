object Constants {
    val WHITE_SPACE =  " \t\r"
    val WHITE_SPACE_WITHOUT_R =  " \t"
    val REGEX_IDENTIFIER = "[a-zA-Z_0-9]"
    val REGEX_START_IDENTIFIER = "[a-zA-Z_$]"
    val NUMBER_CHARS = "0123456789"
    val NUMBER_TYPE = "number"
    val IDENTIFIER = "id"
    val OPERATORS_CHARS = ":=;+-,()*/<>{}."
    val OPERATOR = "operator"
    val KEYWORD = "keyword"
    val TYPE = "type"
    val REAL_TYPE = "real"
    val INTEGER_TYPE = "integer"
    val EOL = "ε"
    val EOL_CHAR = "\n"

    val KEYWORDS_LIST = listOf(
            "program",
            "begin",
            "end",
            "var",
            "procedure",
            "else",
            "read",
            "write",
            "while",
            "do",
            "if",
            "then",
            "$")
    val TYPES_LIST = listOf("integer", "real")
}