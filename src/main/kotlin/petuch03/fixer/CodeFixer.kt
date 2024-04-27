package petuch03.fixer

interface CodeFixer {
    suspend fun fixCode(filePath: String, maxIterations: Int)
}