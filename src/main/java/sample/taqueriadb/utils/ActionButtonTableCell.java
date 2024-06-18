package sample.taqueriadb.utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

/**
 * Esta clase representa una celda de tabla que contiene un botón de acción.
 * El botón en la celda puede realizar una acción específica dependiendo su uso.
 *
 * @param <T> Representa un genérico para el tipo de dato en la fila de la tabla.
 */
public class ActionButtonTableCell<T> extends TableCell<T, Void> {
    private final Button action_button;

    /**
     * Instancia para crear un nuevo botón de acción en la celda.
     *
     * @param button_text El texto que específica la acción del botón.
     */
    public ActionButtonTableCell(String button_text) {
        this.action_button = new Button(button_text);
    }

    /**
     * Establece la acción que se realizará al hacer clic en el botón.
     *
     * @param eventHandler Evento a realizar al hacer clic en el botón.
     */
    public void setButtonAction(EventHandler<ActionEvent> eventHandler) {
        this.action_button.setOnAction(eventHandler);
    }

    /**
     * Devuelve los datos de la fila que ha sido seleccionada al presionar el botón de acción.
     * Utiliza genéricos para especificar el tipo de dato a obtener.
     *
     * @return los datos de la fila seleccionada.
     */
    private T getCurrentItem() {
        return getTableView().getItems().get(getIndex());
    }

    /**
     * Actualiza el elemento representado por la celda y la visualización de la celda. En otras palabras, renderiza el
     * botón de acción en la celda.
     *
     * @param item El nuevo elemento representado en la celda.
     * @param empty Verifica si la celda está vacía.
     */
    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty) {
            setGraphic(action_button);
        } else {
            setGraphic(null);
        }
    }
}
