package App.Controllers;

import javafx.fxml.FXML;

/**
 * Controller for the welcome view.
 */
public class WelcomeController {

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void showHotelsView() {
        if (mainController != null) {
            mainController.showHotelsView();
        }
    }

    @FXML
    private void showLoginView() {
        if (mainController != null) {
            mainController.showLoginView();
        }
    }

    @FXML
    private void showRegisterView() {
        if (mainController != null) {
            mainController.showRegisterView();
        }
    }
}
