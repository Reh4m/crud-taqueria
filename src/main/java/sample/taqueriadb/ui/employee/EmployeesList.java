package sample.taqueriadb.ui.employee;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import sample.taqueriadb.model.Employee;
import sample.taqueriadb.components.EmployeeForm;
import sample.taqueriadb.dao.EmployeeDAO;
import sample.taqueriadb.utils.UsersList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Ventana que muestra la lista de empleados registrados.
 */
public class EmployeesList extends UsersList<Employee> {

    public EmployeesList() {
        super("Lista de empleados");
    }

    /**
     * Obtiene la lista de empleados y los guarda en un ObservableList como un objeto de tipo Employee.
     *
     * @return ObservableList con la información de los empleados.
     */
    @Override
    protected ObservableList<Employee> getUsers() {
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
     * Muestra la información de los empleados asignándolos a una columna en la tabla de empleados.
     */
    @Override
    protected void showUsersList() {
        // Guarda los de empleados recuperados desde la base de datos.
        users = getUsers();

        TableColumn<Employee, String> id_column = createColumn("ID", "id");

        TableColumn<Employee, String> name_column = createColumn("Nombre", "name");

        TableColumn<Employee, String> last_name_column = createColumn(
                "Apellidos", "lastName"
        );

        TableColumn<Employee, String> phone_number_column = createColumn(
                "Número de teléfono", "phoneNumber"
        );

        TableColumn<Employee, String> email_column = createColumn("Email", "email");

        // Agrega las columnas de la tabla a una lista para poder iterar sobre ellas.
        List<TableColumn<Employee, String>> columns = Arrays.asList(
            id_column, name_column, last_name_column, phone_number_column, email_column
        );

        // Recorre la lista de columnas y se agregan una por una a la tabla de empleados.
        for (TableColumn<Employee, String> column : columns) {
            table_view.getColumns().add(column);
        }

        table_view.setItems(users);
    }

    /**
     * Agrega una columna con botones "Editar" y los despliega en cada celda de la tabla. En otras palabras,
     * muestra un botón "Editar" en cada fila de la tabla Empleados.
     */
    @Override
    protected void addEditButtonColumn() {
        TableColumn<Employee, Void> button_column = new TableColumn<>();

        button_column.setCellFactory(param -> new TableCell<>() {
            private final Button edit_button = new Button("Editar");

            // Al momento de presionar el botón "Editar" se abre un formulario para modificar los datos del empleado.
            {
                edit_button.setOnAction(actionEvent -> {
                    // Obtiene los datos de la fila seleccionada.
                    Employee selected_record = table_view.getItems().get(this.getTableRow().getIndex());

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
     * Abre el formulario EmployeeForm para editar un empleado existente.
     * Este método crea una instancia de EmployeeForm, pasando la instancia actual de EmployeesList y el objeto
     * Employee del empleado a modificar.
     *
     * @param old_employee El empleado a ser modificado.
     */
    @Override
    protected void openEditForm(Employee old_employee) {
        new EmployeeForm(this, old_employee);
    }

    /**
     * Agrega una columna con botones "Borrar" y los despliega en cada celda de la tabla. En otras palabras,
     * muestra un botón "Borrar" en cada fila de la tabla Empleado.
     */
    @Override
    protected void addDeleteButtonColumn() {
        TableColumn<Employee, Void> delete_column = new TableColumn<>();

        delete_column.setCellFactory(param -> new TableCell<>() {
            private final Button delete_button = new Button("Borrar");

            // Al momento de presionar el botón "Borrar" se abre una ventana para confirmar la acción.
            {
                delete_button.setOnAction(actionEvent -> {
                    // Obtiene los datos de la fila seleccionada.
                    Employee selected_record = table_view.getItems().get(this.getTableRow().getIndex());

                    // En caso de recibir una respuesta de confirmación se borra al empleado.
                    showConfirmationDialog(
                        "Eliminar Empleado",
                        "¿Estás seguro de eliminar este empleado?"
                    ).ifPresent(response -> {
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
     * Elimina un empleado de la base de datos mediante su ID. Se ejecuta de forma asíncrona un DELETE a la base de
     * datos con la referencia del empleado. Por último, la tabla de empleados se actualiza.
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
     * Crea un botón que se utilizará para agregar nuevos empleados.
     * Al dar clic en el botón, se abre una ventana con un formulario para ingresar los datos del empleado.
     *
     * @return el botón que se crea para agregar nuevos empleados.
     */
    @Override
    protected Button addNewUserButton() {
        Button btn_add_client = new Button("Agregar empleado");
        btn_add_client.setMaxWidth(Double.MAX_VALUE);
        // Establece la acción que se ejecutará cuando se dé clic en el botón.
        // En este caso, abre el formulario del empleado.
        btn_add_client.setOnAction(actionEvent -> new EmployeeForm(this));

        return btn_add_client;
    }
}
