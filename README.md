## Overview

Python-Heal-CLI is a command-line tool that automatically fixes syntax and runtime errors in Python scripts using OpenAI's GPT-3.5 Turbo model. 
This tool iterates over the provided Python file, fixing errors until either all are resolved or a maximum number of iterations is reached.

## Requirements

- `Java 11` or higher
- `Python 3.x` installed and accessible in the system path as `python3`
- An active `OpenAI API` key (set in `.env` file)

## Configuration

1. API Key: Set your OpenAI API key in your environment:
2. Environment File: Create a .env file in the same directory where you run the JAR file with the following content:
```
OPENAI_KEY=your_api_key_here
```

## Usage Syntax:
```bash
java -jar python-heal-cli.jar -f <path_to_python_file> [-m <max_iterations>] [--printLLMLogs]
```
- `-f`, `--filePath`: Path to the Python file that needs fixing.
- `-m`, `--maxIterations`: Optional. Maximum number of iterations to attempt fixes. Default is 5.
- `--printLLMLogs`: Optional. Enable detailed logs from the LLM.

## Example:
```bash
java -jar build/libs/python-heal-cli.jar -f src/test/resources/syntax.py -m 10 --printLLMLogs
```
This command will attempt to fix errors in `syntax.py`, allowing up to 10 iterations and printing logs from the language model API call.

## Output

The tool will create a new Python file with suffix `-llm.py` in the same directory as the input file if fixes are made. If no errors are found or no changes are made, the file won't be created or saved.


If there are errors during execution, such as an invalid file path or missing API key, the tool will print an error message and terminate.
