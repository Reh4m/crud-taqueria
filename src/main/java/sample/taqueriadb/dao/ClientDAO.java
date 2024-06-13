package sample.taqueriadb.dao;

import sample.taqueriadb.model.Client;
import sample.taqueriadb.utils.SQLCommandExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Contiene los métodos para interactuar con la tabla Client de la base de datos.
 */
public class ClientDAO extends SQLCommandExecutor {
    /**
     * Inserta un nuevo cliente a la base de datos.
     *
     * @param client objeto tipo Client con los datos del nuevo cliente.
     * @return número de filas afectadas.
     */
    public static int add(Client client) throws SQLException {
        String query = String.format("INSERT INTO client (name) VALUES ('%s')", client.getName());

        return executeUpdate(query);
    }

    /**
     * Obtiene los datos de un cliente en específico utilizando su ID.
     *
     * @param id del cliente a recuperar.
     * @return objeto tipo ResultSet con los datos del cliente.
     */
    public static ResultSet getClientById(int id) throws SQLException {
        String query = String.format("SELECT * FROM client WHERE id_client = %d", id);

        return executeQuery(query);
    }

    /**
     * Obtiene todos los clientes registrados en la base de datos.
     *
     * @return objeto tipo ResultSet con los datos de todos los clientes.
     */
    public static ResultSet getClients() throws SQLException {
        String query = "SELECT * FROM client";

        return executeQuery(query);
    }

    /**
     * Actualiza los datos de un cliente en específico utilizando su ID.
     *
     * @param client objeto tipo Client con los datos del cliente a actualizar.
     * @return número de filas afectadas.
     */
    public static int update(Client client) throws SQLException {
        String query = String.format(
            "UPDATE client SET name = '%s' WHERE id_client = %d", client.getName(), client.getId()
        );

        return executeUpdate(query);
    }

    /**
     * Elimina un cliente de la base de datos utilizando su ID.
     *
     * @param id del cliente a ser eliminado.
     * @return número de filas afectadas.
     */
    public static int delete(int id) throws SQLException {
        String query = String.format("DELETE FROM client WHERE id_client = %d", id);

        return executeUpdate(query);
    }
}
