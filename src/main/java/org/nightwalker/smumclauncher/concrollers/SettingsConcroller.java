package org.nightwalker.smumclauncher.concrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jmccc.microsoft.MicrosoftAuthenticator;
import org.nightwalker.smumclauncher.utils.DownloadThread;
import org.nightwalker.smumclauncher.utils.LoginThread;
import org.nightwalker.smumclauncher.utils.confirmPane;
import org.nightwalker.smumclauncher.utils.toolThread;
import org.to2mbn.jmccc.version.Version;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Future;

import static org.nightwalker.smumclauncher.LauncherBody.*;
import static org.nightwalker.smumclauncher.concrollers.LauncherConcroller.loginLabelForPublic;
import static org.nightwalker.smumclauncher.concrollers.LauncherConcroller.versionLabelForPublic;


public class SettingsConcroller {
    private final String serverRegex =  "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)+([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$";//IPv4/HostRegex
    private final String NumRegex = "\\d+";
    public static HashMap<String,String> JavaVersionMap = new HashMap<>();
    public static ObservableList<String> JavaVersionList = FXCollections.observableArrayList();
    private final ObservableList<String> launchOptions = FXCollections.observableArrayList(
            "启动后自动退出",
            "启动后自动最小化",
            "启动后保持不变");
    private final ObservableList<String> downloadVersions = FXCollections.observableArrayList(
            "1.12.2"
            ,"1.13.2"
            ,"1.14.4"
            ,"1.15.2"
            ,"1.16.5"
            ,"1.17.1"
            ,"1.18.2"
            ,"1.19.4");
    public static boolean downloadStarted=false;
    public static MicrosoftAuthenticator loginAuthenticator = null;

    private File downloadDirCopy;
    public static Future<Version> downloaded = null;
    @FXML
    private BorderPane SettingsPane;
    @FXML
    private AnchorPane GAMEPane;
    @FXML
    private AnchorPane LAUNCHERPane;
    @FXML
    private AnchorPane DOWNLOADPane;

    //GamePaneNodes
    @FXML
    private TextField gameIdText;
    @FXML
    private TextField serverIpText;
    @FXML
    private TextField serverPortText;

    public static ChoiceBox<String> publicversionChooser=null;
    @FXML
    private ChoiceBox<String> versionChooser;
    @FXML
    private TextArea extraJVMText;
    @FXML
    private TextArea extraMCText;
    @FXML
    private Slider memorySlider;
    @FXML
    private TextField memoryText;
    @FXML
    private ChoiceBox<String> javaVersionChooser;
    //LauncherPaneNodes
    @FXML
    private Label gamePathLabel;
    @FXML
    private ChoiceBox<String> launchCaseChoiceBox;
    @FXML
    private Button chooseDownloadPathButton;
    @FXML
    private Label downloadPathLabel;
    @FXML
    private CheckBox samePathCheckBox;
    @FXML
    private CheckBox runDirCheckBox;
    @FXML
    private Button loginButton;
    @FXML
    private TextField loginURIText;
    public static TextField publicLoginURIText;
    @FXML
    private  Label loginStatement;
    @FXML
    private CheckBox switchLoginCheckBox;
    public static CheckBox publicSwitchLoginCheckBox;
    @FXML
    private Button deleteLoginButton;
    @FXML
    private CheckBox launcherAssestsOnCheckBox;

    //downloadNodes
    @FXML
    private ChoiceBox<String> downloadVersionChoiceBox;
    @FXML
    private Label downloadVersionLabel;
    @FXML
    private Label downloadStatementLabel;
    @FXML
    private TextArea downloadMessageText;

