module org.example.demo8 {
    requires javafx.controls;
    requires javafx.fxml;


    requires org.controlsfx.controls;

    requires java.sql;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires openhtmltopdf.pdfbox;
requires mysql.connector.java;
    opens org.example.max to javafx.fxml;
    exports org.example.max;
}