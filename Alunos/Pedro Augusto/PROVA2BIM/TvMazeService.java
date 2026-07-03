import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

public class TvMazeService {
    private static final String BASE_URL = "https://api.tvmaze.com";

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    public List<Show> searchShows(String query) throws IOException {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
        String url = BASE_URL + "/search/shows?q=" + encoded;
        return parseSearchResults(get(url));
    }

    private String get(String url) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", "TVTracker-Java/1.0")
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(
                request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            int status = response.statusCode();
            if (status == 429) throw new IOException("Limite de requisições atingido. Aguarde alguns segundos.");
            if (status != 200)  throw new IOException("Erro na API: HTTP " + status);

            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Requisição interrompida.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Show> parseSearchResults(String json) {
        List<Show> shows = new ArrayList<>();

        Object parsed = new JsonParser(json).parse();
        if (!(parsed instanceof List)) return shows;

        for (Object item : (List<Object>) parsed) {
            if (!(item instanceof Map)) continue;
            Map<String, Object> entry = (Map<String, Object>) item;
            Object showObj = entry.get("show");
            if (showObj instanceof Map) {
                Show show = parseShow((Map<String, Object>) showObj);
                if (show != null) shows.add(show);
            }
        }
        return shows;
    }

    @SuppressWarnings("unchecked")
    private Show parseShow(Map<String, Object> map) {
        try {
            Show show = new Show();

            Object idObj = map.get("id");
            if (idObj instanceof Number) show.setId(((Number) idObj).intValue());

            show.setName(getString(map, "name"));
            show.setLanguage(getString(map, "language"));
            show.setStatus(getString(map, "status"));
            show.setPremiered(getString(map, "premiered"));
            show.setEnded(getString(map, "ended"));

            Object ratingObj = map.get("rating");
            if (ratingObj instanceof Map) {
                Map<String, Object> ratingMap = (Map<String, Object>) ratingObj;
                Object avg = ratingMap.get("average");
                if (avg instanceof Number) show.setRating(((Number) avg).doubleValue());
            }

            Object networkObj = map.get("network");
            if (networkObj instanceof Map) {
                show.setNetwork(getString((Map<String, Object>) networkObj, "name"));
            } else {
                Object webChannel = map.get("webChannel");
                if (webChannel instanceof Map) {
                    show.setNetwork(getString((Map<String, Object>) webChannel, "name"));
                }
            }

            Object genresObj = map.get("genres");
            if (genresObj instanceof List) {
                List<String> genres = new ArrayList<>();
                for (Object g : (List<Object>) genresObj) {
                    if (g != null) genres.add(g.toString());
                }
                show.setGenres(genres);
            }

            String summary = getString(map, "summary");
            show.setSummary(stripHtml(summary));

            Object imageObj = map.get("image");
            if (imageObj instanceof Map) {
                String imgUrl = getString((Map<String, Object>) imageObj, "medium");
                show.setImageUrl(imgUrl);
            }

            return show;
        } catch (Exception e) {
            return null;
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }

    @SuppressWarnings("unchecked")
    public String fetchImageUrl(int showId) throws IOException {
        String json = get(BASE_URL + "/shows/" + showId);
        Map<String, Object> map = JsonParser.parseObject(json);
        Object imageObj = map.get("image");
        if (imageObj instanceof Map) {
            Map<String, Object> imageMap = (Map<String, Object>) imageObj;
            Object medium = imageMap.get("medium");
            if (medium != null) return medium.toString();
        }
        return null;
    }

    private String stripHtml(String html) {
        if (html == null || html.isEmpty()) return "";
        return html.replaceAll("<[^>]+>", "").trim();
    }
}