    @FXML
    private void initialize(){
        {
            gameIdText.setText(gameId);
            serverIpText.setText(serverIp);
            serverPortText.setText(serverPort);
            iniVersionChooser();
            extraJVMText.setText(extraJVM);
            extraMCText.setText(extraMC);
            iniMemorySlider();
            iniMemoryText();
            iniJavaVersionChooser();
        }//gamePaneIni

        {
            iniGamePathLabel();
            iniLaunchCaseChoiceBox();
            samePathCheckBox.setSelected(pathSame);
            chooseDownloadPathButton.setDisable(pathSame);
            iniDownloadDirCopy();
            iniDownloadPathLabel();
            runDirCheckBox.setSelected(versionIsolation);
            iniLoginStatementLabel();
            iniSwitchLoginCheckBox();
            publicLoginURIText = loginURIText;
            iniLauncherAssestsOnCheckBox();
        }//launcherPaneIni

        {
            iniDownloadMessageText();
            iniDownloadVersionChoiceBox();
        }//downloadPaneIni
        Runtime.getRuntime().gc();
    }



    public void BackToLauncher(ActionEvent actionEvent) {
        SettingsPane.getScene().setRoot(LauncherPane);
        iniVersionLabelAndLoginLabel();
        Runtime.getRuntime().gc();
    }//ToLauncher

    private void iniVersionLabelAndLoginLabel() {
        if (version!=null){
            if (version.equals("")){
                versionLabelForPublic.setText("当前版本：未选择");
            }else {
                versionLabelForPublic.setText("当前版本："+version);
            }
        }else {
            versionLabelForPublic.setText("当前版本：未选择");
        }

        if (loginSuccess&&wantToLogin){
            loginLabelForPublic.setText("登录状态：正版登录");
        }else {
            loginLabelForPublic.setText("登录状态：离线登录");
        }
    }

    public void switchGame(ActionEvent actionEvent) {
        LAUNCHERPane.setVisible(false);
        LAUNCHERPane.setDisable(true);
        DOWNLOADPane.setVisible(false);
        DOWNLOADPane.setDisable(true);
        GAMEPane.setVisible(true);
        GAMEPane.setDisable(false);
        Runtime.getRuntime().gc();
    }//toGamePane

    public void switchLauncher(ActionEvent actionEvent) {
        DOWNLOADPane.setVisible(false);
        DOWNLOADPane.setDisable(true);
        GAMEPane.setVisible(false);
        GAMEPane.setDisable(true);
        LAUNCHERPane.setVisible(true);
        LAUNCHERPane.setDisable(false);
        iniLoginStatementLabel();
        Runtime.getRuntime().gc();
    }//toLauncherPane

    public void switchDonowload(ActionEvent actionEvent) {
        GAMEPane.setVisible(false);
        GAMEPane.setDisable(true);
        LAUNCHERPane.setVisible(false);
        LAUNCHERPane.setDisable(true);
        DOWNLOADPane.setVisible(true);
        DOWNLOADPane.setDisable(false);
        Runtime.getRuntime().gc();
    }//toDownloadPane

