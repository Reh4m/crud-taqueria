package sample.taqueriadb.ui.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import sample.taqueriadb.base.UsersList;
import sample.taqueriadb.dao.ClientDAO;
import sample.taqueriadb.model.Client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Ventana que muestra la lista de clientes registrados.
 */
public class ClientsList extends UsersList<Client> {

    public ClientsList() {
        super("Lista de clientes");
    }

    /**
     * Obtiene la lista de clientes y los guarda en un ObservableList como un objeto de tipo Client.
     *
     * @return ObservableList con la información de los clientes.
     */
    @Override
    protected ObservableList<Client> getUsers() {
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
     * Muestra la información de los clientes asignándolos a una columna en la tabla de clientes.
     */
    @Override
    protected void showUsersList() {
        // Guarda los empleados recuperados desde la base de datos.
        users = getUsers();

        TableColumn<Client, String> id_column = createColumn("ID", "id");

        TableColumn<Client, String> name_column = createColumn("Nombre", "name");

        // Agrega las columnas de la tabla a una lista para poder iterar sobre ellas.
        List<TableColumn<Client, String>> columns = Arrays.asList(id_column, name_column);

        // Recorre la lista de columnas y se agregan una por una a la tabla de clientes.
        for (TableColumn<Client, String> column : columns) {
            table_view.getColumns().add(column);
        }

        table_view.setItems(users);
    }

    /**
     * Agrega una columna con botones "Editar" y los despliega en cada celda de la tabla. En otras palabras,
     * muestra un botón "Editar" en cada fila de la tabla Cliente.
     */
    @Override
    protected void addEditButtonColumn() {
        TableColumn<Client, Void> button_column = new TableColumn<>();

        button_column.setCellFactory(param -> new TableCell<>() {
            private final Button edit_button = new Button("Editar");

            // Al momento de presionar el botón "Editar" se abre un formulario para modificar los datos del cliente.
            {
                edit_button.setOnAction(actionEvent -> {
                    // Obtiene los datos de la fila seleccionada.
                    Client selected_record = table_view.getItems().get(this.getTableRow().getIndex());

                    openEditForm(selected_record);
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
    @Override
    protected void openEditForm(Client old_client) {
        new ClientForm(this, old_client);
    }

    /**
     * Agrega una columna con botones "Borrar" y los despliega en cada celda de la tabla. En otras palabras,
     * muestra un botón "Borrar" en cada fila de la tabla Cliente.
     */
    @Override
    protected void addDeleteButtonColumn() {
        TableColumn<Client, Void> delete_column = new TableColumn<>();

        delete_column.setCellFactory(param -> new TableCell<>() {
            private final Button delete_button = new Button("Borrar");

            // Al momento de presionar el botón "Borrar" se abre una ventana para confirmar la acción.
            {
                delete_button.setOnAction(actionEvent -> {
                    // Obtiene los datos de la fila seleccionada.
                    Client selected_record = table_view.getItems().get(this.getTableRow().getIndex());

                    showConfirmationDialog(
                        "Eliminar cliente",
                        "¿Estás seguro de eliminar este cliente?"
                    ).ifPresent(response -> {
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

    private void deleteClientAction(Client client) {
        showConfirmationDialog(
            "Eliminar cliente",
            "¿Estás seguro de eliminar este cliente?"
        ).ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteClient(client.getId());
            }
        });
    }

    /**
     * Elimina un cliente de la base de datos mediante su ID. Se ejecuta de forma asíncrona un DELETE a la base de
     * datos con la referencia del cliente. Por último, la tabla de clientes se actualiza.
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
     * Crea un botón que se utilizará para agregar nuevos clientes.
     * Al dar clic en el botón, se abre una ventana con un formulario para ingresar los datos del cliente.
     *
     * @return el botón que se crea para agregar nuevos clientes.
     */
    @Override
    protected Button addNewUserButton() {
        Button btn_add_client = new Button("Agregar cliente");
        btn_add_client.setMaxWidth(Double.MAX_VALUE);
        // Establece la acción que se ejecutará cuando se dé clic en el botón.
        // En este caso, abre el formulario del cliente.
        btn_add_client.setOnAction(actionEvent -> new ClientForm(this));

        return btn_add_client;
    }
}
