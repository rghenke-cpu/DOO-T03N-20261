import java.util.List;
import java.util.ArrayList;

public class Show {
    private int id;
    private String name;
    private String language;
    private List<String> genres;
    private double rating;
    private String status;
    private String premiered;
    private String ended;
    private String network;
    private String summary;
    private String imageUrl;

    public Show() {
        this.genres = new ArrayList<>();
    }

    public Show(int id, String name, String language, List<String> genres,
                double rating, String status, String premiered, String ended,
                String network, String summary, String imageUrl) {
        this.id = id;
        this.name = name;
        this.language = language;
        this.genres = genres != null ? genres : new ArrayList<>();
        this.rating = rating;
        this.status = status;
        this.premiered = premiered;
        this.ended = ended;
        this.network = network;
        this.summary = summary;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPremiered() { return premiered; }
    public void setPremiered(String premiered) { this.premiered = premiered; }

    public String getEnded() { return ended; }
    public void setEnded(String ended) { this.ended = ended; }

    public String getNetwork() { return network; }
    public void setNetwork(String network) { this.network = network; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getGenresAsString() {
        if (genres == null || genres.isEmpty()) return "N/A";
        return String.join(", ", genres);
    }

    public String getRatingAsString() {
        if (rating <= 0) return "N/A";
        return String.format("%.1f", rating);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Show)) return false;
        Show other = (Show) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
