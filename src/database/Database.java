package database;

import hash.BCrypt;
import com.mysql.jdbc.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private static Database db = null;

    private Database() {

    }

    public static Database getInstance() {
        if (db == null)
            db = new Database();
        return db;
    }

    public Connection createConnection(String[] args) throws SQLException {
        Connection con = DriverManager.getConnection(args[0], args[1], args[2]);
        return con;
    }

    public boolean checkLogin(Connection con, String username, String passwd) {
        boolean success;
        String hashedPswd = BCrypt.hashpw(passwd, BCrypt.gensalt(12));
        System.out.println(hashedPswd);

        try {
            String query = "select pswd from users where username='"+username+"';";
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(query);

            if (!rs.next()) {
                success = false;
            }
            else {
                String pswddb = rs.getString("pswd");

                return BCrypt.checkpw(passwd, pswddb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    public boolean checkUsernameExists(Connection con, String username) {
        boolean isExists = true;
        try {
            String query = "select username from users WHERE username='" + username +"';";
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(query);

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return isExists;
    }

    public boolean createUser(Connection con, String username, String passwd) {
        boolean success;
        String hashedPswd = BCrypt.hashpw(passwd, BCrypt.gensalt());

        try {
            String query = "insert into users values (NULL, '" + username + "', '" + hashedPswd + "');";
            Statement s = con.createStatement();

            s.executeUpdate(query);

            success = true;
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    public boolean deleteUser(Connection con, String username, String passwd) {
        boolean success;
        String hashedPswd = BCrypt.hashpw(passwd, BCrypt.gensalt());

        try {
            String query = "delete from users where username=? and pswd=?;";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, username);
            ps.setString(2, hashedPswd);
            ResultSet rs = ps.executeQuery();

            success = true;
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    public String fetchNote(Connection con, int id) {
        String note = "";
        try {
            String query = "select note from notes where id="+id+";";
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                note = rs.getString("note");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return note;
    }

    public Map<Integer, String> fetchNotes(Connection con, String username) {
        Map<Integer, String> notes = new HashMap<>();

        try {
            String query = "select id, note from notes where who='"+username+"';";
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                notes.put(rs.getInt("id"), rs.getString("note"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notes;
    }

    public boolean deleteNote(Connection con, int id) {
        boolean success;

        try {
            String query = "delete from notes where id=?;";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, id);
            ps.executeUpdate();

            success = true;
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    public boolean uploadNote(Connection con, String username, String desc) {
        boolean success;

        try {
            String query = "insert into notes values (NULL, ?, ?, ?, ?, ?);";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, username);
            ps.setString(2, desc);
            ps.setString(3, "");
            ps.setString(4, "");
            ps.setString(5, "");
            ps.executeUpdate();

            success = true;
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    public boolean updateNote(Connection con, int id, String content, boolean backup, String theme) {
        boolean success;

        try {
            String toBackup = fetchNote(con, id);

            if (!toBackup.equals(content) && backup) {
                String query0 = "update notes set backup=? where id=?;";
                PreparedStatement prs = con.prepareStatement(query0);

                prs.setString(1, toBackup);
                prs.setInt(2, id);
                prs.executeUpdate();
            }

            String query1 = "update notes set note=?, theme=? where id=?;";
            PreparedStatement ps = con.prepareStatement(query1);

            ps.setString(1, content);
            ps.setInt(3, id);
            ps.setString(2, theme);
            ps.executeUpdate();

            success = true;
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    public String loadFromBackup(Connection con, int id) {
        String backupNote = "";

        try {
            String fetchQuery = "select backup from notes where id='" + id + "';";
            Statement fetchStmt = con.createStatement();

            String updateQuery = "update notes set backup=? where id=?;";
            PreparedStatement updateStmt = con.prepareStatement(updateQuery);

            updateStmt.setString(1, "");
            updateStmt.setInt(2, id);

            ResultSet rs = fetchStmt.executeQuery(fetchQuery);

            while (rs.next())
                backupNote = rs.getString("backup");

            updateStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return backupNote;
    }

    public int findID(Connection con, String content) {
        int id = 0;
        try {
            String query = "select id from notes where note='"+content+"';";
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                id = rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public String fetchProperties(Connection con, int id) {
        String properties = "";
        try {
            String query = "select properties from notes where id="+id+";";
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                properties = rs.getString("properties");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return properties;
    }

    public boolean updateProperties(Connection con, int id, String properties) {
        boolean success;

        try {
            String query1 = "update notes set properties=? where id=?;";
            PreparedStatement ps = con.prepareStatement(query1);

            ps.setString(1, properties);
            ps.setInt(2, id);
            ps.executeUpdate();

            success = true;
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    public String fetchTheme(Connection con, int id) {
        String theme = "";
        try {
            String query = "select theme from notes where id="+id+";";
            Statement s = con.createStatement();

            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                theme = rs.getString("theme");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return theme;
    }

    public boolean updateTheme(Connection con, int id, String theme) {
        boolean success;

        try {
            String query1 = "update notes set theme=? where id=?;";
            PreparedStatement ps = con.prepareStatement(query1);

            ps.setString(1, theme);
            ps.setInt(2, id);
            ps.executeUpdate();

            success = true;
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
