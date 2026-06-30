

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    public static int findMatchingBrace(String text, int start) {
        int depth = 0;
        boolean inString = false;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && (i == 0 || text.charAt(i - 1) != '\\')) inString = !inString;
            if (!inString) {
                if (c == '{') depth++;
                else if (c == '}') {
                    depth--;
                    if (depth == 0) return i;
                }
            }
        }
        return -1;
    }

    public static int findMatchingBracket(String text, int start) {
        int depth = 0;
        boolean inString = false;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && (i == 0 || text.charAt(i - 1) != '\\')) inString = !inString;
            if (!inString) {
                if (c == '[') depth++;
                else if (c == ']') {
                    depth--;
                    if (depth == 0) return i;
                }
            }
        }
        return -1;
    }

    public static String extractString(String json, String key) {
        if (json == null || key == null) return "";
        String busca = "\"" + key + "\":";
        int pos = json.indexOf(busca);
        if (pos == -1) return "";
        pos += busca.length();
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++;
        if (pos >= json.length()) return "";
        if (json.charAt(pos) == '"') {
            int fim = pos + 1;
            while (fim < json.length()) {
                char c = json.charAt(fim);
                if (c == '"' && json.charAt(fim - 1) != '\\') {
                    return unescape(json.substring(pos + 1, fim));
                }
                fim++;
            }
        }
        return "";
    }

    public static int extractInt(String json, String key) {
        try { return Integer.parseInt(extractPrimitive(json, key)); }
        catch (NumberFormatException e) { return 0; }
    }

    public static double extractDouble(String json, String key) {
        if (json == null || key == null) return 0.0;
        String busca = "\"" + key + "\":";
        int pos = json.indexOf(busca);
        if (pos == -1) return 0.0;
        pos += busca.length();
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++;
        int fim = pos;
        while (fim < json.length() && (Character.isDigit(json.charAt(fim)) || json.charAt(fim) == '.')) fim++;
        try { return Double.parseDouble(json.substring(pos, fim)); }
        catch (Exception e) { return 0.0; }
    }

    public static String extractPrimitive(String json, String key) {
        if (json == null || key == null) return "";
        String busca = "\"" + key + "\":";
        int pos = json.indexOf(busca);
        if (pos == -1) return "";
        pos += busca.length();
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) pos++;
        int fim = pos;
        while (fim < json.length() && json.charAt(fim) != ',' && json.charAt(fim) != '}' && json.charAt(fim) != ']') fim++;
        return json.substring(pos, fim).trim();
    }

    public static String extractJsonObject(String json, String key) {
        if (json == null || key == null) return null;
        String busca = "\"" + key + "\":";
        int pos = json.indexOf(busca);
        if (pos == -1) return null;
        pos = json.indexOf('{', pos);
        if (pos == -1) return null;
        int fim = findMatchingBrace(json, pos);
        if (fim == -1) return null;
        return json.substring(pos, fim + 1);
    }

    public static List<String> extractStringArray(String json, String key) {
        List<String> values = new ArrayList<>();
        if (json == null || key == null) return values;
        String busca = "\"" + key + "\":";
        int pos = json.indexOf(busca);
        if (pos == -1) return values;
        pos = json.indexOf('[', pos);
        if (pos == -1) return values;
        int fim = findMatchingBracket(json, pos);
        if (fim == -1) return values;
        String array = json.substring(pos + 1, fim);
        String[] partes = array.split(",");
        for (String parte : partes) {
            parte = parte.trim();
            if (parte.startsWith("\"") && parte.endsWith("\"") && parte.length() > 1) {
                values.add(unescape(parte.substring(1, parte.length() - 1)));
            }
        }
        return values;
    }

    public static String stripHtml(String texto) {
        if (texto == null) return "";
        return texto.replaceAll("<[^>]*>", "").trim();
    }

    public static String toJsonString(String value) {
        if (value == null) return "\"\"";
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: sb.append(c);
            }
        }
        sb.append('"');
        return sb.toString();
    }

    public static String stringListToJson(List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < list.size(); i++) {
            sb.append(toJsonString(list.get(i)));
            if (i < list.size() - 1) sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    private static String unescape(String value) {
        return value.replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t").replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
