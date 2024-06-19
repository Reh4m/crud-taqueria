package sample.taqueriadb.base;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.Optional;

/**
 * Plantilla para crear una ventana de lista de usuarios. Esto hace referencia al tipo de usuario que se está manejando
 * en una clase hijo al heredar la clase abstracta. La clase utiliza tipos genéricos (T), que representa el tipo de
 * usuario.
 *
 * @param <T> Clase genérica para definir el tipo de usuario.
 */
public abstract class UsersList<T> extends Stage {
    protected Scene scene;
    protected TableView<T> table_view;
    protected ObservableList<T> users;

    public UsersList(String title) {
        createUI();
        this.setTitle(title);
        this.setScene(scene);
        this.show();
    }

    private void createUI() {
        // Tabla de usuarios.
        table_view = new TableView<>();

        // Muestra las columnas de la tabla con la información de los usuarios.
        showUsersList();

        // Muestra la columna "Editar".
        addEditButtonColumn();

        // Muestra la columna "Borrar".
        addDeleteButtonColumn();

        // Abre una ventana con un formulario para agregar un nuevo usuario.
        Button btn_add_user = addNewUserButton();

        // Layout principal.
        // Contiene la tabla de usuarios.
        VBox container = new VBox();
        container.getChildren().addAll(table_view, btn_add_user);

        // Ventana principal.
        scene = new Scene(container, 500, 500);
    }

    protected abstract ObservableList<T> getUsers();

    protected abstract void showUsersList();

    protected abstract void addEditButtonColumn();

    protected abstract void openEditForm(T old_item);

    protected abstract void addDeleteButtonColumn();

    protected abstract Button addNewUserButton();

    /**
     * Reduce el código necesario para crear una columna en la tabla.
     *
     * @param column_name nombre de la columna.
     * @param property_name nombre de la propiedad.
     * @return columna de la tabla de empleados.
     */
    protected TableColumn<T, String> createColumn(String column_name, String property_name) {
        TableColumn<T, String> column = new TableColumn<>(column_name);

        column.setCellValueFactory(new PropertyValueFactory<>(property_name));

        return column;
    }

//    protected void createActionButtonColumn() {}

    /**
     * Actualiza la tabla de empleados en la interfaz. Su propósito es obtener nuevamente la lista de empleados y
     * actualizar el TableView con los nuevos datos.
     */
    public void refreshTable() {
        // Obtiene la lista de empleados actualizada.
        users = getUsers();

        // Establece nuevamente el contenido del TableView.
        table_view.setItems(users);

        // Actualiza el TableView para reflejar los cambios.
        // Esto es útil en casos donde la fuente de datos subyacente ha cambiado de una forma que no es observada por
        // el propio TableView.
        table_view.refresh();
    }

    /**
     * Muestra una ventana de confirmación al eliminar un usuario.
     *
     * @param title Título de la ventana.
     * @param message Mensaje de confirmación.
     * @return Optional<ButtonType> Contiene la respuesta del usuario a la ventana de confirmación.
     */
    protected Optional<ButtonType> showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);

        return alert.showAndWait();
    }
}
