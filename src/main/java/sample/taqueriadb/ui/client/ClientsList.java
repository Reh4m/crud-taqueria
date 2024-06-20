package sample.taqueriadb.ui.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import sample.taqueriadb.base.ItemsList;
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
public class ClientsList extends ItemsList<Client> {

    public ClientsList() {
        super("Lista de clientes");
    }

    /**
     * Obtiene la lista de clientes y los guarda en un ObservableList como un objeto de tipo Client.
     *
     * @return ObservableList con la información de los clientes.
     */
    @Override
    protected ObservableList<Client> getItems() {
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
    protected void showItemsList() {
        // Guarda los empleados recuperados desde la base de datos.
        items = getItems();

        TableColumn<Client, String> id_column = createColumn("ID", "id");

        TableColumn<Client, String> name_column = createColumn("Nombre", "name");

        // Agrega las columnas de la tabla a una lista para poder iterar sobre ellas.
        List<TableColumn<Client, String>> columns = Arrays.asList(id_column, name_column);

        // Recorre la lista de columnas y se agregan una por una a la tabla de clientes.
        for (TableColumn<Client, String> column : columns) {
            table_view.getColumns().add(column);
        }

        table_view.setItems(items);
    }

    /**
     * Agrega una columna con botones "Editar" y los despliega en cada celda de la tabla.
     */
    @Override
    protected void addEditButtonColumn() {
        createActionButtonColumn("Editar", this::editButtonAction);
    }

    /**
     * Abre el formulario ClientForm al presionar el botón "Editar".
     * Este método crea una instancia de ClientForm, pasando la instancia actual de ClientList y el objeto Client del
     * cliente a modificar.
     *
     * @param old_client El cliente a ser modificado.
     */
    @Override
    protected void editButtonAction(Client old_client) {
        new ClientForm(this, old_client);
    }

    /**
     * Agrega una columna con botones "Borrar" y los despliega en cada celda de la tabla.
     */
    @Override
    protected void addDeleteButtonColumn() {
        createActionButtonColumn("Borrar", this::deleteButtonAction);
    }

    /**
     * Implementa una ventana de confirmación previo a la eliminación de un cliente.
     * La ventana despliega los botones "Aceptar" y "Cancelar". Al presionar en "Aceptar", se elimina al cliente.
     *
     * @param client Datos del cliente a ser eliminado.
     */
    @Override
    protected void deleteButtonAction(Client client) {
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
    protected Button addNewItemButton() {
        Button btn_add_client = new Button("Agregar cliente");
        btn_add_client.setMaxWidth(Double.MAX_VALUE);
        // Establece la acción que se ejecutará cuando se dé clic en el botón.
        // En este caso, abre el formulario del cliente.
        btn_add_client.setOnAction(actionEvent -> new ClientForm(this));

        return btn_add_client;
    }
}
