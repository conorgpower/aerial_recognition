import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Created by conor on 22/02/2018.
 */
public class Main extends Application{

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage window) throws Exception{

        AnchorPane root = FXMLLoader.load(getClass().getResource("MainGUI.fxml"));
        Scene scene = new Scene(root, 775, 441);

        window.setScene(scene);
        window.setTitle("Aerial Recognition Application");
        window.show();

        Image image = new Image("http://goo.gl/kYEQl");
        ImageView imageView = new ImageView();
        imageView.setImage(image);
    }
}
