package petuch03.sandbox

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class PythonRunner : Runner {
    override fun run(filePath: String): ExecutionResult {
        val workingPath = Paths.get("").toAbsolutePath().toString()
        val processBuilder = ProcessBuilder("python3", filePath)
        processBuilder.directory(File(workingPath))

        try {
            println("Starting Python execution")
            val process = processBuilder.start()

            val isFinished = process.waitFor(10, TimeUnit.SECONDS)
            if (!isFinished) {
                process.destroy()
                return ExecutionResult(ExecutionResultEnum.TIMEOUT, "Execution timed out.")
            }

            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            val errorBuilder = StringBuilder()
            var line: String?
            while (errorReader.readLine().also { line = it } != null) {
                errorBuilder.append(line).append("\n")
            }

            return if (process.exitValue() == 0) {
                ExecutionResult(ExecutionResultEnum.SUCCESS)
            } else {
                analyzeErrorOutput(errorBuilder.toString())
            }
        } catch (e: Exception) {
            return ExecutionResult(
                ExecutionResultEnum.UNKNOWN_ERROR,
                "Failed to execute Python script. Error: ${e.message}"
            )
        }
    }


    private fun analyzeErrorOutput(errorOutput: String): ExecutionResult {
        return when {
            "SyntaxError" in errorOutput -> ExecutionResult(
                ExecutionResultEnum.SYNTAX_ERROR,
                "Syntax error occurred: $errorOutput"
            )

            errorOutput.isNotEmpty() -> ExecutionResult(
                ExecutionResultEnum.RUNTIME_ERROR,
                "Runtime error occurred: $errorOutput"
            )

            else -> ExecutionResult(ExecutionResultEnum.UNKNOWN_ERROR, "An unspecified error occurred: $errorOutput")
        }
    }
}