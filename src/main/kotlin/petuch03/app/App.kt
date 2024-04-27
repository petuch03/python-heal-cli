package petuch03.app

import petuch03.fixer.LLMFixer
import java.io.File


class App(openAIKey: String, printLLMLogs: Boolean = false) {
    private val codeFixer = LLMFixer(openAIKey, printLLMLogs)

    suspend fun execute(filePath: String, maxIterations: Int) {
        if (!File(filePath).exists()) {
            println("The specified file does not exist: $filePath")
            return
        }
        codeFixer.fixCode(filePath, maxIterations)
    }
}