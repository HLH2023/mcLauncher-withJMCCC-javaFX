package org.nightwalker.smumclauncher.utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.nightwalker.smumclauncher.Anchor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.nightwalker.smumclauncher.LauncherBody.UserData;
import static org.nightwalker.smumclauncher.LauncherBody.loginSuccess;

public class confirmPane{
    private Label loginStatement;
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        AnchorPane root = fxmlLoader.load(Anchor.class.getResource("fxmls/ConfirmPane.fxml"));
        Button confirmDeleteButton = (Button) root.lookup("#confirmDeleteButton");


        confirmDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loginSuccess = false;
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(UserData,false);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (fos!=null){
                        try {
                            fos.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    loginStatement.setText("未登录");
                    Thread toolThread = new Thread(new toolThread("删除成功",200));
                    toolThread.start();
                    Stage stage =  (Stage)root.getScene().getWindow();
                    stage.close();
                }
            }
        });//设置点击事件



        Scene scene = new Scene(root);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.UTILITY);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Runtime.getRuntime().gc();
            }
        });
        stage.show();
    }
    public confirmPane(Label loginStatement) {
        this.loginStatement = loginStatement;
        try {
            start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
