package org.nightwalker.smumclauncher.concrollers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.nightwalker.smumclauncher.Anchor;
import org.nightwalker.smumclauncher.utils.toolThread;
import org.to2mbn.jmccc.auth.OfflineAuthenticator;
import org.to2mbn.jmccc.launch.LaunchException;
import org.to2mbn.jmccc.launch.Launcher;
import org.to2mbn.jmccc.launch.LauncherBuilder;
import org.to2mbn.jmccc.option.*;
import org.to2mbn.jmccc.util.ExtraArgumentsTemplates;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.nightwalker.smumclauncher.LauncherBody.*;
import static org.nightwalker.smumclauncher.concrollers.SettingsConcroller.*;

public class LauncherConcroller {
    @FXML
    private BorderPane launcherBorderPane;
    public static Label versionLabelForPublic=null;
    public static Label loginLabelForPublic=null;
    @FXML
    private Label versionLabel;
    @FXML
    private Label loginLabel;

    @FXML
    private Button settingsButton;
    @FXML
    private ImageView changingImageView;
    @FXML
    private static Image[] images ={
            new Image(Anchor.class.getResourceAsStream("pictures/Slider-MC.png")),
            new Image(Anchor.class.getResourceAsStream("pictures/Slider-SMU.jpg"))
    };
    private static String[] imageUrls={
            "https://8888.band/index.html",
            "https://www.shmtu.edu.cn/"
    };
    private static int imageIndex = 0;

    @FXML
    private void initialize(){
        versionLabelForPublic = versionLabel;
        loginLabelForPublic = loginLabel;
        iniVersionLabelAndLoginLabel();
        iniChangingImageView();
    }

    private void iniChangingImageView() {
        changingImageView.setImage(images[imageIndex]);
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(5), event -> {
            imageIndex = (imageIndex + 1) % images.length;
            changingImageView.setImage(images[imageIndex]);
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void iniVersionLabelAndLoginLabel() {
        if (version!=null){
            if (version.equals("")){
                versionLabelForPublic.setText("当前版本：未选择");
            }else {
                versionLabelForPublic.setText("当前版本："+version);
            }
        }else {
            versionLabel.setText("当前版本：未选择");
        }

        if (loginSuccess&&wantToLogin){
            loginLabelForPublic.setText("登录状态：正版登录");
        }else {
            loginLabelForPublic.setText("登录状态：离线登录");
        }
    }

    @FXML
    private void start(ActionEvent actionEvent) throws IOException {
        ArrayList<String> wrongs = new ArrayList<>();
        if (checkBeforeLaunchPass(wrongs)){
            //iniLaunchingOptions
            MinecraftDirectory mcDirectory = new MinecraftDirectory(rootDir);
            MinecraftDirectory mcRunDirectory = new MinecraftDirectory(rootDir.toString()+"\\versions\\"+version);
            Launcher launcher = LauncherBuilder.create()
                    .printDebugCommandline(true)
                    .nativeFastCheck(false)
                    .build();
            //MicrosoftAuthenticator.login(it -> System.out.println(it.message))
            LaunchOption option = null;
            if (loginSuccess&&wantToLogin){
                option = new LaunchOption(version,loginAuthenticator,mcDirectory);
            }else {
                option = new LaunchOption(version,new OfflineAuthenticator(gameId) ,mcDirectory);
            }
            option.setServerInfo(new ServerInfo(serverIp,Integer.parseInt(serverPort)));
            option.setMaxMemory((int) memoryToUse);
            option.setJavaEnvironment(new JavaEnvironment(new File(JavaVersionMap.get(JavaVersion)+"//bin//java.exe")));
            option.setWindowSize(new WindowSize(1920,1080));
            option.extraJvmArguments().add(ExtraArgumentsTemplates.FML_IGNORE_INVALID_MINECRAFT_CERTIFICATES);
            option.extraJvmArguments().add(ExtraArgumentsTemplates.FML_IGNORE_PATCH_DISCREPANCISE);

            if (versionIsolation){
                option.setRuntimeDirectory(mcRunDirectory);//版本隔离
            }

            if (!extraJVM.equals("")){
                option.extraJvmArguments().add(extraJVM);
            }
            if (!extraMC.equals("")) {
                option.extraMinecraftArguments().add(extraMC);
            }

            Map<String,String> vars =option.commandlineVariables();
            vars.put("version_type", "Made By HLH With JMCCC");

            switch (launchCase){
                case 0:
                    launchAndExit(launcher,option);
                    break;
                case 1:
                    launchAndStayMin(launcher,option);
                    break;
                case 2:
                    launchAndStay(launcher,option);
                    break;
            }
        }else {
            Thread toolThread =new Thread(new toolThread(wrongs.toArray(), 200)) ;
            toolThread.start();
        }
    }

    private void launchAndExit(Launcher launcher,LaunchOption option){
        boolean launchSuccess=true;
        try {
            launcher.launch(option);
        } catch (LaunchException e) {
            Thread toolThread =new Thread(new toolThread("游戏启动失败",200)) ;
            toolThread.start();
            launchSuccess=false;
            throw new RuntimeException(e);
        }finally {
            if (launchSuccess){
                System.exit(0);
            }
        }
    }
    private void launchAndStayMin(Launcher launcher,LaunchOption option){
        try {
            launcher.launch(option);
        } catch (LaunchException e) {
            Thread toolThread =new Thread(new toolThread("游戏启动失败",200)) ;
            toolThread.start();
            throw new RuntimeException(e);
        }finally {
            Stage stage = (Stage)launcherBorderPane.getScene().getWindow();
            stage.setIconified(true);
        }
    }
    private void launchAndStay(Launcher launcher,LaunchOption option) {
        try {
            launcher.launch(option);
        } catch (LaunchException e) {
            Thread toolThread =new Thread(new toolThread("游戏启动失败",200)) ;
            toolThread.start();
            throw new RuntimeException(e);
        }
    }
    private boolean checkBeforeLaunchPass(ArrayList<String> wrongs){
        if (version.equals("无版本")){
            wrongs.add("游戏版本未选择");
        }
        if (rootDir==null){
            wrongs.add("游戏路径未指定");
        }
        if (JavaVersionList.size()==0){
            wrongs.add("未找到JDK");
        }

        return wrongs.size() <= 0;
    }
    @FXML
    public void openSettings(ActionEvent actionEvent) {
        launcherBorderPane.getScene().setRoot(SettingsPane);
        Runtime.getRuntime().gc();
    }

    public void openBrowser(MouseEvent mouseEvent) {
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+ imageUrls[imageIndex]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
