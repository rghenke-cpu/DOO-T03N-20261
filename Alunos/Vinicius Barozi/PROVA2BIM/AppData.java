

import java.util.ArrayList;
import java.util.List;

public class AppData {
    private String currentUser = "";
    private List<UserData> users = new ArrayList<>();

    public String getCurrentUser() { return currentUser; }
    public void setCurrentUser(String currentUser) { this.currentUser = currentUser == null ? "" : currentUser; }

    public List<UserData> getUsers() { return users; }
    public void setUsers(List<UserData> users) { this.users = users == null ? new ArrayList<>() : users; }
}
