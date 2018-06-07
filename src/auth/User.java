package auth;

import java.util.HashMap;
import java.util.Map;

//Model class for users
public class User {

    private String username, pswd;
    private Map<Integer, String> notes;

    private static User curUser = null;

    public User(String username, String pswd, Map<Integer, String> notes) {
        this.username = username;
        this.pswd = pswd;

        if (notes == null)
            this.notes = new HashMap<>();

        else
            this.notes = notes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public Map<Integer, String>  getNotes() {
        return notes;
    }

    public void setNotes(Map<Integer, String>  notes) {
        this.notes = notes;
    }

    public static User getCurrentUser() {
        return curUser;
    }

    public static void setCurrentUser(User curUser) {
        User.curUser = curUser;
    }
}
