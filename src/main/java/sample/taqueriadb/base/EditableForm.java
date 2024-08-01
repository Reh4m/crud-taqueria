package sample.taqueriadb.base;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Clase abstracta que sirve como plantilla para crear un formulario de datos.
 * Utiliza clases genéricas para representar el tipo de dato que se está manejando.
 *
 * @param <T> Define el tipo de elemento que será agregado o modificado.
 * @param <U> Define la lista que contiene la información del elemento.
 */
public abstract class EditableForm<T, U> extends Stage {
    protected Scene scene;
    protected GridPane grid_pane_form;

    // Referencia a los hijos de la clase abstracta ItemsList.
    protected U items_list;
    // Datos del elemento a modificar.
    protected T old_item;

    /**
     * Indica la forma en que se usará el formulario.
     * Verdadero si es para agregar y falso si es para modificar un item.
     */
    protected final boolean is_new_item;

    /**
     * Crea una instancia para agregar un nuevo elemento a los hijos de ItemsList.
     *
     * @param items_list Referencia a la instancia ItemsList para llamar a sus métodos internos e interactuar con su
     *                   lista de elementos.
     */
    public EditableForm(String title, U items_list) {
        this.items_list = items_list;

        is_new_item = true;

        createUI(title);
    }

    /**
     * Constructor secundario, se utiliza para modificar un elemento existente. Recupera los datos del elemento a
     * modificar para posteriormente poder actualizarlos.
     *
     * @param items_list Referencia a la instancia ItemsList para llamar a sus métodos internos e interactuar con su
     *                   lista de elementos.
     * @param old_item Objeto genérico con la información del elemento a modificar.
     */
    public EditableForm(String title, U items_list, T old_item) {
        this.items_list = items_list;

        this.old_item = old_item;

        is_new_item = false;

        createUI(title);
    }

    /**
     * Establece la configuración básica de la ventana.
     *
     * @param title Título de la ventana.
     */
    private void createUI(String title) {
        setupForm();

        this.setTitle(title);
        this.setScene(scene);
        this.show();
    }

    /**
     * Establece la configuración inicial del formulario utilizando un GridPane como contenedor.
     * Este método es llamado durante la creación de la UI.
     */
    private void setupForm() {
        // Contenedor de los elementos del formulario.
        grid_pane_form = new GridPane();
        grid_pane_form.setAlignment(Pos.CENTER);
        grid_pane_form.setHgap(10);
        grid_pane_form.setVgap(10);
        grid_pane_form.setPadding(new Insets(25, 25, 25, 25));

        displayFormElements();

        // Layout principal.
        VBox container = new VBox();
        container.getChildren().add(grid_pane_form);
        container.setSpacing(5);
        container.setPadding(new Insets(5));

        scene = new Scene(container);
    }

    protected abstract void displayFormElements();

    protected abstract T getFormData();

    protected abstract void onSaveButtonClicked();
}