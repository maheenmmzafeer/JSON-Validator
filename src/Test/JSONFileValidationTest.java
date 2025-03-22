package Test;

import Main.JSONLexer;
import Main.JSONParser;
import Main.Token;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class JSONFileValidationTest {
    private final Path filePath;
    private final boolean shouldBeValid;

    public JSONFileValidationTest(Path filePath, boolean shouldBeValid) {
        this.filePath = filePath;
        this.shouldBeValid = shouldBeValid;
    }

    @Parameterized.Parameters(name = "{index}: Testing {0}")
    public static Collection<Object[]> data() {
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
            fail("Error reading directory: " + e.getMessage());
        }

        return testCases;
    }

    @Test
    public void testJsonFileValidation() {
        try {
            String jsonContent = new String(Files.readAllBytes(filePath));
            JSONLexer lexer = new JSONLexer(jsonContent);
            List<Token> tokens = lexer.tokenize();
            JSONParser parser = new JSONParser(tokens);
            parser.parse();

            assertTrue("Expected valid JSON, but got invalid JSON: " + filePath, shouldBeValid);
        } catch (Exception e) {
            assertTrue("Expected invalid JSON, but got valid JSON: " + filePath, !shouldBeValid);
        }
    }
}
