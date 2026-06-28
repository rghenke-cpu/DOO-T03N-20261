package tvmanager.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tvmanager.model.Elenco;
import tvmanager.model.Episodio;
import tvmanager.model.Serie;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente para a API pública TVMaze.
 * Responsável exclusivamente por requisições HTTP e parsing JSON.
 */
public class TVMazeClient {

    private static final String BASE_URL = "https://api.tvmaze.com";

    /** Faz uma requisição GET e retorna o corpo como String. Retorna null em 404. */
    private String get(String endpoint) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);

        int code = conn.getResponseCode();
        if (code == 404) return null;
        if (code != 200) throw new Exception("Erro HTTP " + code);

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String linha;
            while ((linha = br.readLine()) != null) sb.append(linha);
            return sb.toString();
        }
    }

    public List<Serie> buscarSeries(String query) throws Exception {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String json = get("/search/shows?q=" + encoded);
        List<Serie> lista = new ArrayList<>();
        if (json == null) return lista;

        JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject().getAsJsonObject("show");
            lista.add(parseSerie(obj));
        }
        return lista;
    }

    public Serie buscarSeriePorId(int id) throws Exception {
        String json = get("/shows/" + id);
        if (json == null) return null;
        return parseSerie(JsonParser.parseString(json).getAsJsonObject());
    }

    public List<Episodio> buscarEpisodios(int serieId) throws Exception {
        String json = get("/shows/" + serieId + "/episodes");
        List<Episodio> lista = new ArrayList<>();
        if (json == null) return lista;

        for (JsonElement el : JsonParser.parseString(json).getAsJsonArray()) {
            lista.add(parseEpisodio(el.getAsJsonObject()));
        }
        return lista;
    }

    public List<Elenco> buscarElenco(int serieId) throws Exception {
        String json = get("/shows/" + serieId + "/cast");
        List<Elenco> lista = new ArrayList<>();
        if (json == null) return lista;

        for (JsonElement el : JsonParser.parseString(json).getAsJsonArray()) {
            JsonObject obj = el.getAsJsonObject();
            Elenco e = new Elenco();

            JsonObject person = obj.getAsJsonObject("person");
            e.setNomeAtor(getString(person, "name"));
            if (person.has("image") && !person.get("image").isJsonNull()) {
                e.setImagemUrl(getString(person.getAsJsonObject("image"), "medium"));
            }
            e.setNomePersonagem(getString(obj.getAsJsonObject("character"), "name"));
            lista.add(e);
        }
        return lista;
    }

    // --- Parsing ---

    private Serie parseSerie(JsonObject obj) {
        Serie s = new Serie();
        s.setId(obj.get("id").getAsInt());
        s.setNome(getString(obj, "name"));
        s.setStatus(getString(obj, "status"));
        s.setPremiada(getString(obj, "premiered"));
        s.setDataTermino(getString(obj, "ended"));
        s.setIdioma(getString(obj, "language"));

        // Sinopse: remover tags HTML
        String summary = getString(obj, "summary");
        if (summary != null) summary = summary.replaceAll("<[^>]*>", "").trim();
        s.setSinopse(summary);

        // Gêneros
        if (obj.has("genres") && obj.get("genres").isJsonArray()) {
            StringBuilder genres = new StringBuilder();
            for (JsonElement g : obj.getAsJsonArray("genres")) {
                if (genres.length() > 0) genres.append(", ");
                genres.append(g.getAsString());
            }
            s.setGeneros(genres.toString());
        }

        // Rating
        if (obj.has("rating") && !obj.get("rating").isJsonNull()) {
            JsonObject rating = obj.getAsJsonObject("rating");
            if (rating.has("average") && !rating.get("average").isJsonNull()) {
                s.setRating(rating.get("average").getAsDouble());
            }
        }

        // Imagem
        if (obj.has("image") && !obj.get("image").isJsonNull()) {
            s.setImagemUrl(getString(obj.getAsJsonObject("image"), "medium"));
        }

        // Emissora: tenta "network" primeiro, depois "webChannel"
        if (obj.has("network") && !obj.get("network").isJsonNull()) {
            s.setEmissora(getString(obj.getAsJsonObject("network"), "name"));
        } else if (obj.has("webChannel") && !obj.get("webChannel").isJsonNull()) {
            s.setEmissora(getString(obj.getAsJsonObject("webChannel"), "name"));
        }

        return s;
    }

    private Episodio parseEpisodio(JsonObject obj) {
        Episodio e = new Episodio();
        e.setId(obj.get("id").getAsInt());
        e.setNome(getString(obj, "name"));
        e.setTemporada(obj.has("season") && !obj.get("season").isJsonNull() ? obj.get("season").getAsInt() : 0);
        e.setNumero(obj.has("number") && !obj.get("number").isJsonNull() ? obj.get("number").getAsInt() : 0);
        e.setDataAr(getString(obj, "airdate"));
        e.setDuracao(obj.has("runtime") && !obj.get("runtime").isJsonNull() ? obj.get("runtime").getAsInt() : 0);

        String summary = getString(obj, "summary");
        if (summary != null) summary = summary.replaceAll("<[^>]*>", "").trim();
        e.setSinopse(summary);

        if (obj.has("rating") && !obj.get("rating").isJsonNull()) {
            JsonObject rating = obj.getAsJsonObject("rating");
            if (rating.has("average") && !rating.get("average").isJsonNull()) {
                e.setRating(rating.get("average").getAsDouble());
            }
        }
        if (obj.has("image") && !obj.get("image").isJsonNull()) {
            e.setImagemUrl(getString(obj.getAsJsonObject("image"), "medium"));
        }
        return e;
    }

    private String getString(JsonObject obj, String key) {
        if (obj != null && obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }
}
