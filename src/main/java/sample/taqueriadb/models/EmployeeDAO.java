package sample.taqueriadb.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import sample.taqueriadb.classes.Employee;
import sample.taqueriadb.utils.SQLCommandExecutor;

/**
 * Contiene los métodos para interactuar con la tabla Employee de la base de datos.
 */
public class EmployeeDAO extends SQLCommandExecutor {
    /**
     * Inserta un nuevo empleado a la base de datos.
     *
     * @param employee objeto tipo Employee con los datos del nuevo empleado.
     * @return número de filas afectadas.
     */
    public static int add(Employee employee) throws SQLException {
        String query = String.format(
            "INSERT INTO employee (name, last_name, email, phone_number, email) VALUES ('%s', '%s', '%s', '%s')",
                employee.getName(),
                employee.getLastName(),
                employee.getPhoneNumber(),
                employee.getEmail()
        );

        return executeUpdate(query);
    }

/**
     * Obtiene los datos de un empleado en específico utilizando su ID.
     *
     * @param id del empleado a recuperar.
     * @return objeto tipo ResultSet con los datos del empleado.
     */
    public static ResultSet getEmployeeById(int id) throws SQLException {
        String query = String.format(
            "SELECT * FROM employee WHERE id = %d", id);

        return executeQuery(query);
    }

    /**
     * Obtiene todos los empleados registrados en la base de datos.
     *
     * @return objeto tipo ResultSet con los datos de todos los empleados.
     */
    public static ResultSet getEmployees() throws SQLException {
        String query = "SELECT * FROM employee";

        return executeQuery(query);
    }

    /**
     * Actualiza los datos de un empleado en específico utilizando su ID.
     *
     * @param employee objeto tipo Employee con los datos del empleado a actualizar.
     * @return número de filas afectadas.
     */
    public static int update(Employee employee) throws SQLException {
        String query = String.format(
            "UPDATE employee SET name = '%s', last_name = '%s', email = '%s', phone_number = '%s' WHERE id = %d",
                employee.getName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getId()
        );

        return executeUpdate(query);
    }

    /**
     * Elimina un empleado de la base de datos utilizando su ID.
     *
     * @param id del empleado a ser eliminado.
     * @return número de filas afectadas.
     */
    public static int delete(int id) throws SQLException {
        String query = String.format("DELETE FROM employee WHERE id = %d", id);

        return executeUpdate(query);
    }
}