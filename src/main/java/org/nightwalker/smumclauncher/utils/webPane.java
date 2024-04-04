package org.nightwalker.smumclauncher.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.nightwalker.smumclauncher.Anchor;

public class webPane{
    private String URL;
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        AnchorPane Root = fxmlLoader.load(Anchor.class.getResource("fxmls/WebPane.fxml"));
        WebView web = (WebView) Root.lookup("#web");
        web.getEngine().load(URL);
        Scene scene = new Scene(Root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> Runtime.getRuntime().gc());
        stage.show();
    }
    public webPane(String URL) throws Exception {
        this.URL = URL;
        start(new Stage());
    }
}
