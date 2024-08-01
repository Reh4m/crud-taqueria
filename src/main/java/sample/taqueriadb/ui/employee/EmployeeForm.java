package sample.taqueriadb.ui.employee;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import sample.taqueriadb.base.EditableForm;
import sample.taqueriadb.model.Employee;
import sample.taqueriadb.dao.EmployeeDAO;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * Ventana que muestra un formulario para agregar o modificar un empleado en la base de datos.
 */
public class EmployeeForm extends EditableForm<Employee, EmployeesList> {
    // Entradas de texto.
    private TextField name_input;
    private TextField last_name_input;
    private TextField phone_number_input;
    private TextField email_input;

    public EmployeeForm(EmployeesList employees_list) {
        super("Agregar nuevo empleado", employees_list);
    }

    public EmployeeForm(EmployeesList employees_list, Employee old_employee) {
        super("Editar empleado", employees_list, old_employee);

        // Se establecen los datos del empleado en sus respectivos campos de texto.
        name_input.setText(old_employee.getName());
        last_name_input.setText(old_employee.getLastName());
        phone_number_input.setText(old_employee.getPhoneNumber());
        email_input.setText(old_employee.getEmail());
    }

    /**
     * Despliega los elementos del formulario para recibir los datos del empleado.
     */
    protected void displayFormElements() {
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
        btn_add_employee.setOnAction(actionEvent -> onSaveButtonClicked());
        grid_pane_form.add(btn_add_employee, 0, 5, 2, 1);
    }

    /**
     * Proporciona los datos ingresados en el formulario.
     *
     * @return objeto de tipo Employee con los datos del empleado.
     */
    protected Employee getFormData() {
        return new Employee(
            name_input.getText(),
            last_name_input.getText(),
            phone_number_input.getText(),
            email_input.getText()
        );
    }

    /**
     * Maneja tanto la creación de un nuevo empleado como la actualización de uno existente.
     * Obtiene los datos del formulario y realiza una operación INSERT o UPDATE en la base de datos de forma asíncrona.
     * Tras ejecutar el proceso, actualiza la tabla de empleados y cierra la ventana.
     */
    protected void onSaveButtonClicked() {
        // Obtiene los datos ingresados en el formulario.
        Employee new_employee = getFormData();

        // Establece el ID del empleado en caso de actualizar.
        // Este se obtiene desde la lista de empleados y lo asigna al objeto del empleado modificado.
        // El ID se utiliza dentro del query para saber qué empleado se va a actualizar.
        if (!is_new_item) new_employee.setId(old_item.getId());

        // Ejecuta el proceso de manera asíncrona. Una vez finalizada la tarea, almacena el resultado del mismo y
        // actualiza la tabla de empleados.
        CompletableFuture.supplyAsync(() -> {
            try {
                return is_new_item ? EmployeeDAO.add(new_employee) : EmployeeDAO.update(new_employee);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(
            // Este bloque de código se ejecuta en el hilo de la aplicación JavaFX, lo cual hace posible ejecutar
            // operaciones en la interfaz de usuario (refreshTable, closeWindow).
            rows_affected -> Platform.runLater(() -> {
                // Actualiza la lista de empleados en la interfaz.
                items_list.refreshTable();

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
