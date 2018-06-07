package controller;

import auth.User;
import database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import screens.NoteFrame;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NoteController implements Initializable {

    @FXML private Button add;
    @FXML private Button sync;
    @FXML private Button del;
    @FXML private Button close;

    @FXML private TextArea content;

    private Database db;
    private Connection con;
    private final String[] args = {"jdbc:mysql://localhost:4242", "aeren", "11471147"};

    private int id;
    private boolean synced;

    private double xOffset;
    private double yOffset;

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
        if (e.getSource() == add ) {
            try {
                new NoteFrame().start(new Stage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == sync){
            String currentContent = content.getText();

            if (currentContent.isEmpty() || currentContent == null)
                return;

            if (id != 0) {
                if (db.updateNote(con, id, currentContent)) {
                    synced = true;

                }
            } else {
                if (db.uploadNote(con, User.getCurrentUser().getUsername(), currentContent)) {
                    synced = true;
                    id = db.findID(con, content.getText());
                    // TODO: 7.06.2018 make alert about upload process
                }
            }
        } else if (e.getSource() == del){
            if (id == 0 && !synced) {
                Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
                s.close();
            } else if (id == 0 && synced) {
                id = db.findID(con, content.getText());
                if (db.deleteNote(con, id)) {
                    Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    s.close();

                    // TODO: 8.06.2018 make alert about deleting process
                }
            } else if (id != 0) {
                if (db.deleteNote(con, id)) {
                    Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    s.close();

                    // TODO: 8.06.2018 make alert about deleting process 
                }
            }
                
        } else if (e.getSource() == close) {
            String currentContent = content.getText();

            if (id != 0) {
                if (db.updateNote(con, id, currentContent)) {
                    // TODO: 7.06.2018 make alert about update process
                }
            } else if (id == 0 && !synced){
                if (db.uploadNote(con, User.getCurrentUser().getUsername(), currentContent)) {
                    // TODO: 7.06.2018 make alert about upload process
                }
            }

            Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
            s.close();
        }
    }

    public void onPressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    public void onDragged(MouseEvent event) {
        Stage stage = (Stage) add.getScene().getWindow();

        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    public void setContent(String content) {
        this.content.setText(content);
    }

    public void setID(int id) {
        this.id = id;
    }
}
