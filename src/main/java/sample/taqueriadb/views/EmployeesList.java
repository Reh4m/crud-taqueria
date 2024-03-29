package sample.taqueriadb.views;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.taqueriadb.classes.Employee;
import sample.taqueriadb.components.EmployeeForm;
import sample.taqueriadb.models.EmployeeDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Ventana que muestra la lista de empleados registrados.
 */
public class EmployeesList extends Stage {
    private Scene scene;
    private TableView<Employee> table_view;

    private ObservableList<Employee> employees;

    public EmployeesList() {
        createUI();
        this.setTitle("Lista de empleados");
        this.setScene(scene);
        this.show();
    }

    private void createUI() {
        // Instancia la tabla de empleados.
        table_view = new TableView<>();

        // Muestra las columnas de la tabla con la información de los empleados.
        showEmployeesList();

        // Muestra la columna "Editar".
        addEditButtonToTable();

        // Muestra la columna "Borrar".
        addDeleteButtonToTable();

        // Se abre una ventana con un formulario para agregar un nuevo empleado.
        Button btn_add_employee = new Button("Agregar empleado");
        btn_add_employee.setMaxWidth(Double.MAX_VALUE);
        btn_add_employee.setOnAction(actionEvent -> new EmployeeForm(this));

        // Layout principal.
        // Contiene la tabla de empleados.
        VBox container = new VBox();
        container.getChildren().addAll(table_view, btn_add_employee);
        container.setSpacing(5);
        container.setPadding(new Insets(5));

        // Ventana principal.
        scene = new Scene(container, 500, 500);
    }

    /**
     * Obtiene la lista de empleados registrados desde la base de datos y los guarda en un ObservableList como un
     * objeto de tipo Employee, para posteriormente mostrar los atributos de cada objeto en la tabla.
     *
     * @return ObservableList con la información de los empleados.
     */
    private ObservableList<Employee> getEmployeesList() {
        ObservableList<Employee> employees = FXCollections.observableArrayList();

        try (ResultSet resultSet = EmployeeDAO.getEmployees()) {
            while (resultSet.next()) {
                 employees.add(new Employee(
                    resultSet.getInt("id_employee"),
                    resultSet.getString("name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("phone_number"),
                    resultSet.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    /**
     * Actualiza la tabla de empleados en la interfaz. Su propósito es obtener nuevamente la lista de empleados y
     * actualizar el TableView con los datos actualizados.
     * Se llama una vez que finaliza un proceso en la base de datos.
     */
    public void refreshTable() {
        // Obtiene la lista actualizada de empleados.
        employees = getEmployeesList();

        // Establece nuevamente el contenido del TableView.
        table_view.setItems(employees);

        // Actualiza el TableView para reflejar los cambios.
        // Esto es útil en casos donde la fuente de datos subyacente ha cambiado de una forma que no es observada por
        // el propio TableView.
        table_view.refresh();
    }

    /**
     * Despliega la información de todos los empleados obtenidos y los asigna en su respectiva columna para mostrarlos
     * en la tabla de empleados.
     */
    private void showEmployeesList() {
        // Lista de empleados obtenidos desde la base de datos.
        employees = getEmployeesList();

        TableColumn<Employee, String> id_column = createColumn("ID", "id");

        TableColumn<Employee, String> name_column = createColumn("Nombre", "name");

        TableColumn<Employee, String> last_name_column = createColumn(
                "Apellidos", "lastName"
        );

        TableColumn<Employee, String> phone_number_column = createColumn(
                "Número de teléfono", "phoneNumber"
        );

        TableColumn<Employee, String> email_column = createColumn("Email", "email");

        // Se agregan las columnas de la tabla a una lista para poder iterar sobre ellas.
        List<TableColumn<Employee, String>> columns = Arrays.asList(
            id_column, name_column, last_name_column, phone_number_column, email_column
        );
        // Posteriormente, se recorre la lista de columnas y se agregan una por una a la tabla de empleados.
        for (TableColumn<Employee, String> column : columns) {
            table_view.getColumns().add(column);
        }

        table_view.setItems(employees);
    }

    /**
     * Reduce el código necesario para crear una columna de la tabla de empleados.
     *
     * @param column_name nombre de la columna.
     * @param property_name nombre de la propiedad.
     * @return columna de la tabla de empleados.
     */
    private TableColumn<Employee, String> createColumn(String column_name, String property_name) {
        TableColumn<Employee, String> column = new TableColumn<>(column_name);

        column.setCellValueFactory(new PropertyValueFactory<>(property_name));

        return column;
    }

    /**
     * Agrega una columna con botones "Editar" y los despliega en cada celda existente. En otras palabras,
     * muestra un botón "Editar" en cada fila de la tabla Empleados.
     */
    private void addEditButtonToTable() {
        TableColumn<Employee, Void> button_column = new TableColumn<>();

        button_column.setCellFactory(param -> new TableCell<>() {
            private final Button edit_button = new Button("Editar");

            // Al momento de presionar el botón "Editar" se abre un formulario para modificar los datos del empleado.
            {
                edit_button.setOnAction(actionEvent -> {
                    // Obtiene los datos de la fila seleccionada.
                    Employee selected_record = table_view.getItems().get(this.getTableRow().getIndex());

                    openEmployeeForm(selected_record);
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
     * Abre el formulario EmployeeForm para editar un empleado existente.
     * Este método crea una instancia de EmployeeForm, pasando la instancia actual de EmployeesList y el objeto
     * Employee del empleado a modificar.
     *
     * @param old_employee El empleado a ser modificado.
     */
    private void openEmployeeForm(Employee old_employee) {
        new EmployeeForm(this, old_employee);
    }

    /**
     * Agrega una columna con botones "Borrar" y los despliega en cada celda existente. En otras palabras,
     * muestra un botón "Borrar" en cada fila de la tabla Empleado.
     */
    private void addDeleteButtonToTable() {
        TableColumn<Employee, Void> delete_column = new TableColumn<>();

        delete_column.setCellFactory(param -> new TableCell<>() {
            private final Button delete_button = new Button("Borrar");

            // Al momento de presionar el botón "Borrar" se abre una ventana de confirmación.
            {
                delete_button.setOnAction(actionEvent -> {
                    // Obtiene los datos de la fila seleccionada.
                    Employee selected_record = table_view.getItems().get(this.getTableRow().getIndex());

                    // En caso de recibir una respuesta de confirmación se borra al empleado.
                    showConfirmationDialog().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            deleteEmployee(selected_record.getId());
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
     * Elimina un empleado de la base de datos. Mediante el ID del empleado seleccionado se ejecuta de forma
     * asíncrona un DELETE a la base de datos con la referencia del empleado. Por último la tabla de empleados se
     * actualiza.
     *
     * @param id del empleado a eliminar.
     */
    private void deleteEmployee(int id) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return EmployeeDAO.delete(id);
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
     * Muestra una ventana de confirmación al momento de querer eliminar un empleado.
     *
     * @return Optional<ButtonType> Contiene la respuesta del usuario a la ventana de confirmación.
     */
    private Optional<ButtonType> showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Eliminar Empleado");
        alert.setContentText("¿Estás seguro de eliminar este empleado?");

        return alert.showAndWait();
    }
}
