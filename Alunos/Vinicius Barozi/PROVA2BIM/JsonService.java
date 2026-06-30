

import java.util.ArrayList;
import java.util.List;

public class JsonService {
    public String toJson(AppData app) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"currentUser\":").append(JsonUtils.toJsonString(app.getCurrentUser())).append(',');
        sb.append("\"users\":").append(usersToJson(app.getUsers()));
        sb.append('}');
        return sb.toString();
    }

    public AppData parseAppData(String json) {
        AppData app = new AppData();
        String nome = JsonUtils.extractString(json, "currentUser");
        if (nome != null && !nome.trim().isEmpty()) app.setCurrentUser(nome);
        app.setUsers(parseUsers(json));

        if (app.getUsers().isEmpty()) {
            UserData usuario = new UserData();
            usuario.setName(app.getCurrentUser().trim().isEmpty() ? "Usuário" : app.getCurrentUser());
            app.getUsers().add(usuario);
        }
        if (app.getCurrentUser().trim().isEmpty()) app.setCurrentUser(app.getUsers().get(0).getName());
        return app;
    }

    private String usersToJson(List<UserData> users) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < users.size(); i++) {
            sb.append(userToJson(users.get(i)));
            if (i < users.size() - 1) sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    private String userToJson(UserData user) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"name\":").append(JsonUtils.toJsonString(user.getName())).append(',');
        sb.append("\"favorites\":").append(showsToJson(user.getFavorites())).append(',');
        sb.append("\"watched\":").append(showsToJson(user.getWatched())).append(',');
        sb.append("\"wantToWatch\":").append(showsToJson(user.getWantToWatch()));
        sb.append('}');
        return sb.toString();
    }

    private String showsToJson(List<Show> shows) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < shows.size(); i++) {
            sb.append(showToJson(shows.get(i)));
            if (i < shows.size() - 1) sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    private String showToJson(Show show) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"id\":").append(show.getId()).append(',');
        sb.append("\"name\":").append(JsonUtils.toJsonString(show.getName())).append(',');
        sb.append("\"language\":").append(JsonUtils.toJsonString(show.getLanguage())).append(',');
        sb.append("\"genres\":").append(JsonUtils.stringListToJson(show.getGenres())).append(',');
        sb.append("\"rating\":").append(show.getRating()).append(',');
        sb.append("\"status\":").append(JsonUtils.toJsonString(show.getStatus())).append(',');
        sb.append("\"premiered\":").append(JsonUtils.toJsonString(show.getPremiered())).append(',');
        sb.append("\"ended\":").append(JsonUtils.toJsonString(show.getEnded())).append(',');
        sb.append("\"network\":").append(JsonUtils.toJsonString(show.getNetwork())).append(',');
        sb.append("\"summary\":").append(JsonUtils.toJsonString(show.getSummary()));
        sb.append('}');
        return sb.toString();
    }

    private List<UserData> parseUsers(String json) {
        List<UserData> users = new ArrayList<>();
        int pos = json.indexOf("\"users\":[");
        if (pos == -1) return users;
        pos = json.indexOf('[', pos);
        int fim = JsonUtils.findMatchingBracket(json, pos);
        if (pos == -1 || fim == -1) return users;
        String array = json.substring(pos + 1, fim);
        int i = 0;
        while (i < array.length()) {
            int inicio = array.indexOf('{', i);
            if (inicio == -1) break;
            int fimObj = JsonUtils.findMatchingBrace(array, inicio);
            if (fimObj == -1) break;
            users.add(parseUsuario(array.substring(inicio, fimObj + 1)));
            i = fimObj + 1;
        }
        return users;
    }

    private UserData parseUsuario(String json) {
        UserData usuario = new UserData();
        usuario.setName(JsonUtils.extractString(json, "name"));
        usuario.setFavorites(parseShowList(json, "favorites"));
        usuario.setWatched(parseShowList(json, "watched"));
        usuario.setWantToWatch(parseShowList(json, "wantToWatch"));
        return usuario;
    }

    private List<Show> parseShowList(String json, String key) {
        List<Show> lista = new ArrayList<>();
        int pos = json.indexOf("\"" + key + "\":[");
        if (pos == -1) return lista;
        pos = json.indexOf('[', pos);
        int fim = JsonUtils.findMatchingBracket(json, pos);
        if (pos == -1 || fim == -1) return lista;
        String array = json.substring(pos + 1, fim);
        int i = 0;
        while (i < array.length()) {
            int inicio = array.indexOf('{', i);
            if (inicio == -1) break;
            int fimObj = JsonUtils.findMatchingBrace(array, inicio);
            if (fimObj == -1) break;
            lista.add(parseSavedShow(array.substring(inicio, fimObj + 1)));
            i = fimObj + 1;
        }
        return lista;
    }

    private Show parseSavedShow(String json) {
        Show show = new Show();
        show.setId(JsonUtils.extractInt(json, "id"));
        show.setName(JsonUtils.extractString(json, "name"));
        show.setLanguage(JsonUtils.extractString(json, "language"));
        show.setStatus(JsonUtils.extractString(json, "status"));
        show.setPremiered(JsonUtils.extractString(json, "premiered"));
        show.setEnded(JsonUtils.extractString(json, "ended"));
        show.setNetwork(JsonUtils.extractString(json, "network"));
        show.setSummary(JsonUtils.extractString(json, "summary"));
        show.setRating(JsonUtils.extractDouble(json, "rating"));
        show.setGenres(JsonUtils.extractStringArray(json, "genres"));
        return show;
    }
}
