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

/**
 * Ventana que muestra un formulario para agregar un nuevo empleado a la base de datos.
 */
public class NewEmployeeForm extends Stage {
    private Scene scene;
    // Cuadrícula para ordenar los elementos del formulario.
    private GridPane grid_pane_form;

    // Entradas de texto.
    private TextField name_input;
    private TextField last_name_input;
    private TextField phone_number_input;
    private TextField email_input;

    public NewEmployeeForm() {
        this.createUI();
        this.setTitle("Agregar nuevo empleado");
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

        // Muestra el formulario para agregar un nuevo empleado.
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
     * Crea y despliega los elementos del formulario para recibir los datos del nuevo empleado.
     * Hace uso de la clase TextField para recibir los datos por medio de una entrada de texto.
     */
    private void showEmployeeForm() {
        // Título.
        Text title = new Text("Ingresa los datos del nuevo empleado");
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

        // Botón para agregar un nuevo empleado.
        Button btn_add_employee = new Button("Agregar empleado");
        btn_add_employee.setMaxWidth(Double.MAX_VALUE);
        btn_add_employee.setOnAction(actionEvent -> addNewEmployee());
        grid_pane_form.add(btn_add_employee, 0, 5, 2, 1);
    }

    /**
     * Obtiene los datos ingresados en el formulario y los asigna a un objeto de tipo Employee.
     *
     * @return objeto de tipo Employee con los datos del nuevo empleado.
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
     * Crea un objeto de tipo Employee con los datos registrados en el formulario y lo inserta en la base de datos.
     */
    private void addNewEmployee() {
        // Se obtienen los datos ingresados en el formulario.
        Employee new_employee = getEmployeeData();

        try {
            int rows_affected = EmployeeDAO.add(new_employee);

            System.out.println("Filas afectadas: " + rows_affected);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Una vez terminado el proceso se cierra la ventana.
            this.close();
        }
    }
}
