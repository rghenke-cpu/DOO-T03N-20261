public class UserProfile {
    private String username;

    public UserProfile() {
        this.username = "";
    }

    public UserProfile(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isConfigured() {
        return username != null && !username.trim().isEmpty();
    }
}
