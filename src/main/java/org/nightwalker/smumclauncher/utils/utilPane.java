package org.nightwalker.smumclauncher.utils;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.nightwalker.smumclauncher.Anchor;

import static org.nightwalker.smumclauncher.LauncherBody.fxmlLoader;


public class utilPane{
    private String[] args;
    private String arg;
    private int width;
    private int height;
    public TextArea text=null;
    public TextArea getText(){
        return text;
    }
    public void setArg(String arg){
        this.arg = arg;
    }
    public String getArg(){
        return this.arg;
    }

    public void start(Stage stage) throws Exception {
        height = width*9/16;
        AnchorPane Root = fxmlLoader.load(Anchor.class.getResource("fxmls/UtilPane.fxml"));
        Root.setPrefWidth(width);
        Root.setPrefHeight(height);
        text =(TextArea) Root.lookup("#texts");
        text.setText(arg);
        text.setEditable(false);

        Scene scene = new Scene(Root);

        stage.setTitle("提示");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.setAlwaysOnTop(true);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Runtime.getRuntime().gc();
            }
        });
        stage.show();
    }

    public utilPane(int width){
        this.width = width;
    }
    public utilPane(String arg,int width) throws Exception {
        this.arg = arg;
        this.width = width;
        start(new Stage());
    }
    public utilPane(Object[] args,int width) throws Exception {
        this.width = width;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
           sb.append((String)args[i]);
           sb.append("\r\n");
          this.width =width*10/9;
        }
        arg = sb.toString();
        start(new Stage());
    }
}
