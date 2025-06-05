package Test;

import Main.JSONLexer;
import Main.JSONParser;
import Main.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class JSONFileValidationTest {

    // Parameterized test method that accepts parameters directly
    @ParameterizedTest
    @MethodSource("data")
    void testJsonFileValidation(Path filePath, boolean shouldBeValid) {
        try {
            String jsonContent = new String(Files.readAllBytes(filePath));
            JSONLexer lexer = new JSONLexer(jsonContent);
            List<Token> tokens = lexer.tokenize();
            JSONParser parser = new JSONParser(tokens);
            parser.parse();

            Assertions.assertTrue(shouldBeValid, "Expected valid JSON, but got invalid JSON: " + filePath);
        } catch (Exception e) {
            Assertions.assertTrue(!shouldBeValid, "Expected invalid JSON, but got valid JSON: " + filePath);
        }
    }

    // This method will provide the parameters for the parameterized test
    static Collection<Object[]> data() {
        String baseDir = System.getProperty("user.dir") + "/resources/";
        List<Object[]> testCases = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(baseDir))) {
            List<Path> jsonFiles = paths
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".json"))
                    .collect(Collectors.toList());

            for (Path file : jsonFiles) {
                String fileName = file.getFileName().toString().toLowerCase();
                boolean shouldBeValid = fileName.startsWith("valid") || fileName.startsWith("pass");
                testCases.add(new Object[]{file, shouldBeValid});
            }
        } catch (IOException e) {
            Assertions.fail("Error reading directory: " + e.getMessage());
        }

        return testCases;
    }
}
