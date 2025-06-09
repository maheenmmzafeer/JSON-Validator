package Main;

import java.util.List;

public class JSONParser {
    private final List<Token> tokens;
    private int index = 0;

    public JSONParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void parse() throws Exception {
        parseValue();
        if (tokens.get(index).getType() != Token.Type.END) {
            throw new Exception("Unexpected token after JSON object at index " + index + ": " + tokens.get(index));
        }
    }

    private void parseValue() throws Exception {
        Token token = tokens.get(index);
        switch (token.getType()) {
            case LEFT_BRACE:
                parseObject();
                break;
            case LEFT_BRACKET:
                parseArray();
                break;
            case STRING:
            case NUMBER:
            case BOOLEAN:
            case NULL:
                index++;
                break;
            default:
                throw new Exception("Unexpected token at index " + index + ": " + token);
        }
    }

    private void parseObject() throws Exception {
        index++;
        if (tokens.get(index).getType() == Token.Type.RIGHT_BRACE) {
            index++;
            return;
        }
        while (true) {
            if (tokens.get(index).getType() != Token.Type.STRING) {
                throw new Exception("Expected string key at index " + index + ": " + tokens.get(index));
            }
            index++;
            if (tokens.get(index).getType() != Token.Type.COLON) {
                throw new Exception("Expected colon after key at index " + index + ": " + tokens.get(index));
            }
            index++;
            parseValue();
            if (tokens.get(index).getType() == Token.Type.RIGHT_BRACE) {
                index++;
                return;
            }
            if (tokens.get(index).getType() != Token.Type.COMMA) {
                throw new Exception("Expected comma between key-value pairs at index " + index + ": " + tokens.get(index));
            }
            index++;
        }
    }

    private void parseArray() throws Exception {
        index++;
        if (tokens.get(index).getType() == Token.Type.RIGHT_BRACKET) {
            index++;
            return;
        }
        while (true) {
            parseValue();
            if (tokens.get(index).getType() == Token.Type.RIGHT_BRACKET) {
                index++;
                return;
            }
            if (tokens.get(index).getType() != Token.Type.COMMA) {
                throw new Exception("Expected comma in array at index " + index + ": " + tokens.get(index));
            }
            index++;
        }
    }
}