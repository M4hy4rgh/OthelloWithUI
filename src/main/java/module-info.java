module ca.othello.othello_v3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens ca.othello.othello_v3 to javafx.fxml;
    exports ca.othello.othello_v3;
}