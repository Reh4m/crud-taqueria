package sample.taqueriadb.db;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Proporciona una utilidad para conectarse a la base de datos.
 */
public class DatabaseConnector {
    // Variables de entorno para la conexión a la base de datos.
    private static final Properties properties = new Properties();
    // Almacena la conexión a la base de datos.
    private static Connection connection;

    private static void loadProperties() {
        try {
            properties.load(new FileReader("src/main/resources/db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Crea la conexión a la base de datos.
    public static void createConnection() {
        // Verifica si la conexión ya existe.
        if (connection != null) return;

        // Carga las credenciales de la base de datos.
        loadProperties();

        try {
            connection = DriverManager.getConnection(
                properties.getProperty("JDBC_URL"),
                properties.getProperty("USER"),
                properties.getProperty("PASSWORD")
            );

            System.out.println("Conexión exitosa.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se utiliza para obtener la conexión a la base de datos desde cualquier lugar.
     *
     * @return la conexión a la base de datos.
     */
    public static Connection getConnection() {
        return connection;
    };

    // Cierra la conexión actual a la base de datos.
    public static void closeConnection() {
        if (connection == null) return;

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se utiliza para cerrar un Statement dado.
     *
     * @param statement Statement a cerrar.
     */
    public static void closeStatement(Statement statement) {
        if (statement == null) return;

        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se utiliza para cerrar un ResultSet dado.
     *
     * @param result ResultSet a cerrar.
     */
    public static void closeResultSet(ResultSet result) {
        if (result == null) return;

        try {
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
