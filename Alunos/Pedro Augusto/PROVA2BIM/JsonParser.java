import java.util.*;

public class JsonParser {
    private final String json;
    private int pos;

    public JsonParser(String json) {
        this.json = json.trim();
        this.pos = 0;
    }

    public Object parse() {
        skipWhitespace();
        char c = current();
        if (c == '{') return parseObject();
        if (c == '[') return parseArray();
        if (c == '"') return parseString();
        if (c == 't' || c == 'f') return parseBoolean();
        if (c == 'n') { parseNull(); return null; }
        return parseNumber();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String json) {
        Object result = new JsonParser(json).parse();
        if (result instanceof Map) return (Map<String, Object>) result;
        return new LinkedHashMap<>();
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        pos++; // skip {
        skipWhitespace();
        if (current() == '}') { pos++; return map; }
        while (pos < json.length()) {
            skipWhitespace();
            String key = parseString();
            skipWhitespace();
            pos++; // skip :
            skipWhitespace();
            Object value = parse();
            map.put(key, value);
            skipWhitespace();
            if (current() == '}') { pos++; break; }
            pos++; // skip ,
        }
        return map;
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        pos++; // skip [
        skipWhitespace();
        if (current() == ']') { pos++; return list; }
        while (pos < json.length()) {
            skipWhitespace();
            list.add(parse());
            skipWhitespace();
            if (current() == ']') { pos++; break; }
            pos++; // skip ,
        }
        return list;
    }

    private String parseString() {
        pos++; // skip opening "
        StringBuilder sb = new StringBuilder();
        while (pos < json.length()) {
            char c = json.charAt(pos);
            if (c == '\\') {
                pos++;
                char esc = json.charAt(pos);
                switch (esc) {
                    case '"':  sb.append('"');  break;
                    case '\\': sb.append('\\'); break;
                    case '/':  sb.append('/');  break;
                    case 'n':  sb.append('\n'); break;
                    case 'r':  sb.append('\r'); break;
                    case 't':  sb.append('\t'); break;
                    default:   sb.append(esc);
                }
            } else if (c == '"') {
                pos++;
                return sb.toString();
            } else {
                sb.append(c);
            }
            pos++;
        }
        return sb.toString();
    }

    private double parseNumber() {
        int start = pos;
        while (pos < json.length()) {
            char c = json.charAt(pos);
            if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) break;
            pos++;
        }
        String num = json.substring(start, pos);
        return Double.parseDouble(num);
    }

    private boolean parseBoolean() {
        if (json.startsWith("true", pos)) { pos += 4; return true; }
        pos += 5;
        return false;
    }

    private void parseNull() { pos += 4; }

    private void skipWhitespace() {
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++;
    }

    private char current() {
        return pos < json.length() ? json.charAt(pos) : 0;
    }
}
