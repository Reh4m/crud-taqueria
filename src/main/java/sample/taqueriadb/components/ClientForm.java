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
import sample.taqueriadb.views.EmployeesList;

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

    // Datos del cliente a modificar
    Client old_client;

    /**
     * Crea una instancia para agregar un nuevo cliente.
     *
     * @param clients_list Referencia a la instancia de la clase ClientList para llamar a sus métodos internos e
     *                     interactuar con la tabla de clientes.
     */
    public ClientForm(ClientsList clients_list) {
        // Instancia de la clase ClientsList para manejar la tabla de clientes.
        this.clients_list = clients_list;

        setupForm("Agregar nuevo cliente");
    }

    /**
     * Constructor secundario, encargado de recuperar los datos de un cliente en el caso de modificar sus atributos y
     * posteriormente actualizarlos.
     *
     * @param clients_list Referencia a la instancia de la clase ClientList para llamar a sus métodos internos e
     *                     interactuar con la tabla de clientes.
     * @param old_client Objeto tipo Client con los datos del cliente a modificar.
     */
    public ClientForm(ClientsList clients_list, Client old_client) {
        // Instancia de la clase ClientList para actualizar la lista de clientes.
        this.clients_list = clients_list;
        // Instancia de la clase Client para recuperar los datos a actualizar.
        this.old_client = old_client;

        // Se crean los campos de texto antes de rellenarlos con los datos del cliente.
        setupForm("Editar Cliente");

        // Se establecen los datos del cliente en sus respectivos campos de texto.
        name_input.setText(old_client.getName());
    }

    /**
     * Establece la configuración básica de la ventana.
     *
     * @param window_title Nombre del título que tendrá la ventana.
     */
    private void setupForm(String window_title) {
        createUI();
        this.setTitle(window_title);
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