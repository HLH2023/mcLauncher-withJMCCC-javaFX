package org.nightwalker.smumclauncher.utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.nightwalker.smumclauncher.Anchor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.nightwalker.smumclauncher.LauncherBody.UserData;
import static org.nightwalker.smumclauncher.LauncherBody.loginSuccess;

public class ConfirmLoginPane{
    private final String CODE;
    private final String URL;
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        AnchorPane root = fxmlLoader.load(Anchor.class.getResource("fxmls/ConfirmPane.fxml"));
        Button confirmDeleteButton = (Button) root.lookup("#confirmDeleteButton");
        Label confirmLabel = (Label) root.lookup("#confirmLabel");
        Button deleteButton = (Button) root.lookup("#deleteButton");

        confirmLabel.setText("登录信息失效，是否选择删除登录配置");
        confirmLabel.setFont(new Font(15));
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
                    Thread toolThread = new Thread(new toolThread("删除登录配置成功",250));
                    toolThread.start();
                    Stage stage =  (Stage)root.getScene().getWindow();
                    stage.close();
                }
            }
        });//设置点击事件

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Thread toolThread = new Thread(new toolThread("登录失效，请新登录\n网址："+URL+"\n代码："+CODE+"\n请在浏览器中打开",400));
                toolThread.start();
                Stage stage =  (Stage)deleteButton.getScene().getWindow();
                stage.close();
            }
        });

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

    public ConfirmLoginPane(String URL,String CODE){
        this.URL = URL;
        this.CODE = CODE;
        try {
            start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
