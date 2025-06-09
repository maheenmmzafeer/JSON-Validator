package Main;

import java.util.ArrayList;
import java.util.List;

public class JSONLexer {
    private final String input;
    private int pos = 0;
    private int depth = 0;
    private static final int MAX_DEPTH = 19;

    public JSONLexer(String input) throws Exception {
        this.input = input.trim();
        validateJsonStart();
    }

    public List<Token> tokenize() throws Exception {
        List<Token> tokens = new ArrayList<>();
        boolean expectColon = false;

        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }

            switch (c) {
                case '{': case '[':
                    depth++;
                    if (depth > MAX_DEPTH) {
                        throw new Exception("Invalid JSON: Too deeply nested at position " + pos);
                    }
                    tokens.add(new Token(c == '{' ? Token.Type.LEFT_BRACE : Token.Type.LEFT_BRACKET, String.valueOf(c)));
                    pos++;
                    expectColon = (c == '{');
                    break;
                case '}': case ']':
                    depth--;
                    tokens.add(new Token(c == '}' ? Token.Type.RIGHT_BRACE : Token.Type.RIGHT_BRACKET, String.valueOf(c)));
                    pos++;
                    expectColon = false;
                    break;
                case ':':
                    tokens.add(new Token(Token.Type.COLON, ":"));
                    pos++;
                    expectColon = false;
                    break;
                case ',':
                    tokens.add(new Token(Token.Type.COMMA, ","));
                    pos++;
                    expectColon = false;
                    break;
                case '"':
                    tokens.add(new Token(Token.Type.STRING, extractString()));
                    expectColon = true;
                    break;
                default:
                    if (Character.isDigit(c) || c == '-') {
                        if (expectColon)
                            throw new Exception("Expected colon before value at position " + pos);
                        tokens.add(new Token(Token.Type.NUMBER, extractNumber()));
                    } else if (input.startsWith("true", pos)) {
                        tokens.add(new Token(Token.Type.BOOLEAN, "true"));
                        pos += 4;
                    } else if (input.startsWith("false", pos)) {
                        tokens.add(new Token(Token.Type.BOOLEAN, "false"));
                        pos += 5;
                    } else if (input.startsWith("null", pos)) {
                        tokens.add(new Token(Token.Type.NULL, "null"));
                        pos += 4;
                    } else {
                        throw new Exception("Unexpected character at position " + pos + ": " + c);
                    }
                    expectColon = false;
            }
        }

        tokens.add(new Token(Token.Type.END, ""));
        return tokens;
    }

    private void validateJsonStart() throws Exception {
        if (!(input.startsWith("{") || input.startsWith("["))) {
            throw new Exception("Invalid JSON: A JSON payload should be an object or array, not a string.");
        }
    }

    private String extractString() throws Exception {
        int start = ++pos;
        StringBuilder sb = new StringBuilder();

        while (pos < input.length()) {
            char ch = input.charAt(pos);

            if (ch == '"') {
                // Closing quote found, return the extracted string
                pos++;
                return sb.toString();
            }

            if (ch == '\\') {
                sb.append(handleEscapeSequence());
                continue;
            }

            if (ch == '\t' || ch == '\n' || ch == '\r') {
                throw new Exception("Illegal character in string at position " + pos);
            }

            sb.append(ch);
            pos++;
        }

        throw new Exception("Unterminated string starting at position " + start);
    }

    private String handleEscapeSequence() throws Exception {
        if (pos + 1 >= input.length()) {
            throw new Exception("Unterminated escape sequence at position " + pos);
        }
        char nextChar = input.charAt(pos + 1);
        pos += 2;

        switch (nextChar) {
            case '"':
                return "\"";
            case '\\':
                return "\\";
            case '/':
                return "/";
            case 'b':
                return "\b";
            case 'f':
                return "\f";
            case 'n':
                return "\n";
            case 'r':
                return "\r";
            case 't':
                return "\t";
            case 'u':
                if (pos + 5 >= input.length()) {
                    throw new Exception("Incomplete Unicode escape sequence at position " + pos);
                }
                String hex = input.substring(pos, pos + 4);
                try {
                    pos += 4;
                    return String.valueOf((char) Integer.parseInt(hex, 16));
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid Unicode escape sequence at position " + (pos - 4));
                }
            default:
                throw new Exception("Illegal escape sequence at position " + pos);
        }
    }

    private String extractNumber() throws Exception {
        int start = pos;
        boolean hasDecimal = false, hasExponent = false;

        if (input.charAt(pos) == '-' || input.charAt(pos) == '+') {
            pos++;
        }

        if (input.charAt(pos) == '0' && pos + 1 < input.length() &&
                (Character.isDigit(input.charAt(pos + 1)) || input.charAt(pos + 1) == 'x')) {
            throw new Exception("Invalid number format: Leading zero or hexadecimal at position " + pos);
        }

        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isDigit(c)) {
                pos++;
            } else if (c == '.') {
                if (hasDecimal) throw new Exception("Multiple decimal points at position " + pos);
                hasDecimal = true;
                pos++;
            } else if (c == 'e' || c == 'E') {
                if (hasExponent) throw new Exception("Multiple exponents at position " + pos);
                hasExponent = true;
                pos++;
                if (pos < input.length() && (input.charAt(pos) == '+' || input.charAt(pos) == '-')) {
                    pos++;
                }
                if (pos >= input.length() || !Character.isDigit(input.charAt(pos))) {
                    throw new Exception("Invalid exponent format at position " + pos);
                }
            } else {
                break;
            }
        }

        char lastChar = input.charAt(pos - 1);
        if (lastChar == '.' || lastChar == 'e' || lastChar == 'E') {
            throw new Exception("Invalid number format at position " + (pos - 1));
        }

        return input.substring(start, pos);
    }
}