package sample.taqueriadb.ui.client;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import sample.taqueriadb.components.ClientForm;
import sample.taqueriadb.dao.ClientDAO;
import sample.taqueriadb.model.Client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Ventana que muestra la lista de clientes registrados.
 */
public class ClientsList extends Stage {
    private Scene scene;
    private TableView<Client> table_view;

    private ObservableList<Client> clients;

    public ClientsList() {
        createUI();
        this.setTitle("Lista de clientes");
        this.setScene(scene);
        this.show();
    }

    private void createUI() {
        // Instancia la tabla de clientes.
        table_view = new TableView<>();
        // Muestra las columnas de la tabla con la información de los clientes.
        showClientsList();
        // Muestra la columna "Editar".
        addEditButtonToTable();
        // Muestra la columna "Borrar"
        addDeleteButtonToTable();

        // Abre una ventana con un formulario para agregar un nuevo cliente.
        Button btn_add_client = new Button("Agregar cliente");
        btn_add_client.setMaxWidth(Double.MAX_VALUE);
        btn_add_client.setOnAction(actionEvent -> new ClientForm(this));

        // Layout principal.
        // Contiene la tabla de clientes.
        VBox container = new VBox();
        container.getChildren().addAll(table_view, btn_add_client);

        // Ventana principal.
        scene = new Scene(container, 500, 500);
    }

    /**
     * Obtiene la lista de clientes registrados desde la base de datos y los guarda en un ObservableList como un objeto
     * de tipo Client, para posteriormente mostrar los atributos de cada objeto en la tabla.
     *
     * @return ObservableList con la información de los clientes.
     */
    private ObservableList<Client> getClientsList() {
        ObservableList<Client> clients = FXCollections.observableArrayList();

        try (ResultSet resultSet = ClientDAO.getClients()) {
            while (resultSet.next()) {
                 clients.add(new Client(
                    resultSet.getInt("id_client"),
                    resultSet.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    /**
     * Actualiza la tabla de clientes en la interfaz. Su propósito es obtener nuevamente la lista de clientes y
     * actualizar el TableView con los datos actualizados.
     * Se llama una vez que finaliza un proceso en la base de datos.
     */
    public void refreshTable() {
        // Obtiene la lista actualizada de clientes.
        clients = getClientsList();

        // Establece nuevamente el contenido del TableView.
        table_view.setItems(clients);

        // Actualiza el TableView para reflejar los cambios.
        // Esto es útil en casos donde la fuente de datos subyacente ha cambiado de una forma que no es observada por
        // el propio TableView.
        table_view.refresh();
    }

    /**
     * Despliega la información de todos los clientes obtenidos y los asigna en su respectiva columna para mostrarlos
     * en la tabla de clientes.
     */
    private void showClientsList() {
        // Lista de clientes obtenidos desde la base de datos.
        clients = getClientsList();

        TableColumn<Client, String> id_column = createColumn("ID", "id");

        TableColumn<Client, String> name_column = createColumn("Nombre", "name");

        // Se agregan las columnas de la tabla a una lista para poder iterar sobre ellas.
        List<TableColumn<Client, String>> columns = Arrays.asList(id_column, name_column);
        // Posteriormente, se recorre la lista de columnas y se agregan una por una a la tabla de clientes.
        for (TableColumn<Client, String> column : columns) {
            table_view.getColumns().add(column);
        }

        table_view.setItems(clients);
    }

    /**
     * Reduce el código necesario para crear una columna de la tabla de clientes.
     *
     * @param column_name Nombre de la columna.
     * @param property_name Nombre de la propiedad.
     * @return Columna de la tabla de clientes.
     */
    private TableColumn<Client, String> createColumn(String column_name, String property_name) {
        TableColumn<Client, String> column = new TableColumn<>(column_name);

        column.setCellValueFactory(new PropertyValueFactory<>(property_name));

        return column;
    }

    /**
     * Agrega una columna con botones "Editar" y los despliega en cada celda existente. En otras palabras,
     * muestra un botón "Editar" en cada fila de la tabla Cliente.
     */
    private void addEditButtonToTable() {
        TableColumn<Client, Void> button_column = new TableColumn<>();

        button_column.setCellFactory(param -> new TableCell<>() {
            private final Button edit_button = new Button("Editar");

            // Al momento de presionar el botón "Editar" se abre un formulario para modificar los datos del cliente.
            {
                edit_button.setOnAction(actionEvent -> {
                    // Obtiene los datos de la fila seleccionada.
                    Client selected_record = table_view.getItems().get(this.getTableRow().getIndex());

                    openClientForm(selected_record);
                });
            }

            // Se encarga de desplegar el botón "Editar". En este caso, si la celda no está vacía, el edit_button se
            // establece como el gráfico de la celda.
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) setGraphic(edit_button);
            }
        });

        table_view.getColumns().add(button_column);
    }

    /**
     * Abre el formulario ClientForm para editar un cliente existente.
     * Este método crea una instancia de ClientForm, pasando la instancia actual de ClientList y el objeto Client del
     * cliente a modificar.
     *
     * @param old_client El cliente a ser modificado.
     */
    private void openClientForm(Client old_client) {
        new ClientForm(this, old_client);
    }

    /**
     * Agrega una columna con botones "Borrar" y los despliega en cada celda existente. En otras palabras,
     * muestra un botón "Borrar" en cada fila de la tabla Cliente.
     */
    private void addDeleteButtonToTable() {
        TableColumn<Client, Void> delete_column = new TableColumn<>();

        delete_column.setCellFactory(param -> new TableCell<>() {
            private final Button delete_button = new Button("Borrar");

            // Al momento de presionar el botón "Borrar" se abre una ventana para confirmar la acción.
            {
                delete_button.setOnAction(actionEvent -> {
                    // Obtiene los datos de la fila seleccionada.
                    Client selected_record = table_view.getItems().get(this.getTableRow().getIndex());

                    showConfirmationDialog().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            deleteClient(selected_record.getId());
                        }
                    });
                });
            }

            // Se encarga de desplegar el botón "Borrar". En este caso, si la celda no está vacía, el delete_button se
            // establece como el gráfico de la celda.
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) setGraphic(delete_button);
            }
        });

        table_view.getColumns().add(delete_column);
    }

    /**
     * Elimina un cliente de la base de datos. Mediante el ID del cliente seleccionado se ejecuta de forma asíncrona un
     * DELETE a la base de datos con la referencia del cliente. Por último la tabla de clientes se actualiza.
     *
     * @param id del cliente a eliminar.
     */
    private void deleteClient(int id) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return ClientDAO.delete(id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(rows_affected -> Platform.runLater(() -> {
            refreshTable();

            System.out.println("Filas afectadas: " + rows_affected);
        })).exceptionally(e -> {
            e.printStackTrace();

            return null;
        });
    }

    /**
     * Muestra una ventana de confirmación al momento de querer eliminar un cliente.
     *
     * @return Optional<ButtonType> Contiene la respuesta del usuario a la ventana de confirmación.
     */
    private Optional<ButtonType> showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Eliminar Cliente");
        alert.setContentText("¿Estás seguro de eliminar este cliente?");

        return alert.showAndWait();
    }
}
