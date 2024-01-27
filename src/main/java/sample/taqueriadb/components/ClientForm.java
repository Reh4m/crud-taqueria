package sample.taqueriadb.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import sample.taqueriadb.classes.Client;
import sample.taqueriadb.models.ClientDAO;
import sample.taqueriadb.views.ClientsList;

import java.sql.SQLException;

/**
 * Ventana que muestra un formulario para agregar o modificar un cliente en la base de datos.
 */
public class ClientForm extends Stage {
    private Scene scene;
    // Cuadrícula para ordenar los elementos del formulario.
    private GridPane grid_pane_form;

    // Entradas de texto.
    private TextField name_input;

    // Referencia a la clase ClientsList.
    ClientsList clients_list;

    public ClientForm(ClientsList clients_list) {
        // Instancia de la clase ClientsList para manejar la tabla de clientes.
        this.clients_list = clients_list;

        createUI();
        this.setTitle("Agregar cliente");
        this.setScene(scene);
        this.show();
    }

    private void createUI() {
        // Contenedor de los elementos del formulario.
        grid_pane_form = new GridPane();
        grid_pane_form.setAlignment(Pos.CENTER);
        grid_pane_form.setHgap(10);
        grid_pane_form.setVgap(10);
        grid_pane_form.setPadding(new Insets(25, 25, 25, 25));

        showClientForm();

        //Layout principal.
        // Contiene únicamente el formulario.
        VBox container = new VBox();
        container.getChildren().add(grid_pane_form);
        container.setSpacing(5);
        container.setPadding(new Insets(5));

        // Ventana principal.
        scene = new Scene(container);
    }

    /**
     * Despliega los elementos del formulario para recibir los datos del cliente.
     * Utiliza la clase TextField para recibir los datos por medio de entradas de texto.
     */
    private void showClientForm() {
        // Título.
        Text title = new Text("Ingresa el nombre del nuevo cliente");
        grid_pane_form.add(title, 0, 0, 2, 1);

        // Nombre.
        Label name_label = new Label("Nombre:");
        grid_pane_form.add(name_label, 0, 1);

        name_input = new TextField();
        grid_pane_form.add(name_input, 1, 1);

        // Botón para agregar o actualizar un nuevo cliente.
        Button btn_add_client = new Button("Agregar empleado");
        btn_add_client.setMaxWidth(Double.MAX_VALUE);
        btn_add_client.setOnAction(actionEvent -> addNewClient());
        grid_pane_form.add(btn_add_client, 0, 2, 2, 1);
    }

    /**
     * Obtiene el nombre del cliente escrito en el campo de texto y lo guarda en un objeto de tipo Client, para
     * posteriormente hacer un INSERT a la base de datos y agregar al nuevo cliente.
     */
    private void addNewClient() {
        try {
            int rows_affected = ClientDAO.add(new Client(name_input.getText()));

            clients_list.refreshTable();

            System.out.println(rows_affected);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Una vez terminado el proceso se cierra la ventana.
            this.close();
        }
    }
}