    //gamePaneMethods-Start
    private void iniJavaVersionChooser() {
        String versionRegex = "^HKEY_LOCAL_MACHINE\\\\SOFTWARE\\\\JavaSoft\\\\JDK\\\\\\p{ASCII}+$";
        BufferedReader ir = null;
        InputStreamReader i = null;
        Process ps = null;
        try {
            ps = Runtime.getRuntime().exec("reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\JDK");
            ps.getOutputStream().close();
            i = new InputStreamReader(ps.getInputStream());
            String line;
            ir = new BufferedReader(i);
            while ((line = ir.readLine()) != null) {
                if (line.matches(versionRegex)){
                    String[] temp = line.split("\\\\");
                    getJavaHomes(line,temp);
                    JavaVersionList.add(temp[temp.length-1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (!(ir==null)){
                    ir.close();
                }
                if (!(i==null)){
                    i.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        javaVersionChooser.setItems(JavaVersionList);
        if (JavaVersionList.contains(JavaVersion)){
            javaVersionChooser.setValue(JavaVersion);
        }else {
            if (JavaVersionList.size()==0){
                Thread toolThread = new Thread(new toolThread("未找到JDK",200));
                toolThread.start();
            }else{
                JavaVersion = JavaVersionList.get(0);
                javaVersionChooser.setValue(JavaVersion);
            }
        }
    }
    private void getJavaHomes(String line,String[] temp) {
        String JavaHomeRegex = "^\\s+JavaHome.+$";
        String toBeSent = "reg query "+line;
        BufferedReader ir = null;
        InputStreamReader i = null;
        Process ps = null;
        try {
            ps = Runtime.getRuntime().exec(toBeSent);
            ps.getOutputStream().close();
            i = new InputStreamReader(ps.getInputStream());
            String newLine=null;
            ir = new BufferedReader(i);
            while ((newLine = ir.readLine()) != null) {
                if (newLine.matches(JavaHomeRegex)){
                    String[] Temp = newLine.split(" {4}");
                    JavaVersionMap.put(temp[temp.length-1],Temp[Temp.length-1]);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally{
            try {
                if (!(ir==null)){
                    ir.close();
                }
                if (!(i==null)){
                    i.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void iniMemoryText() {
        memoryText.setText(String.valueOf((long)memorySlider.getValue()));
        memoryText.textProperty().addListener((observable,oldValue,newValue)->{
            if (newValue.matches(NumRegex)){
                int memoryInput = Integer.parseInt(newValue);
                if (memoryInput>memory/1024/1024){
                    memorySlider.setValue(memory/1024/1024);
                    memoryText.setText(memory/1024/1024+"");
                    memoryToUse = memory/1024/1024;
                }else if (memoryInput<300){
                    memorySlider.setValue(300);
                    memoryToUse = 300;
                }else{
                    memorySlider.setValue(memoryInput);
                    memoryToUse = memoryInput;
                }
            }else{
                memoryText.setText(oldValue);
            }
        });
    }

    private void iniMemorySlider() {
        memorySlider.setMax(memory/1024/1024);
        memorySlider.setMajorTickUnit(memory/1024/1024/5);
        memorySlider.setValue(memoryToUse);
    }

    private void iniVersionChooser() {
        publicversionChooser = versionChooser;
        publicversionChooser.setItems(versionList);
        if (!checkVersion()){
            if (versionList.size()!=0){
                publicversionChooser.setValue(versionList.get(0));
            }else {
                publicversionChooser.setValue("无版本");
            }
        }else{
            publicversionChooser.setValue(version);
        }
    }
    public static boolean checkVersion() {
        for (int i = 0; i < versionList.size(); i++) {
            if (version.equals(versionList.get(i))){
                return true;
            }
        }
        return false;
    }
    public void SaveGameSettings(ActionEvent actionEvent) throws IOException {
        ArrayList<String> wrongs = new ArrayList<>();
        if ((gameId = gameIdText.getText()).equals("")){
            gameSettings.put("gameId","ID");
            gameId="ID";
            gameIdText.setText(gameId);
        }else {
            gameSettings.put("gameId",gameId);
        }//saveID

        if(serverIpText.getText().matches(serverRegex)){
            serverIp = serverIpText.getText();
            gameSettings.put("serverIp",serverIp);
        }else {
            wrongs.add("ip地址不符合规范");
        }//saveServerIp

        if (serverPortText.getText().equals("")){
            serverPort = "25565";
            gameSettings.put("serverPort",serverPort);
        }else if (Integer.parseInt(serverPortText.getText())>=0 && Integer.parseInt(serverPortText.getText())<=65535){
            serverPort = serverPortText.getText();
            gameSettings.put("serverPort",serverPort);
        }else {
            wrongs.add("端口号不符合规范");
        }
        //saveServerPort

        {
            version = versionChooser.getValue();
            gameSettings.put("version", version);
        }//saveVersion

        {
            extraJVM = extraJVMText.getText();
            gameSettings.put("extraJVM",extraJVM);
        }//saveExtraJVM

        {
            extraMC = extraMCText.getText();
            gameSettings.put("extraMC",extraMC);
        }//saveExtraMC

        {
            gameSettings.put("memoryToUse",String.valueOf(memoryToUse));
        }//saveMemory

        {
            JavaVersion = javaVersionChooser.getValue();
            gameSettings.put("JavaVersion",JavaVersion);
        }//saveJavaVersion
        Thread toolThread;
        if (wrongs.size()!=0){
            toolThread = new Thread(new toolThread(wrongs.toArray(), 200));
        }else {
            toolThread = new Thread(new toolThread("修改成功", 200));
        }
        toolThread.start();
        gameFos = new FileOutputStream(game,false);
        gameSettings.store(gameFos,"GameSettings");

        gameFos.close();
    }//gameSettings
    public void setMemoryText(MouseEvent mouseEvent) {
        memoryText.setText(String.valueOf((long)memorySlider.getValue()));
        memoryToUse = (long)memorySlider.getValue();
    }
    //gamePaneMethods-End

    //launcherPaneMethods-Start
    public void SaveLauncherSettings(ActionEvent actionEvent) {
        ArrayList<String> wrongs = new ArrayList<>();
        if (rootDir==null){
            wrongs.add("游戏路径未设置");
        }
        if (downloadDir==null){
            wrongs.add("下载路径未设置");
        }
        if (wrongs.size()==0){
            launcherSettings.put("rootDir",rootDir.getPath());

            launcherSettings.put("downloadDir",downloadDir.getPath());

            String CASEOption = launchCaseChoiceBox.getValue();

            if (CASEOption.matches("启动后自动退出")){
                launcherSettings.put("launchCase","0");
                launchCase = 0;
            } else if (CASEOption.matches("启动后自动最小化")) {
                launcherSettings.put("launchCase","1");
                launchCase = 1;
            } else if (CASEOption.matches("启动后保持不变")) {
                launcherSettings.put("launchCase","2");
                launchCase = 2;
            }

            if (pathSame){
                launcherSettings.put("pathSame","1");
            }else {
                launcherSettings.put("pathSame","0");
            }

            if (versionIsolation){
                launcherSettings.put("versionIsolation","1");
            }else {
                launcherSettings.put("versionIsolation","0");
            }

            if (launcherAssestsOn){
                launcherSettings.put("launcherAssestsOn","1");
            }else {
                launcherSettings.put("launcherAssestsOn","0");
            }
            try {
                launcherFos = new FileOutputStream(launcher,false);
                launcherSettings.store(launcherFos,"LauncherSettings");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                Thread toolThread = new Thread(new toolThread("修改成功",200));
                toolThread.start();
                if (launcherFos!=null){
                    try {
                        launcherFos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            Thread toolThread = new Thread(new toolThread(wrongs.toArray(),200));
            toolThread.start();
        }

    }
    private void iniLauncherAssestsOnCheckBox() {
        launcherAssestsOnCheckBox.setSelected(launcherAssestsOn);
    }
    private void iniGamePathLabel() {
        if (rootDir!=null){
            gamePathLabel.setText(rootDir.getPath());
        }else {
            gamePathLabel.setText("游戏文件夹未指定");
        }
    }
    private void iniLaunchCaseChoiceBox() {
        launchCaseChoiceBox.setItems(launchOptions);
        launchCaseChoiceBox.setValue(launchOptions.get(launchCase));
    }
    private void iniDownloadPathLabel() {
        if (pathSame){
            if (rootDir!=null){
                downloadPathLabel.setText(rootDir.getPath());
            }else {
                downloadPathLabel.setText("下载文件夹未指定,请设置.Minecraft文件夹");
            }
        }else {
            if (downloadDir!=null){
                downloadPathLabel.setText(downloadDir.getPath());
            }else {
                downloadPathLabel.setText("下载文件夹未指定");
            }
        }
    }
    private void iniDownloadDirCopy() {
        if (downloadDir!=null){
            downloadDirCopy = new File(downloadDir.toURI());
        }
    }
    public void iniLoginStatementLabel() {
        if (loginSuccess){
            loginStatement.setText("已登录");
        }else {
            loginStatement.setText("未登录");
        }
    }
    private void iniSwitchLoginCheckBox() {
        if (loginSuccess){
            switchLoginCheckBox.setSelected(wantToLogin);
        }
        switchLoginCheckBox.setDisable(!loginSuccess);
    }

    public void ChooseGamePath(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File path = null;
        if ( (path = directoryChooser.showDialog(new Stage())) !=null){
            rootDir = path;
            iniGamePathLabel();
            getVersions();
            iniVersionChooser();
            if (pathSame){
                downloadDir = new File(rootDir.toURI());
                iniDownloadPathLabel();
            }
        }
    }

    public void ChooseDownloadPath(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File path = null;
        if ( (path = directoryChooser.showDialog(new Stage())) !=null){
            downloadDir = path;
            downloadDirCopy = new File(path.toURI());
            iniDownloadPathLabel();
        }
    }

    public void switchPathSameAndDifferent(ActionEvent actionEvent) {
        if (pathSame){
            pathSame = false;
            if (downloadDirCopy!=null){
                downloadDir = new File(downloadDirCopy.toURI());
            }else {
                downloadDir = null;
            }

        }else {
            pathSame = true;
            if (rootDir == null){
                Thread toolThread = new Thread(new toolThread("游戏文件夹未设置",200));
                toolThread.start();
            }else {
                downloadDir = new File(rootDir.toURI());
            }
        }
        chooseDownloadPathButton.setDisable(pathSame);
        iniDownloadPathLabel();
    }

    public void switchRunDir(ActionEvent actionEvent) {
        versionIsolation = !versionIsolation;
    }
    public void openRootDir(MouseEvent mouseEvent) {
        if (rootDir!=null){
            if (rootDir.exists()){
                try {
                    Desktop.getDesktop().open(rootDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void openDownloadDir(MouseEvent mouseEvent){
        if (downloadDir!=null){
            if (downloadDir.exists()){
                try {
                    Desktop.getDesktop().open(downloadDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void loginStart(ActionEvent actionEvent) {
        Thread loginThread = new Thread(new LoginThread(switchLoginCheckBox, loginURIText,loginStatement));
        loginThread.start();
    }
    public void switchLogin(ActionEvent actionEvent) {
        wantToLogin = !wantToLogin;
    }
    public void deleteLogin(ActionEvent actionEvent) {
        if (loginSuccess){
            new confirmPane(loginStatement);
        }else {
            Thread toolThread = new Thread(new toolThread("未检测到登录状态",200));
            toolThread.start();
        }
    }
    public void switchLauncherAssests(ActionEvent actionEvent) {
        launcherAssestsOn = !launcherAssestsOn;
    }
    //launcherPaneMethods-End

    //downloadPaneMethods-Start
    public void downloadStart(ActionEvent actionEvent) {
        if (downloadDir==null){
            Thread toolThread = new Thread(new toolThread("下载文件夹未指定",200));
            toolThread.start();
            return;
        }

        if (downloadStarted){
            Thread toolThread = new Thread(new toolThread("已经在下载中",200));
            toolThread.start();

        }else {
            String choosedVersion = downloadVersionChoiceBox.getValue();
            downloadStarted = true;
            downloadMessageText.appendText("下载开始\n");
            downloadVersionLabel.setText(choosedVersion);
            downloadStatementLabel.setText("下载中...");

            DownloadThread downloadThread = new DownloadThread(minecraftDownloader
                    ,downloadMessageText
                    ,choosedVersion
                    ,downloadVersionLabel
                    ,downloadStatementLabel);
            downloadThread.run();
        }
    }
    public void downloadCancel(ActionEvent actionEvent) {
        if (downloaded==null){
            Thread toolThread =new Thread( new toolThread("当前没有在下载的版本",210));
            toolThread.start();
        }else {
            downloaded.cancel(true);
            downloaded=null;
        }
    }
    public void clearDownloadMessageText(ActionEvent actionEvent) {
        downloadMessageText.clear();
    }
    private void iniDownloadMessageText() {
        downloadMessageText.setEditable(false);
    }
    private void iniDownloadVersionChoiceBox() {
        downloadVersionChoiceBox.setItems(downloadVersions);
        downloadVersionChoiceBox.setValue(downloadVersions.get(0));
    }


    //downloadPaneMethods-End
}