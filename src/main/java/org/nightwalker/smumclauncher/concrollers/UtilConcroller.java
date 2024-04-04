package org.nightwalker.smumclauncher.concrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class UtilConcroller {
    @FXML
    private TextArea texts;
    public TextArea getTextArea(){
        return this.texts;
    }
    @FXML
    private void initialize(){

    }
}
