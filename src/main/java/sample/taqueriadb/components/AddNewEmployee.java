package sample.taqueriadb.components;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddNewEmployee extends Stage {
    private Scene scene;

    public AddNewEmployee() {
        this.createUI();
        this.setTitle("Agregar nuevo empleado");
        this.setScene(scene);
        this.show();
    }

    private void createUI() {
        // Layout principal.
        // Contiene el formulario para agregar un nuevo empleado.
        VBox container = new VBox();

        // Ventana principal.
        scene = new Scene(container, 500, 500);
    }
}
