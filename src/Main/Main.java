package Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        String baseDir = System.getProperty("user.dir") + "/resources/";
        System.out.println("Base Directory: " + baseDir);

        try (Stream<Path> paths = Files.walk(Paths.get(baseDir))) {
            paths
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".json"))
                    .forEach(Main::processJsonFile);
        } catch (IOException e) {
            System.out.println("Error reading directory: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void processJsonFile(Path filePath) {
        String fileName = filePath.getFileName().toString();

        try {
            String jsonContent = new String(Files.readAllBytes(filePath));

            JSONLexer lexer = new JSONLexer(jsonContent);
            List<Token> tokens = lexer.tokenize();
            JSONParser parser = new JSONParser(tokens);
            parser.parse();
            System.out.println(fileName + ": Valid JSON");

        } catch (IOException e) {
            System.out.println("Error reading file: " + fileName + " - " + e.getMessage());

        } catch (Exception e) {
            System.out.println("Invalid JSON: " + fileName + " - " + e.getMessage());
        }
    }
}
