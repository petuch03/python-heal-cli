package petuch03

import com.aallam.openai.api.logging.LogLevel
import kotlinx.cli.*
import kotlinx.coroutines.runBlocking
import java.io.File


class App {
    val sandboxManager = SandboxManager()
    val llmManager = LLMManager("", LogLevel.None)

    suspend fun run(filePath: String) {
        val filePathLength = filePath.length
        val newFilePath = filePath.substring(0, filePathLength - 3) + "-llm.py"
        createNewFile(newFilePath)
        val newFile = File(newFilePath)

        var numberOfIterations = 1
        var previousCode = File(filePath).readText(Charsets.UTF_8)
        var previousExecutionResult = sandboxManager.runPython(filePath)
        while (previousExecutionResult.executionResult != ExecutionResultEnum.SUCCESS && numberOfIterations <= 5) {
            val prompt = "Error: \n" +
                    "${previousExecutionResult.description}\n" +
                    "Code: \n" +
                    previousCode
            val resp = llmManager.askLLM(prompt, previousCode.length + 100, 0.2)
            previousCode = resp
            newFile.writeText(previousCode, Charsets.UTF_8)
            println("Iteration ${numberOfIterations}, current status: ${previousExecutionResult.executionResult}")

            previousExecutionResult = sandboxManager.runPython(newFilePath)
            numberOfIterations++
        }
        print("Check out fixed file $newFilePath")
    }

    private fun createNewFile(filePath: String): Boolean {
        val file = File(filePath)
        return try {
            file.createNewFile()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

fun main(args: Array<String>) = runBlocking{
    val parser = ArgParser("python-heal-cli")
    val filePath by parser.option(ArgType.String, shortName = "f", description = "Path to the Python file").required()
    parser.parse(args)

    val app = App()
    app.run(filePath)
//    val res = app.sandboxManager.runPython(filePath)
//    print(res)
}

