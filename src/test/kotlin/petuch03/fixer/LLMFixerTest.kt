package petuch03.fixer

import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.Test

class LLMFixerTest {
    private val apiKey = dotenv()["OPENAI_KEY"]
    private val fixer = LLMFixer(apiKey)

    @Test
    fun testFileCreation(@TempDir tempDir: Path) = runBlocking() {
        val testFilePath = tempDir.resolve("test.py").toString()
        File(testFilePath).writeText("print(hello)")
        fixer.fixCode(testFilePath, 1)
        assertTrue(File(tempDir.resolve("test-llm.py").toString()).exists())
    }

    @Test
    fun testSuccessfulFileFix(@TempDir tempDir: Path) = runBlocking() {
        val testFilePath = tempDir.resolve("test.py").toString()
        File(testFilePath).writeText("print(hello)")
        fixer.fixCode(testFilePath, 5)
        val fixedFilePath = tempDir.resolve("test-llm.py").toString()
        val fixedFile = File(fixedFilePath)
        assertTrue(fixedFile.exists())
        assertTrue(fixedFile.readText().contains("print(\"hello\")"))
    }

    @Test
    fun testFailureAfterMaxIterations(@TempDir tempDir: Path) = runBlocking() {
        val testFilePath = tempDir.resolve("test_with_error.py").toString()
        File(testFilePath).writeText("prnt(Hello, \"World)")
        fixer.fixCode(testFilePath, 1) // Assuming it needs more than 1 iteration to fix
        val fixedFilePath = tempDir.resolve("test_with_error-llm.py").toString()
        assertTrue(File(fixedFilePath).exists()) // Check if it still creates the file
    }

    @Test
    fun testIOExceptionHandling(@TempDir tempDir: Path) = runBlocking() {
        val testFilePath = tempDir.resolve("test_unreadable.py").toString()
        val testFile = File(testFilePath)
        testFile.writeText("print(f'Hello, World!')")
        testFile.setReadable(false)
        fixer.fixCode(testFilePath, 1)
    }

}