package sample.taqueriadb.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.taqueriadb.classes.Employee;
import sample.taqueriadb.models.EmployeeDAO;
import sample.taqueriadb.views.EmployeesList;

import java.sql.SQLException;
import java.sql.SQLOutput;

/**
 * Ventana que muestra un formulario para agregar o modificar un empleado a la base de datos.
 */
public class EmployeeForm extends Stage {
    private Scene scene;
    // Cuadrícula para ordenar los elementos del formulario.
    private GridPane grid_pane_form;

    // Entradas de texto.
    private TextField name_input;
    private TextField last_name_input;
    private TextField phone_number_input;
    private TextField email_input;

    // Referencia a la clase EmployeesList.
    EmployeesList employees_list;
    // Datos del Empleado a modificar.
    Employee old_employee;

    /**
     * Crea una instancia para agregar un nuevo Empleado.
     *
     * @param employees_list Referencia a la instancia de la clase EmployeesList para llamar a sus métodos internos
     *                       e interactuar con la tabla de Empleados.
     */
    public EmployeeForm(EmployeesList employees_list) {
        // Instancia de la clase EmployeesList para actualizar la lista de empleados.
        this.employees_list = employees_list;

        setupForm("Agregar nuevo empleado");
    }

    /**
     * Constructor secundario, encargado de recuperar los datos de un Empleado en el caso de modificar sus atributos y
     * posteriormente actualizarlos.
     *
     * @param employees_list Referencia a la instancia de la clase EmployeesList para llamar a sus métodos internos
     *                       e interactuar con la tabla de Empleados.
     * @param old_employee objeto tipo Employee con los datos del empleado a modificar.
     */
    public EmployeeForm(EmployeesList employees_list, Employee old_employee) {
        // Instancia de la clase EmployeesList para actualizar la lista de empleados.
        this.employees_list = employees_list;
        // Instancia del usuario Empleado para recuperar los atributos a actualizar.
        this.old_employee = old_employee;

        // Se crean los campos de textos antes de rellenarlos con los datos del Empleado.
        setupForm("Editar Empleado");

        // Una vez creados los campos de texto, se establecen los datos del Empleado en sus respectivos campos de texto.
        name_input.setText(old_employee.getName());
        last_name_input.setText(old_employee.getLastName());
        phone_number_input.setText(old_employee.getPhoneNumber());
        email_input.setText(old_employee.getEmail());
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

        showEmployeeForm();

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
     * Despliega los elementos del formulario para recibir los datos del empleado.
     * Hace uso de la clase TextField para recibir los datos por medio de una entrada de texto.
     */
    private void showEmployeeForm() {
        // Título.
        Text title = new Text("Ingresa los datos del empleado");
        grid_pane_form.add(title, 0, 0, 2, 1);

        // Nombre.
        Label name_label = new Label("Nombre:");
        grid_pane_form.add(name_label, 0, 1);

        name_input = new TextField();
        grid_pane_form.add(name_input, 1, 1);

        // Apellidos.
        Label last_name_label = new Label("Apellidos:");
        grid_pane_form.add(last_name_label, 0, 2);

        last_name_input = new TextField();
        grid_pane_form.add(last_name_input, 1, 2);

        // Número de teléfono.
        Label phone_number_label = new Label("Número de teléfono:");
        grid_pane_form.add(phone_number_label, 0, 3);

        phone_number_input = new TextField();
        grid_pane_form.add(phone_number_input, 1, 3);

        // Email.
        Label email_label = new Label("Correo electrónico:");
        grid_pane_form.add(email_label, 0, 4);

        email_input = new TextField();
        grid_pane_form.add(email_input, 1, 4);

        // Botón para agregar o actualizar un Empleado.
        Button btn_add_employee = new Button("Agregar");
        btn_add_employee.setMaxWidth(Double.MAX_VALUE);
        btn_add_employee.setOnAction(actionEvent -> updateEmployee());
        grid_pane_form.add(btn_add_employee, 0, 5, 2, 1);
    }

    /**
     * Obtiene los datos ingresados en el formulario y los asigna a un objeto de tipo Employee.
     *
     * @return objeto de tipo Employee con los datos del empleado.
     */
    private Employee getEmployeeData() {
        return new Employee(
            name_input.getText(),
            last_name_input.getText(),
            phone_number_input.getText(),
            email_input.getText()
        );
    }

    /**
     * Agrega un nuevo Empleado a la base de datos. Crea un objeto de tipo Employee con los datos registrados en el
     * formulario y lo inserta en la base de datos. Por último, se actualiza la tabla de empleados para mostrar al
     * nuevo empleado agregado.
     */
    private void addNewEmployee() {
        // Se obtienen los datos ingresados en el formulario.
        Employee new_employee = getEmployeeData();

        try {
            int rows_affected = EmployeeDAO.add(new_employee);

            employees_list.refreshTable();

            System.out.println("Filas afectadas: " + rows_affected);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Una vez terminado el proceso se cierra la ventana.
            this.close();
        }
    }

    /**
     * Modifica un Empleado existente en la base de datos. Obtiene los datos ingresados en el formulario y actualiza
     * los datos del Empleado (UPDATE). Por último, se actualiza la tabla de Empleados y se cierra la ventana.
     */
    private void updateEmployee() {
        // Se obtienen los datos ingresados en el formulario.
        Employee new_employee = getEmployeeData();

        new_employee.setId(old_employee.getId());

        try {
            int rows_affected = EmployeeDAO.update(new_employee);

            employees_list.refreshTable();

            System.out.println("Filas afectadas: " + rows_affected);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Una vez terminado el proceso se cierra la ventana.
            this.close();
        }
    }
}
