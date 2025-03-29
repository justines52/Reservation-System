
   module reservationsystem{
       requires javafx.fxml;
       requires java.sql;
       requires com.jfoenix;
       requires javafx.controls;
       requires org.kordamp.ikonli.javafx;
       requires java.smartcardio;
       requires mysql.connector.j;

       opens reservationsystem to javafx.fxml;
    opens reservationsystem.controllers to javafx.fxml;
    exports reservationsystem;
}