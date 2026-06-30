
import java.util.ArrayList;
import java.util.List;

public class UserData {
    private String name = "";
    private List<Show> favorites = new ArrayList<>();
    private List<Show> watched = new ArrayList<>();
    private List<Show> wantToWatch = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? "" : name; }

    public List<Show> getFavorites() { return favorites; }
    public void setFavorites(List<Show> favorites) { this.favorites = favorites == null ? new ArrayList<>() : favorites; }

    public List<Show> getWatched() { return watched; }
    public void setWatched(List<Show> watched) { this.watched = watched == null ? new ArrayList<>() : watched; }

    public List<Show> getWantToWatch() { return wantToWatch; }
    public void setWantToWatch(List<Show> wantToWatch) { this.wantToWatch = wantToWatch == null ? new ArrayList<>() : wantToWatch; }
}
