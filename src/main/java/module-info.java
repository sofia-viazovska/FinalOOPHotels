module com.example.finaloophotels {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.finaloophotels to javafx.fxml;
    exports com.example.finaloophotels;

    opens App to javafx.fxml;
    exports App;

    opens App.Controllers to javafx.fxml;
    exports App.Controllers;

    opens Models to javafx.fxml;
    exports Models;

    opens Models.DataStructures to javafx.fxml;
    exports Models.DataStructures;

    opens Models.Utils to javafx.fxml;
    exports Models.Utils;
}
