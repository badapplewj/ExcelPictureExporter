package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Read file fxml and draw interface.
            FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                    .getResource("/sample/export.fxml"));
            Parent root = fxmlLoader.load();

            ExportController controller = fxmlLoader.getController();
            controller.setStage(primaryStage);
            primaryStage.setTitle("Excel导出助手");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
