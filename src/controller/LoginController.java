package controller;

import auth.User;
import database.Database;
import hash.BCrypt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import screens.NoteFrame;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private Database db;
    private Connection con;
    private final String[] args = {"jdbc:mysql://localhost:4242", "aeren", "11471147"};

    @FXML private TextField username;
    @FXML private TextField pswd;

    @FXML private Button login;
    @FXML private Button register;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            db = Database.getInstance();
            con = db.createConnection(args);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleAction(ActionEvent e) {
        if (e.getSource() == login) {
            String usrname = username.getText();
            String paswd = pswd.getText();

            if (paswd == null || paswd.isEmpty() || usrname.isEmpty() || usrname == null)
                return;

            if (db.checkLogin(con, usrname, paswd)) {
                User user = new User(usrname, BCrypt.hashpw(paswd, BCrypt.gensalt()), db.fetchNotes(con, usrname));
                User.setCurrentUser(user);

                try {
                    if (!User.getCurrentUser().getNotes().isEmpty())
                        for (Map.Entry<Integer, String> entry : User.getCurrentUser().getNotes().entrySet()) {
                            new NoteFrame(entry.getValue(), entry.getKey()).start(new Stage());
                        }
                    else
                        new NoteFrame().start(new Stage());

                    Stage s = (Stage) login.getScene().getWindow();
                    s.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            String usrname = username.getText();
            String paswd = pswd.getText();

            if (paswd == null || paswd.isEmpty() || usrname.isEmpty() || usrname == null)
                return;

            if (db.createUser(con, usrname, paswd)) {
                User user = new User(usrname, BCrypt.hashpw(paswd, BCrypt.gensalt()), db.fetchNotes(con, usrname));
                User.setCurrentUser(user);

                try {
                    new NoteFrame().start(new Stage());

                    Stage s = (Stage) login.getScene().getWindow();
                    s.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
