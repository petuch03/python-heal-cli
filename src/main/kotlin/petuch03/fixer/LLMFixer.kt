package petuch03.fixer

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.*
import kotlinx.coroutines.delay
import petuch03.sandbox.ExecutionResult
import petuch03.sandbox.ExecutionResultEnum
import petuch03.sandbox.PythonRunner
import java.io.File
import java.io.IOException

class LLMFixer(apiKey: String, printLLMLogs: Boolean = false, modelId: String = "gpt-3.5-turbo") : CodeFixer {
    private val logLevel = if (printLLMLogs) LogLevel.All else LogLevel.None
    private val openAI = OpenAI(apiKey, logging = LoggingConfig(logLevel = logLevel))
    private val model = ModelId(modelId)

    override suspend fun fixCode(filePath: String, maxIterations: Int) {
        val fixedFilePath = Regex("(.*)\\.py$").replace(filePath) { "${it.groupValues[1]}-llm.py" }
        if (!createNewFile(fixedFilePath)) {
            println("Failed to create a new file at $fixedFilePath. Check that file is not present yet.")
            return
        }
        val fixedFile = File(fixedFilePath)

        var numberOfIterations = 1
        val pythonRunner = PythonRunner()
        try {
            var previousIterationCode = File(filePath).readText(Charsets.UTF_8)
            var previousExecutionResult = pythonRunner.run(filePath)
            while (previousExecutionResult.executionResult != ExecutionResultEnum.SUCCESS &&
                numberOfIterations <= maxIterations
            ) {
                println("Iteration ${numberOfIterations}, current status: ${previousExecutionResult.executionResult}")

                val fixedIterationCode = this.askLLM(previousExecutionResult, previousIterationCode)
                previousIterationCode = fixedIterationCode
                try {
                    fixedFile.writeText(previousIterationCode)
                } catch (e: IOException) {
                    println("Failed to write to file $fixedFilePath. Error: ${e.message}")
                    break
                }

                previousExecutionResult = pythonRunner.run(fixedFilePath)
                numberOfIterations++
            }

            if (previousExecutionResult.executionResult == ExecutionResultEnum.SUCCESS) {
                if (numberOfIterations == 1) {// no changes were made
                    println("$filePath has no errors, no changes were made")
                    fixedFile.delete()
                } else {
                    println(
                        "Iteration ${numberOfIterations}, current status: ${previousExecutionResult.executionResult}\n" +
                                "Check out fixed file $fixedFilePath"
                    )
                }
            } else {
                println("Could not fix the file after $numberOfIterations iterations. Check $fixedFilePath for last attempted fixes.")
            }
        } catch (e: IOException) {
            println("Failed to read file $filePath. Error: ${e.message}")
        } catch (e: Exception) {
            println("Unexpected Error occurred: ${e.message}")
        }
    }

    private suspend fun askLLM(previousExecutionResult: ExecutionResult, previousIterationCode: String): String {
        val prompt = createPrompt(previousExecutionResult.description, previousIterationCode)

        val additionalTokens = when (previousExecutionResult.executionResult) {
            ExecutionResultEnum.SYNTAX_ERROR -> 30
            ExecutionResultEnum.RUNTIME_ERROR -> 150
            else -> 0
        }
        val maxTokens = previousIterationCode.length + additionalTokens
        val chatCompletionRequest = ChatCompletionRequest(
            model = model,
            maxTokens = maxTokens,
            temperature = 0.2,
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "Your task is to fix provided Python code. " +
                            "The error will be given to you. " +
                            "You need to output only the fixed version of code. For example: \n" +
                            "print(\"hello\")"
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )
        repeat(3) { attempt ->
            try {
                val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
                return completion.choices[0].message.content.toString()
            } catch (e: Exception) {
                if (attempt < 2) {
                    println("Attempt ${attempt + 1} failed, retrying...")
                    delay(2000)
                } else {
                    println("Failed to communicate with OpenAI API after several attempts: ${e.message}")
                }
            }
        }
        return "Error: Failed to obtain a fix from the OpenAI API after multiple attempts."
    }


    private fun createPrompt(error: String?, code: String): String {
        return "Fix the error in the code and print the correct code. Error: \n" +
                "${error}\n" +
                "Code: \n" +
                code
    }


    private fun createNewFile(filePath: String): Boolean {
        val file = File(filePath)
        return try {
            file.createNewFile()
        } catch (_: Exception) {
            false
        }
    }
}