package controller;

import auth.User;
import database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import screens.NoteFrame;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NoteController implements Initializable {

    @FXML private HBox bar;

    @FXML private Button add;
    @FXML private Button sync;
    @FXML private Button del;
    @FXML private Button close;

    @FXML private MenuItem backup;
    @FXML private MenuItem yellow;
    @FXML private MenuItem green;
    @FXML private MenuItem purple;
    @FXML private MenuItem blue;

    @FXML private TextArea content;

    private Database db;
    private Connection con;
    private final String[] args = {"jdbc:mysql://sql7.freemysqlhosting.net:3306", "sql7242098", "Uitb4SB6vl"};

    private final Tooltip tAdd = new Tooltip("New");
    private final Tooltip tSync = new Tooltip("Synchronise");
    private final Tooltip tDel = new Tooltip("Delete");
    private final Tooltip tClose = new Tooltip("Close window");
    
    /* I decalred a lot of Tooltip objects for each button in the app.
     * I could do button.setTooltip(new Tooltip("bla bla")).I didn't because
     * Buttons can't describe what they do exactly, so I want tooltips to appear immediately
     */

    private String currentTheme = "yellow_theme";

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

        tAdd.getStyleClass().add("ttip");
        tSync.getStyleClass().add("ttip");
        tDel.getStyleClass().add("ttip");
        tClose.getStyleClass().add("ttip");

        bar.getStyleClass().add("hbox");
    }

    public void handleAction(ActionEvent e) {
        if (e.getSource() == add) {
            try {
                new NoteFrame().start(new Stage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == sync) {
            String currentContent = content.getText();

            if (currentContent.isEmpty() || currentContent == null)
                return;

            if (id != 0) {
                if (db.updateNote(con, id, currentContent, true)) {
                    synced = true;

                }
            } else {
                if (db.uploadNote(con, User.getCurrentUser().getUsername(), currentContent)) {
                    synced = true;
                    id = db.findID(con, content.getText());
                    // TODO: 7.06.2018 make alert 
                }
            }
        } else if (e.getSource() == del) {
            if (id == 0 && !synced) {
                Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
                s.close();
            } else if (id == 0 && synced) {
                id = db.findID(con, content.getText());
                if (db.deleteNote(con, id)) {
                    Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    s.close();

                    // TODO: 8.06.2018 make alert 
                }
            } else if (id != 0) {
                if (db.deleteNote(con, id)) {
                    Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    s.close();

                    // TODO: 8.06.2018 make alert 
                }
            }

        } else if (e.getSource() == close) {
            String currentContent = content.getText();
            String properties = close.getScene().getWidth()+","+close.getScene().getHeight()+","+close.getScene().getWindow().getX()+","+close.getScene().getWindow().getY();

            if (id != 0) {
<<<<<<< HEAD
                db.updateNote(con, id, currentContent, true);
                db.updateProperties(con, id, properties);
                db.updateTheme(con, db.findID(con, currentContent), currentTheme);
            } else if (id == 0) {
                if (!synced)
                    db.uploadNote(con, User.getCurrentUser().getUsername(), currentContent);
                db.updateProperties(con, db.findID(con, currentContent), properties);
                db.updateTheme(con, db.findID(con, currentContent), currentTheme);
=======
                if (db.updateNote(con, id, currentContent, true)) {
                    // TODO: 7.06.2018 make alert
                }
            } else if (id == 0 && !synced){
                if (db.uploadNote(con, User.getCurrentUser().getUsername(), currentContent)) {
                    // TODO: 7.06.2018 make alert 
                }
>>>>>>> 77389d4b2b2fbbb0c0b788ceb6f1edc91d526dff
            }

            Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
            s.close();
        } else if (e.getSource() == backup) {
            String fromBackup = db.loadFromBackup(con, id);

            if (id == 0) {
                id = db.findID(con, content.getText());

                if (db.updateNote(con, id, fromBackup, false)) {
                    content.setText(fromBackup);
                }
            } else {
                if (db.updateNote(con, id, fromBackup, false)) {
                    content.setText(fromBackup);
                }
            }

        }
    }

    public void handleTheme(ActionEvent e) {
        if (e.getSource() == yellow) {
            Scene scene = add.getScene();
            scene.getStylesheets().remove("/style/"+ currentTheme +".css");
            scene.getStylesheets().add("/style/yellow_theme.css");
            currentTheme = "yellow_theme";
        } else if (e.getSource() == green) {
            Scene scene = add.getScene();
            scene.getStylesheets().remove("/style/"+ currentTheme +".css");
            scene.getStylesheets().add("/style/green_theme.css");
            currentTheme = "green_theme";
        } else if (e.getSource() == purple) {
            // TODO: 10.06.2018 create a purple theme css file
        } else if (e.getSource() == blue) {
            // TODO: 10.06.2018 create a blue theme css file
        }
    }

    public void mouseEntered(MouseEvent e) {
        double x = e.getScreenX();
        double y = e.getScreenY();

        if (e.getSource() == add) {
            tAdd.show(add, x+4, y+4);
        } else if (e.getSource() == sync) {
            tSync.show(sync, x+4, y+4);
        } else if (e.getSource() == del) {
            tDel.show(del, x+4, y+4);
        } else if (e.getSource() == close) {
            tClose.show(close, x+4, y+4);
        }
    }

    public void mouseExited(MouseEvent e) {
        if (e.getSource() == add) {
            tAdd.hide();
        } else if (e.getSource() == sync) {
            tSync.hide();
        } else if (e.getSource() == del) {
            tDel.hide();
        } else if (e.getSource() == close) {
            tClose.hide();
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

    public void setTheme(String theme) {
        currentTheme = theme;
    }
}
