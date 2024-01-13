module sample.taqueriadb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens sample.taqueriadb to javafx.fxml;
    opens sample.taqueriadb.classes;
    exports sample.taqueriadb;
}