package sample.taqueriadb.views;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import sample.taqueriadb.components.NewClientForm;
import sample.taqueriadb.models.ClientDAO;
import sample.taqueriadb.classes.Client;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Ventana que muestra la lista de clientes registrados.
 */
public class ClientsList extends Stage {
    private Scene scene;
    private TableView<Client> table_view;

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
        this.showClientsList();

        // Abre una ventana con un formulario para agregar un nuevo cliente.
        Button btn_add_client = new Button("Agregar cliente");
        btn_add_client.setMaxWidth(Double.MAX_VALUE);
        btn_add_client.setOnAction(actionEvent -> new NewClientForm());

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
     * Despliega la información de todos los clientes obtenidos y los asigna en su respectiva columna para mostrarlos
     * en la tabla de clientes.
     */
    private void showClientsList() {
        // Lista de clientes obtenidos desde la base de datos.
        ObservableList<Client> clients = getClientsList();

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
}
