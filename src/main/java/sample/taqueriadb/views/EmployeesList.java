package sample.taqueriadb.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.taqueriadb.classes.Employee;
import sample.taqueriadb.components.NewEmployeeForm;
import sample.taqueriadb.models.EmployeeDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
        this.showEmployeesList();

        // Se abre una ventana con un formulario para agregar un nuevo empleado.
        Button btn_add_employee = new Button("Agregar empleado");
        btn_add_employee.setMaxWidth(Double.MAX_VALUE);
        btn_add_employee.setOnAction(actionEvent -> new NewEmployeeForm(this));

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
     * Actualiza la tabla de empleados. Su propósito es cargar nuevamente la lista de empleados después de ocurrir una
     * acción en la base de datos, por ejemplo, después de hacer un UPDATE a dicha tabla.
     */
    public void refreshTable() {
        employees = getEmployeesList();

        table_view.setItems(employees);
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
}
