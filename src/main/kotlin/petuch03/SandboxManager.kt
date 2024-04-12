package petuch03

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Paths

class SandboxManager {
    fun runPython(filePath: String): ExecutionResult{
        val workingPath = Paths.get("").toAbsolutePath().toString()
        val errorList = mutableListOf<String>() // List to hold error messages

        try {
            val processBuilder = ProcessBuilder("python3", filePath)
            processBuilder.directory(File(workingPath))
            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            val errorBuilder = StringBuilder()

            var line: String?
//            println("Output:")
//            while (reader.readLine().also { line = it } != null) {
//                println(line)
//            }

//            println("Errors:")
            while (errorReader.readLine().also { line = it } != null) {
//                println(line)
                errorBuilder.append(line).append("\n")
            }

            val exitVal = process.waitFor()
            return if (exitVal == 0) {
                ExecutionResult(ExecutionResultEnum.SUCCESS)
            } else {
                analyzeErrorOutput(errorBuilder.toString())
            }
        } catch (e: Exception) {
            return ExecutionResult(ExecutionResultEnum.UNKNOWN_ERROR, e.message)
        }
    }

    private fun analyzeErrorOutput(errorOutput: String): ExecutionResult {
        return when {
            "SyntaxError" in errorOutput -> ExecutionResult(ExecutionResultEnum.SYNTAX_ERROR, "Syntax error occurred: $errorOutput")
            errorOutput.isNotEmpty() -> ExecutionResult(ExecutionResultEnum.RUNTIME_ERROR, "Runtime error occurred: $errorOutput")
            else -> ExecutionResult(ExecutionResultEnum.UNKNOWN_ERROR, "An unspecified error occurred: $errorOutput")
        }
    }
}