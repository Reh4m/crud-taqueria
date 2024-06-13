package sample.taqueriadb.components;

import javafx.application.Platform;
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
import sample.taqueriadb.model.Client;
import sample.taqueriadb.dao.ClientDAO;
import sample.taqueriadb.views.ClientsList;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

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
     * Indica la forma en que se usará el formulario. Si es verdadero quiere decir que se agregará un nuevo cliente.
     * Si el valor es falso entonces quiere decir que se modificará un cliente.
     */
    private final boolean is_new_client;

    /**
     * Crea una instancia para agregar un nuevo cliente.
     *
     * @param clients_list Referencia a la instancia de la clase ClientList para llamar a sus métodos internos e
     *                     interactuar con la tabla de clientes.
     */
    public ClientForm(ClientsList clients_list) {
        // Instancia de la clase ClientsList para manejar la tabla de clientes.
        this.clients_list = clients_list;

        // Indica que se agregará un nuevo cliente.
        is_new_client = true;

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

        // Indica que se modificará un cliente.
        is_new_client = false;

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

        // Layout principal.
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
        Text title = new Text("Ingresa los datos del cliente");
        grid_pane_form.add(title, 0, 0, 2, 1);

        // Nombre.
        Label name_label = new Label("Nombre:");
        grid_pane_form.add(name_label, 0, 1);

        name_input = new TextField();
        grid_pane_form.add(name_input, 1, 1);

        // Botón para agregar o actualizar un nuevo cliente.
        Button btn_add_client = new Button("Agregar");
        btn_add_client.setMaxWidth(Double.MAX_VALUE);
        btn_add_client.setOnAction(actionEvent -> onAddButtonClicked());
        grid_pane_form.add(btn_add_client, 0, 2, 2, 1);
    }

    /**
     * Indica al botón 'Agregar' qué acción realizar al momento de presionar el botón, esto dependiendo del uso que se
     * le esté dando al formulario.
     */
    private void onAddButtonClicked() {
        if (is_new_client) {
            addNewClient();
        } else {
            updateClient();
        }
    }

    /**
     * Agrega un nuevo cliente a la base de datos. Obtiene los datos ingresados en el formulario y hace un INSERT a la
     * base de datos de manera asíncrona. Una vez terminado el proceso, se actualiza la tabla de clientes para mostrar
     * al nuevo cliente agregado. Por último, se cierra la ventana actual.
     */
    private void addNewClient() {
        // Se obtienen los datos ingresados en el formulario.
        Client new_client = new Client(name_input.getText());

        // Ejecuta el proceso de manera asíncrona. Una vez finalizada la tarea, almacena el resultado del mismo y
        // actualiza la tabla de clientes.
        CompletableFuture.supplyAsync(() -> {
            try {
                return ClientDAO.add(new_client);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(
                // Este bloque de código se ejecuta en el hilo de la aplicación JavaFX, lo cual hace posible ejecutar
                // operaciones en la interfaz de usuario (refreshTable, closeWindow).
                rows_affected -> Platform.runLater(() -> {
                    // Actualiza la lista de empleados en la interfaz.
                    clients_list.refreshTable();

                    System.out.println("Filas afectadas: " + rows_affected);

                    // Cierra la ventana actual.
                    this.close();
                })
        ).exceptionally(e -> {
            e.printStackTrace();

            return null;
        });
    }

    /**
     * Modifica un cliente existente en la base de datos. Obtiene los datos ingresados en el formulario y actualiza
     * los datos del cliente (UPDATE) de manera asíncrona. Después de ejecutar el proceso, se actualiza la tabla de
     * clientes y se cierra la ventana.
     */
    private void updateClient() {
        // Se obtienen los datos ingresados en el formulario.
        Client new_client = new Client(name_input.getText());

        // Establece el ID del cliente.
        // Este se obtiene desde la lista de clientes y lo asigna al objeto del cliente modificado.
        // El ID se utiliza dentro del query para saber qué cliente se va a actualizar.
        new_client.setId(old_client.getId());

        // Ejecuta el proceso de manera asíncrona. Una vez finalizada la tarea, almacena el resultado del mismo y
        // actualiza la tabla de clientes.
        CompletableFuture.supplyAsync(() -> {
            try {
                return ClientDAO.update(new_client);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(
                // Este bloque de código se ejecuta en el hilo de la aplicación JavaFX, lo cual hace posible ejecutar
                // operaciones en la interfaz de usuario (refreshTable, closeWindow).
                rows_affected -> Platform.runLater(() -> {
                    // Actualiza la lista de clientes en la interfaz.
                    clients_list.refreshTable();

                    System.out.println("Filas afectadas: " + rows_affected);

                    // Cierra la ventana actual.
                    this.close();
                })
        ).exceptionally(e -> {
            e.printStackTrace();

            return null;
        });
    }
}
