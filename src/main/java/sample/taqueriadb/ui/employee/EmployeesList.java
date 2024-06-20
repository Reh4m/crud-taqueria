package sample.taqueriadb.ui.employee;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import sample.taqueriadb.model.Employee;
import sample.taqueriadb.dao.EmployeeDAO;
import sample.taqueriadb.base.ItemsList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Ventana que muestra la lista de empleados registrados.
 */
public class EmployeesList extends ItemsList<Employee> {

    public EmployeesList() {
        super("Lista de empleados");
    }

    /**
     * Obtiene la lista de empleados y los guarda en un ObservableList como un objeto de tipo Employee.
     *
     * @return ObservableList con la información de los empleados.
     */
    @Override
    protected ObservableList<Employee> getItems() {
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
    protected void showItemsList() {
        // Guarda los empleados recuperados desde la base de datos.
        items = getItems();

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
     * Abre el formulario EmployeeForm al presionar el botón "Editar".
     * Este método crea una instancia de EmployeeForm, pasando la instancia actual de EmployeesList y el objeto
     * Employee del empleado a modificar.
     *
     * @param old_employee El empleado a ser modificado.
     */
    @Override
    protected void editButtonAction(Employee old_employee) {
        new EmployeeForm(this, old_employee);
    }

    /**
     * Agrega una columna con botones "Borrar" y los despliega en cada celda de la tabla.
     */
    @Override
    protected void addDeleteButtonColumn() {
        createActionButtonColumn("Borrar", this::deleteButtonAction);
    }

    /**
     * Implementa una ventana de confirmación previo a la eliminación de un empleado.
     * La ventana despliega los botones "Aceptar" y "Cancelar". Al presionar en "Aceptar", se elimina al empleado.
     *
     * @param employee Datos del empleado a ser eliminado.
     */
    @Override
    protected void deleteButtonAction(Employee employee) {
        showConfirmationDialog(
            "Eliminar empleado",
            "¿Estás seguro de eliminar este empleado?"
        ).ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteEmployee(employee.getId());
            }
        });
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
    protected Button addNewItemButton() {
        Button btn_add_employee = new Button("Agregar empleado");
        btn_add_employee.setMaxWidth(Double.MAX_VALUE);
        // Establece la acción que se ejecutará cuando se dé clic en el botón.
        // En este caso, abre el formulario del empleado.
        btn_add_employee.setOnAction(actionEvent -> new EmployeeForm(this));

        return btn_add_employee;
    }
}
