import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PersistenceService {
    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = DATA_DIR + File.separator + "userdata.json";

    public void save(UserData userData) throws IOException {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        String json = JsonWriter.toJson(toMap(userData));

        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(DATA_FILE), StandardCharsets.UTF_8)) {
            writer.write(json);
        }
    }

    public UserData load() throws IOException {
        File file = new File(DATA_FILE);
        if (!file.exists()) return new UserData();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }

        Map<String, Object> map = JsonParser.parseObject(sb.toString());
        return fromMap(map);
    }

    private Map<String, Object> toMap(UserData userData) {
        Map<String, Object> root = new LinkedHashMap<>();

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("username", userData.getProfile().getUsername());
        root.put("profile", profile);

        root.put("favorites", showListToJson(userData.getFavorites()));
        root.put("watched",   showListToJson(userData.getWatched()));
        root.put("watchlist", showListToJson(userData.getWatchlist()));

        return root;
    }

    private List<Map<String, Object>> showListToJson(List<Show> shows) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Show show : shows) list.add(showToMap(show));
        return list;
    }

    private Map<String, Object> showToMap(Show show) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",        show.getId());
        map.put("name",      show.getName());
        map.put("language",  show.getLanguage());
        map.put("genres",    show.getGenres());
        map.put("rating",    show.getRating());
        map.put("status",    show.getStatus());
        map.put("premiered", show.getPremiered());
        map.put("ended",     show.getEnded());
        map.put("network",   show.getNetwork());
        map.put("summary",   show.getSummary());
        map.put("imageUrl",  show.getImageUrl());
        return map;
    }

    private UserData fromMap(Map<String, Object> root) {
        UserData userData = new UserData();

        Object profileObj = root.get("profile");
        if (profileObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> profileMap = (Map<String, Object>) profileObj;
            String username = getString(profileMap, "username");
            userData.setProfile(new UserProfile(username));
        }

        userData.setFavorites(parseShowList(root.get("favorites")));
        userData.setWatched(parseShowList(root.get("watched")));
        userData.setWatchlist(parseShowList(root.get("watchlist")));

        return userData;
    }

    @SuppressWarnings("unchecked")
    private List<Show> parseShowList(Object obj) {
        List<Show> shows = new ArrayList<>();
        if (!(obj instanceof List)) return shows;
        for (Object item : (List<Object>) obj) {
            if (item instanceof Map) {
                shows.add(showFromMap((Map<String, Object>) item));
            }
        }
        return shows;
    }

    @SuppressWarnings("unchecked")
    private Show showFromMap(Map<String, Object> map) {
        Show show = new Show();
        show.setId((int) getDouble(map, "id"));
        show.setName(getString(map, "name"));
        show.setLanguage(getString(map, "language"));
        show.setRating(getDouble(map, "rating"));
        show.setStatus(getString(map, "status"));
        show.setPremiered(getString(map, "premiered"));
        show.setEnded(getString(map, "ended"));
        show.setNetwork(getString(map, "network"));
        show.setSummary(getString(map, "summary"));
        show.setImageUrl(getString(map, "imageUrl"));

        Object genresObj = map.get("genres");
        if (genresObj instanceof List) {
            List<String> genres = new ArrayList<>();
            for (Object g : (List<Object>) genresObj) {
                if (g != null) genres.add(g.toString());
            }
            show.setGenres(genres);
        }

        return show;
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }

    private double getDouble(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        return 0;
    }
}
