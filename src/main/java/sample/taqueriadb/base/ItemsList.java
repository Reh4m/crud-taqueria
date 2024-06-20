package sample.taqueriadb.base;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import sample.taqueriadb.utils.ActionButtonTableCell;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Clase abstracta que sirve como plantilla para crear una lista de elementos.
 * La clase utiliza genéricos (T), que representa el tipo de elemento que se está manejando.
 *
 * @param <T> Clase genérica para definir el tipo de dato.
 */
public abstract class ItemsList<T> extends Stage {
    protected Scene scene;
    protected TableView<T> table_view;
    protected ObservableList<T> items;

    public ItemsList(String title) {
        createUI();
        this.setTitle(title);
        this.setScene(scene);
        this.show();
    }

    private void createUI() {
        // Tabla de elementos.
        table_view = new TableView<>();

        // Muestra las columnas de la tabla con la información de los elementos.
        showItemsList();

        // Muestra la columna "Editar".
        addEditButtonColumn();

        // Muestra la columna "Borrar".
        addDeleteButtonColumn();

        // Abre una ventana con un formulario para agregar un nuevo elemento.
        Button btn_add_item = addNewItemButton();

        // Layout principal.
        // Contiene la tabla de elementos.
        VBox container = new VBox();
        container.getChildren().addAll(table_view, btn_add_item);

        // Ventana principal.
        scene = new Scene(container, 500, 500);
    }

    protected abstract ObservableList<T> getItems();

    protected abstract void showItemsList();

    protected abstract void addEditButtonColumn();

    protected abstract void addDeleteButtonColumn();

    protected abstract Button addNewItemButton();

    protected abstract void editButtonAction(T item);

    protected abstract void deleteButtonAction(T item);

    /**
     * Reduce el código necesario para crear una columna (TableColumn) en la tabla (TableView).
     *
     * @param column_name El nombre de la columna. Esta será mostrada en la cabeza de la columna.
     * @param property_name El nombre de la propiedad que será mostrada en la celda de la columna.
     * @return un objeto TableColumn con la información específicada.
     */
    protected TableColumn<T, String> createColumn(String column_name, String property_name) {
        TableColumn<T, String> column = new TableColumn<>(column_name);

        column.setCellValueFactory(new PropertyValueFactory<>(property_name));

        return column;
    }

    /**
     * Crea una nueva columna con botones de acción y los despliega en cada celda de la tabla. La acción a realizar se
     * define en los parámetros. Utiliza genéricos para determinar el tipo de dato que se está manejando.
     *
     * @param button_text El texto que específica la acción del botón.
     * @param action La acción a realizar cuando se pulse el botón.
     */
    protected void createActionButtonColumn(String button_text, Consumer<T> action) {
        TableColumn<T, Void> action_column = new TableColumn<>();

        // Renderiza las celdas de la columna.
        // En este caso, cada celda será un objeto de la clase ActionButtonTableCell.
        action_column.setCellFactory(params -> {
            ActionButtonTableCell<T> action_button = new ActionButtonTableCell<>(button_text);
            // Establece la acción a realizar cuando se pulse el botón.
            // Al hacer clic, se llama al parámetro de acción con el elemento actual de la fila seleccionada.
            action_button.setButtonAction(event -> action.accept(action_button.getCurrentItem()));

            // Devuelve el botón a ser desplegado en la celda.
            return action_button;
        });

        // Agrega la columna de acción a la tabla.
        table_view.getColumns().add(action_column);
    }

    /**
     * Recarga la lista de elementos en el TableView.
     * Esto es útil en casos donde la fuente de datos subyacente ha cambiado de una forma que no es observada por el
     * propio TableView.
     */
    public void refreshTable() {
        // Obtiene la lista actualizada de elementos.
        items = getItems();

        // Establece nuevamente el contenido del TableView.
        table_view.setItems(items);

        // Actualiza el TableView para reflejar los cambios.
        table_view.refresh();
    }

    /**
     * Muestra una ventana de confirmación al eliminar un elemento.
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
