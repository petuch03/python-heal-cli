package petuch03.app

import io.github.cdimascio.dotenv.dotenv
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

object CLI {
    suspend fun runCLI(args: Array<String>): CLIResult {
        val parser = ArgParser("python-heal-cli")
        val filePath by parser.option(
            type = ArgType.String,
            fullName = "filePath",
            shortName = "f",
            description = "Path to the Python file",
        ).required()
        val maxIterations by parser.option(
            type = ArgType.Int,
            fullName = "maxIterations",
            shortName = "m",
            description = "Max number of iterations",
        ).default(5)
        val printLLMLogs by parser.option(type = ArgType.Boolean, fullName = "printLLMLogs").default(false)

        parser.parse(args)
        val dotenv = dotenv()

        if (!Regex(".*\\.py$").matches(filePath)) {
            return CLIResult(false, "Invalid file extension: the file path must end with '.py'")
        }

        if (maxIterations <= 0) {
            return CLIResult(false, "Negative or zero max iterations: the max iterations parameter must be positive")
        }

        if (dotenv["OPENAI_KEY"] == null) {
            return CLIResult(false, "OPENAI_KEY is not present in .env")
        }

        val app = App(dotenv["OPENAI_KEY"], printLLMLogs)
        app.execute(filePath, maxIterations)
        return CLIResult(true)
    }

}