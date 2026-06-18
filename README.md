# JSON Validator

A Java-based JSON parser and validator built from scratch using manual lexical analysis and parsing.

## Features

- Tokenizes JSON input without external libraries
- Parses objects, arrays and primitive values
- Detects invalid syntax and malformed JSON
- Supports nested JSON structures
- Includes JUnit tests

## Tech Stack

- Java
- JUnit 4

## Project Structure

```text
src/Main/
├── JSONLexer.java
├── JSONParser.java
├── Token.java
└── Main.java

src/Test/
└── JSONFileValidationTest.java
```

## Example

Valid JSON:

```json
{
  "name": "John",
  "active": true
}
```

Invalid JSON:

```json
{
  "name": "John",
}
```

## Challenge

Built as part of the Coding Challenges JSON Parser challenge:

https://codingchallenges.fyi/challenges/challenge-json-parser/
