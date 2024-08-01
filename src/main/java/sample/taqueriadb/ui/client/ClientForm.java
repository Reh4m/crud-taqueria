package sample.taqueriadb.ui.client;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import sample.taqueriadb.base.EditableForm;
import sample.taqueriadb.model.Client;
import sample.taqueriadb.dao.ClientDAO;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * Ventana que muestra un formulario para agregar o modificar un cliente en la base de datos.
 */
public class ClientForm extends EditableForm<Client, ClientsList> {
    // Entradas de texto.
    private TextField name_input;

    public ClientForm(ClientsList clients_list) {
        super("Agregar nuevo cliente", clients_list);
    }

    public ClientForm(ClientsList clients_list, Client old_client) {
        super("Editar Cliente", clients_list, old_client);

        // Se establecen los datos del cliente en sus respectivos campos de texto.
        name_input.setText(old_client.getName());
    }

    /**
     * Despliega los elementos del formulario para recibir los datos del cliente.
     */
    protected void displayFormElements() {
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
        btn_add_client.setOnAction(actionEvent -> onSaveButtonClicked());
        grid_pane_form.add(btn_add_client, 0, 2, 2, 1);
    }

    /**
     * Proporciona los datos ingresados en el formulario.
     *
     * @return objeto de tipo Client con los datos del client.
     */
    protected Client getFormData() {
        return new Client(name_input.getText());
    }

    /**
     * Maneja tanto la creación de un nuevo cliente como la actualización de uno existente.
     * Obtiene los datos del formulario y realiza una operación INSERT o UPDATE en la base de datos de forma asíncrona.
     * Tras ejecutar el proceso, actualiza la tabla de clientes y cierra la ventana.
     */
    protected void onSaveButtonClicked() {
        // Obtiene los datos ingresados en el formulario.
        Client new_client = getFormData();

        // Establece el ID del cliente en caso de actualizar.
        // Este se obtiene desde la lista de clientes y lo asigna al objeto del cliente modificado.
        // El ID se utiliza dentro del query para saber qué cliente se va a actualizar.
        if (!is_new_item) new_client.setId(old_item.getId());

        // Ejecuta el proceso de manera asíncrona. Una vez finalizada la tarea, almacena el resultado del mismo y
        // actualiza la tabla de clientes.
        CompletableFuture.supplyAsync(() -> {
            try {
                return is_new_item ? ClientDAO.add(new_client) : ClientDAO.update(new_client);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(
                // Este bloque de código se ejecuta en el hilo de la aplicación JavaFX, lo cual hace posible ejecutar
                // operaciones en la interfaz de usuario (refreshTable, closeWindow).
                rows_affected -> Platform.runLater(() -> {
                    // Actualiza la lista de clientes en la interfaz.
                    items_list.refreshTable();

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
