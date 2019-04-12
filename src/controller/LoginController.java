package controller;

import auth.User;
import com.jfoenix.controls.JFXSnackbar;
import database.Database;
import hash.BCrypt;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
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
    private final String[] args = {"jdbc:mysql://remotemysql.com:3306/eipTeMBY7h", "eipTeMBY7h", "2kSyZuZRsP"};

    private JFXSnackbar snack;
    private boolean focus = true;

    @FXML private VBox root;

    @FXML private TextField username;
    @FXML private TextField pswd;

    @FXML private Button login;
    @FXML private Button register;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        new Thread(() -> {
            try {
                db = Database.getInstance();
                con = db.createConnection(args);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();

        snack = new JFXSnackbar(root);

        //Remove focus from textfield to see hint
        username.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && focus) {
                root.requestFocus();
                focus = false;
            }
        });
    }

    public void handleAction(ActionEvent e) {
        if (e.getSource() == login) {
            String usrname = username.getText();
            String paswd = pswd.getText();

            if (paswd == null || paswd.isEmpty() || usrname.isEmpty()) {
                snack.show("Fill the fields", 1500);
                return;
            }

            if (db.checkLogin(con, usrname, paswd)) {
                User user = new User(usrname, BCrypt.hashpw(paswd, BCrypt.gensalt()), db.fetchNotes(con, usrname));
                User.setCurrentUser(user);

                try {
                    if (!User.getCurrentUser().getNotes().isEmpty())
                        for (Map.Entry<Integer, String> entry : User.getCurrentUser().getNotes().entrySet()) {
                            String theme = db.fetchTheme(con, entry.getKey());
                            String[] properties = db.fetchProperties(con, entry.getKey()).split(",");
                            System.out.println(properties.length);
                            if (properties.length != 0 && properties.length != 1) {
                                double w = Double.parseDouble(properties[0]);
                                double h = Double.parseDouble(properties[1]);
                                double x = Double.parseDouble(properties[2]);
                                double y = Double.parseDouble(properties[3]);

                                if (theme.isEmpty())
                                    theme = "yellow_theme";

                                NoteFrame note = new NoteFrame(entry.getValue(), entry.getKey(), theme, w, h, x, y);
                                note.start(new Stage());
                            } else {
                                if (theme.isEmpty())
                                    theme = "yellow_theme";
                                NoteFrame note = new NoteFrame(entry.getValue(), entry.getKey(), theme, 240, 320, 0, 0);
                                note.start(new Stage());
                            }
                        }
                    else
                        new NoteFrame().start(new Stage());

                    Stage s = (Stage) login.getScene().getWindow();
                    s.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                snack.show("Username and password isn't matching", 1500);
            }
        } else {
            String usrname = username.getText();
            String paswd = pswd.getText();

            if (db.checkUsernameExists(con, usrname)) {
                snack.show("Username is in use", 1500);
                return;
            }

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
            } else {
                snack.show("An error has occured", 1500);
            }
        }
    }
}
