package sample.taqueriadb.components;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NewClientForm extends Stage {
    private Scene scene;

    public NewClientForm() {
        this.createUI();
        this.setTitle("Agregar cliente");
        this.setScene(scene);
        this.show();
    }

    private void createUI() {

        //Layout principal.
        // Contiene únicamente el formulario.
        VBox container = new VBox();
        container.setSpacing(5);
        container.setPadding(new Insets(5));

        scene = new Scene(container);
    }
}
