package org.nightwalker.smumclauncher.concrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class ConfirmPaneConcroller {
    @FXML
    private Button deleteButton;
    @FXML
    private void initialize(){

    }
    public void cancelDelete(ActionEvent actionEvent) {
        Stage stage =  (Stage)deleteButton.getScene().getWindow();
        stage.close();
    }
}
