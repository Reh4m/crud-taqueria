package sample.taqueriadb.ui.employee;

import javafx.application.Platform;
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
import sample.taqueriadb.model.Employee;
import sample.taqueriadb.dao.EmployeeDAO;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * Ventana que muestra un formulario para agregar o modificar un empleado en la base de datos.
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

    // Datos del empleado a modificar.
    Employee old_employee;

    /**
     * Indica la forma en que se usará el formulario. Si es verdadero quiere decir que se agregará un nuevo empleado.
     * Si el valor es falso entonces quiere decir que se modificará un empleado.
     */
    private final boolean is_new_employee;

    /**
     * Crea una instancia para agregar un nuevo empleado.
     *
     * @param employees_list Referencia a la instancia de la clase EmployeesList para llamar a sus métodos internos
     *                       e interactuar con la tabla de Empleados.
     */
    public EmployeeForm(EmployeesList employees_list) {
        // Instancia de la clase EmployeesList para actualizar la lista de empleados.
        this.employees_list = employees_list;

        // Indica que se agregará un nuevo empleado.
        is_new_employee = true;

        setupForm("Agregar nuevo empleado");
    }

    /**
     * Constructor secundario, encargado de recuperar los datos de un empleado en el caso de modificar sus atributos y
     * posteriormente actualizarlos.
     *
     * @param employees_list Referencia a la instancia de la clase EmployeesList para llamar a sus métodos internos
     *                       e interactuar con la tabla de empleados.
     * @param old_employee Objeto tipo Employee con los datos del empleado a modificar.
     */
    public EmployeeForm(EmployeesList employees_list, Employee old_employee) {
        // Instancia de la clase EmployeesList para actualizar la lista de empleados.
        this.employees_list = employees_list;
        // Instancia del objeto Employee para recuperar los datos a actualizar.
        this.old_employee = old_employee;

        // Indica que se modificará un empleado.
        is_new_employee = false;

        // Se crean los campos de texto antes de rellenarlos con los datos del empleado.
        setupForm("Editar Empleado");

        // Se establecen los datos del empleado en sus respectivos campos de texto.
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

        // Botón para agregar o actualizar un empleado.
        Button btn_add_employee = new Button("Agregar");
        btn_add_employee.setMaxWidth(Double.MAX_VALUE);
        btn_add_employee.setOnAction(actionEvent -> onAddButtonClicked());
        grid_pane_form.add(btn_add_employee, 0, 5, 2, 1);
    }

    /**
     * Indica al botón 'Agregar' qué acción realizar al momento de presionar el botón, esto dependiendo del uso que se
     * le esté dando al formulario.
     */
    private void onAddButtonClicked() {
       if (is_new_employee) {
           addNewEmployee();
       } else {
           updateEmployee();
       }
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
     * Agrega un nuevo empleado a la base de datos. Obtiene los datos ingresados en el formulario y hace un INSERT a la
     * base de datos de manera asíncrona. Una vez terminado el proceso, se actualiza la tabla de empleados para mostrar
     * al nuevo empleado agregado. Por último, se cierra la ventana actual.
     */
    private void addNewEmployee() {
        // Se obtienen los datos ingresados en el formulario.
        Employee new_employee = getEmployeeData();

        // Ejecuta el proceso de manera asíncrona. Una vez finalizada la tarea, almacena el resultado del mismo y
        // actualiza la tabla de empleados.
        CompletableFuture.supplyAsync(() -> {
            try {
                return EmployeeDAO.add(new_employee);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(
            // Este bloque de código se ejecuta en el hilo de la aplicación JavaFX, lo cual hace posible ejecutar
            // operaciones en la interfaz de usuario (refreshTable, closeWindow).
            rows_affected -> Platform.runLater(() -> {
                // Actualiza la lista de empleados en la interfaz.
                employees_list.refreshTable();

                System.out.println("Filas afectadas: " + rows_affected);

                // Cierra la ventana actual.
                this.close();
            })
        ).exceptionally(e -> {
            e.printStackTrace();

            return null;
        });
    }

    /**
     * Modifica un empleado existente en la base de datos. Obtiene los datos ingresados en el formulario y actualiza
     * los datos del empleado (UPDATE) de manera asíncrona. Después de ejecutar el proceso, se actualiza la tabla de
     * empleados y se cierra la ventana.
     */
    private void updateEmployee() {
        // Se obtienen los datos ingresados en el formulario.
        Employee new_employee = getEmployeeData();

        // Establece el ID del empleado.
        // Este se obtiene desde la lista de empleados y lo asigna al objeto del empleado modificado.
        // El ID se utiliza dentro del query para saber qué empleado se va a actualizar.
        new_employee.setId(old_employee.getId());

        // Ejecuta el proceso de manera asíncrona. Una vez finalizada la tarea, almacena el resultado del mismo y
        // actualiza la tabla de empleados.
        CompletableFuture.supplyAsync(() -> {
            try {
                return EmployeeDAO.update(new_employee);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(
            // Este bloque de código se ejecuta en el hilo de la aplicación JavaFX, lo cual hace posible ejecutar
            // operaciones en la interfaz de usuario (refreshTable, closeWindow).
            rows_affected -> Platform.runLater(() -> {
                // Actualiza la lista de empleados en la interfaz.
                employees_list.refreshTable();

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
