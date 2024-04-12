package petuch03
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI

class LLMManager(apiKey: String, logLevel: LogLevel) {
    private val openAI = OpenAI(apiKey, logging = LoggingConfig(logLevel = logLevel))
    private val model = ModelId("gpt-3.5-turbo")

    suspend fun askLLM(prompt: String, maxTokens: Int, temperature: Double = 0.5): String {
        val chatCompletionRequest = ChatCompletionRequest(
            model = model,
            maxTokens = maxTokens,
            temperature = temperature,
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "Your task is to fix provided Python code. " +
                            "The error will be given to you. " +
                            "You need to output only the fixed version of code. For example: \n" +
                            "print(\"Hello World!\")"
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        return completion.choices[0].message.content.toString()
    }
}