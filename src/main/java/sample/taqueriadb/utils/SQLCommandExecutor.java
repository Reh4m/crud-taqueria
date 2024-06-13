package sample.taqueriadb.utils;

import sample.taqueriadb.db.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Proporciona una utilidad para ejecutar comandos DDL y DML en la base de datos.
 */
public class SQLCommandExecutor {
    private static ResultSet result_set;
    private static int rows_affected;

    /**
     * Ejecuta una consulta SQL y devuelve un objeto tipo ResultSet con la información requerida de una o
     * más tablas de la base de datos.
     *
     * @param query Consulta SQL a ejecutar.
     * @return un objeto ResultSet con el resultado de la consulta.
     */
    public static ResultSet executeQuery(String query) throws SQLException {
        Statement statement = DatabaseConnector.getConnection().createStatement();

        try {
            result_set = statement.executeQuery(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result_set;
    }

    /**
     * Se utiliza para insertar, actualizar o eliminar información de la base de datos (SQL update).
     *
     * @param query Consulta SQL a ejecutar.
     * @return un entero con el número de filas afectadas.
     */
    public static int executeUpdate(String query) throws SQLException {
        Statement statement = DatabaseConnector.getConnection().createStatement();

        try {
            rows_affected = statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows_affected;
    }
}
