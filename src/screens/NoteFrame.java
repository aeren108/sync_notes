package screens;

import borderless.BorderlessScene;
import controller.NoteController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class NoteFrame extends Application {

    private String note;
    private String theme;
    private double x,y;
    private double w,h;
    private int id;

    public NoteFrame(String note, int id, String theme, double w, double h, double x, double y) {
        this.note = note;
        this.id = id;
        this.theme = theme;
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
    }

    public NoteFrame() {
        note = "";
        id = 0;
        w = 240;
        h = 350;
        x = 0;
        y = 0;

        theme = "yellow_theme";
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/style/note_frame.fxml").openStream());

        BorderlessScene scene = new BorderlessScene(primaryStage, root);
        scene.getStylesheets().add("/style/"+theme+".css");
        scene.setResizable(true);

        NoteController con = loader.getController();
        con.setContent(note);
        con.setID(id);
        con.setTheme(theme);

        primaryStage.setWidth(w);
        primaryStage.setHeight(h);
        primaryStage.setX(x);
        primaryStage.setY(y);
        primaryStage.setMinHeight(35);
        primaryStage.setMinWidth(185);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
