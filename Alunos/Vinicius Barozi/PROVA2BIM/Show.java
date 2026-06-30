import java.util.ArrayList;
import java.util.List;

public class Show {
    private int id;
    private String name = "";
    private String language = "";
    private List<String> genres = new ArrayList<>();
    private double rating;
    private String status = "";
    private String premiered = "";
    private String ended = "";
    private String network = "";
    private String summary = "";

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = valueOrEmpty(name); }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = valueOrEmpty(language); }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres == null ? new ArrayList<>() : genres; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = valueOrEmpty(status); }

    public String getPremiered() { return premiered; }
    public void setPremiered(String premiered) { this.premiered = valueOrEmpty(premiered); }

    public String getEnded() { return ended; }
    public void setEnded(String ended) { this.ended = valueOrEmpty(ended); }

    public String getNetwork() { return network; }
    public void setNetwork(String network) { this.network = valueOrEmpty(network); }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = valueOrEmpty(summary); }

    public String getDisplayLabel() {
        String estado = "";
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ended")) {
            estado = " [" + status + "]";
        }
        return name + estado + " - Nota: " + String.format("%.1f", rating);
    }

    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome: ").append(name).append("\n");
        sb.append("Idioma: ").append(isEmpty(language) ? "-" : language).append("\n");
        sb.append("Gêneros: ").append(genres.isEmpty() ? "-" : String.join(", ", genres)).append("\n");
        sb.append("Nota: ").append(rating > 0 ? String.format("%.1f", rating) : "-").append("\n");
        sb.append("Estado: ").append(isEmpty(status) ? "-" : status).append("\n");
        sb.append("Estreia: ").append(isEmpty(premiered) ? "-" : premiered).append("\n");
        sb.append("Término: ").append(isEmpty(ended) ? "-" : ended).append("\n");
        sb.append("Emissora: ").append(isEmpty(network) ? "-" : network).append("\n\n");
        sb.append("Resumo:\n");
        sb.append(isEmpty(summary) ? "Sem resumo disponível." : summary).append("\n");
        return sb.toString();
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
