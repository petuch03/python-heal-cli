package petuch03

import kotlinx.coroutines.runBlocking
import petuch03.app.CLI

fun main(args: Array<String>) = runBlocking {
    val result = CLI.runCLI(args)
    if (!result.success) {
        println("Error: ${result.message}")
    }
}