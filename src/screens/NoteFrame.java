package screens;

import borderless.BorderlessScene;
import controller.NoteController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class NoteFrame extends Application {

    private String note;
    private int id;

    public NoteFrame(String note, int id) {
        this.note = note;
        this.id = id;
    }

    public NoteFrame() {
        note = "";
        id = 0;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("../style/note_frame.fxml").openStream());

        BorderlessScene scene = new BorderlessScene(primaryStage, root);
        scene.getStylesheets().add("/style/style.css");
        scene.setResizable(true);

        NoteController con = loader.getController();
        con.setContent(note);
        con.setID(id);

        primaryStage.setWidth(250);
        primaryStage.setHeight(350);
        primaryStage.setMinHeight(35);
        primaryStage.setMinWidth(50);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
