

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TvMazeService {
    private static final String API_URL = "https://api.tvmaze.com/search/shows?q=%s";

    public List<Show> buscarSeries(String termo) throws IOException {
        String termoCodificado = URLEncoder.encode(termo, StandardCharsets.UTF_8.toString());
        URL url = new URL(String.format(API_URL, termoCodificado));
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("GET");
        conexao.setConnectTimeout(10000);
        conexao.setReadTimeout(10000);

        BufferedReader leitor = null;
        try {
            int codigo = conexao.getResponseCode();
            InputStream input = (codigo == 200) ? conexao.getInputStream() : conexao.getErrorStream();
            if (input == null) throw new IOException("Erro HTTP: " + codigo);

            leitor = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            StringBuilder resposta = new StringBuilder();
            String linha;
            while ((linha = leitor.readLine()) != null) resposta.append(linha);

            if (codigo != 200) throw new IOException("Erro HTTP: " + codigo + " - " + resposta);
            return parseSearchResponse(resposta.toString());
        } finally {
            if (leitor != null) leitor.close();
            conexao.disconnect();
        }
    }

    private List<Show> parseSearchResponse(String json) {
        List<Show> results = new ArrayList<>();
        int pos = 0;
        while (true) {
            int inicio = json.indexOf("\"show\":", pos);
            if (inicio == -1) break;
            inicio = json.indexOf('{', inicio);
            if (inicio == -1) break;
            int fim = JsonUtils.findMatchingBrace(json, inicio);
            if (fim == -1) break;
            Show show = parseShow(json.substring(inicio, fim + 1));
            if (show != null) results.add(show);
            pos = fim + 1;
        }
        return results;
    }

    private Show parseShow(String json) {
        if (json == null || json.trim().isEmpty()) return null;
        Show show = new Show();
        show.setId(JsonUtils.extractInt(json, "id"));
        show.setName(JsonUtils.extractString(json, "name"));
        show.setLanguage(JsonUtils.extractString(json, "language"));
        show.setStatus(JsonUtils.extractString(json, "status"));
        show.setPremiered(JsonUtils.extractString(json, "premiered"));
        show.setEnded(JsonUtils.extractString(json, "ended"));
        show.setSummary(JsonUtils.stripHtml(JsonUtils.extractString(json, "summary")));
        show.setNetwork(JsonUtils.extractString(JsonUtils.extractJsonObject(json, "network"), "name"));
        if (show.getNetwork().trim().isEmpty()) {
            show.setNetwork(JsonUtils.extractString(JsonUtils.extractJsonObject(json, "webChannel"), "name"));
        }
        show.setRating(JsonUtils.extractDouble(JsonUtils.extractJsonObject(json, "rating"), "average"));
        show.setGenres(JsonUtils.extractStringArray(json, "genres"));
        return show;
    }
}
