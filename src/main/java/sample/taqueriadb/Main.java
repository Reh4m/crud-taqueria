package sample.taqueriadb;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

import sample.taqueriadb.ui.ClientsList;
import sample.taqueriadb.ui.EmployeesList;
import sample.taqueriadb.db.DatabaseConnector;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseConnector.createConnection();

        new EmployeesList();

        new ClientsList();

        Scene scene = new Scene(new VBox(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}