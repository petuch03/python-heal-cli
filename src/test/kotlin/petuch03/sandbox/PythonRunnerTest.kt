package petuch03.sandbox

import org.junit.jupiter.api.Assertions.*
import java.io.File
import kotlin.test.Test

class PythonRunnerTest{
    private val runner = PythonRunner()

    @Test
    fun testSuccessfulExecution() {
        val scriptPath = createPythonScript("print('hello world')")
        val result = runner.run(scriptPath)
        assertEquals(ExecutionResultEnum.SUCCESS, result.executionResult)
        File(scriptPath).delete()
    }

    @Test
    fun testTimeout() {
        val scriptPath = createPythonScript("import time\ntime.sleep(20)")
        val result = runner.run(scriptPath)
        assertEquals(ExecutionResultEnum.TIMEOUT, result.executionResult)
        File(scriptPath).delete()
    }

    @Test
    fun testSyntaxError() {
        val scriptPath = createPythonScript("print('Hello)")
        val result = runner.run(scriptPath)
        assertEquals(ExecutionResultEnum.SYNTAX_ERROR, result.executionResult)
        File(scriptPath).delete()
    }

    @Test
    fun testRuntimeError() {
        val scriptPath = createPythonScript("raise Exception('Test Error')")
        val result = runner.run(scriptPath)
        assertEquals(ExecutionResultEnum.RUNTIME_ERROR, result.executionResult)
        File(scriptPath).delete()
    }

    private fun createPythonScript(code: String): String {
        val tempFile = File.createTempFile("src/test/resources/test_script", ".py").apply {
            writeText(code)
            deleteOnExit()
        }
        return tempFile.absolutePath
    }
}