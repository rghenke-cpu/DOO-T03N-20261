import java.util.ArrayList;
import java.util.List;

public class UserData {
    private UserProfile profile;
    private List<Show> favorites;
    private List<Show> watched;
    private List<Show> watchlist;

    public UserData() {
        this.profile = new UserProfile();
        this.favorites = new ArrayList<>();
        this.watched = new ArrayList<>();
        this.watchlist = new ArrayList<>();
    }

    public UserProfile getProfile() { return profile; }
    public void setProfile(UserProfile profile) { this.profile = profile; }

    public List<Show> getFavorites() { return favorites; }
    public void setFavorites(List<Show> favorites) { this.favorites = favorites; }

    public List<Show> getWatched() { return watched; }
    public void setWatched(List<Show> watched) { this.watched = watched; }

    public List<Show> getWatchlist() { return watchlist; }
    public void setWatchlist(List<Show> watchlist) { this.watchlist = watchlist; }

    public boolean isFavorite(Show show) {
        return favorites.stream().anyMatch(s -> s.getId() == show.getId());
    }

    public boolean isWatched(Show show) {
        return watched.stream().anyMatch(s -> s.getId() == show.getId());
    }

    public boolean isInWatchlist(Show show) {
        return watchlist.stream().anyMatch(s -> s.getId() == show.getId());
    }

    public void addFavorite(Show show) {
        if (!isFavorite(show)) favorites.add(show);
    }

    public void removeFavorite(Show show) {
        favorites.removeIf(s -> s.getId() == show.getId());
    }

    public void addWatched(Show show) {
        if (!isWatched(show)) watched.add(show);
    }

    public void removeWatched(Show show) {
        watched.removeIf(s -> s.getId() == show.getId());
    }

    public void addToWatchlist(Show show) {
        if (!isInWatchlist(show)) watchlist.add(show);
    }

    public void removeFromWatchlist(Show show) {
        watchlist.removeIf(s -> s.getId() == show.getId());
    }
}
