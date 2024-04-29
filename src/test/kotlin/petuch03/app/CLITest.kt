package petuch03.app

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class CLITest {
    @Test
    fun testInvalidFileExtension() = runBlocking {
        val args = arrayOf("-f", "script.txt", "-m", "3")
        val result = CLI.runCLI(args)
        assertEquals(false, result.success)
        assertEquals("Invalid file extension: the file path must end with '.py'", result.message)
    }


    @Test
    fun testNegativeMaxIterations() = runBlocking {
        val args = arrayOf("-f", "src/test/app/script.py", "-m", "-1")
        val result = CLI.runCLI(args)
        assertEquals(false, result.success)
        assertEquals("Negative or zero max iterations: the max iterations parameter must be positive", result.message)
    }

    @Test
    fun testValidInput() = runBlocking {
        val args = arrayOf("-f", "src/test/app/script.py", "-m", "5")
        val result = CLI.runCLI(args)
        assertEquals(true, result.success)
    }

//    @Test
//    fun testMissingOpenAIKey() = runBlocking{
//        val dotenv = Dotenv.load()
//        val args = arrayOf("-f", "script.py")
//        val result = CLI.runCLI(args)
//        assertEquals(false, result.success)
//        assertEquals("OPENAI_KEY is not present in .env", result.message)
//    }
}
