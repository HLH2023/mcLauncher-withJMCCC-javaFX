package org.nightwalker.smumclauncher;

import com.sun.management.OperatingSystemMXBean;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.nightwalker.smumclauncher.utils.TryToLoginThread;
import org.nightwalker.smumclauncher.utils.toolThread;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Properties;

import static org.nightwalker.smumclauncher.concrollers.SettingsConcroller.JavaVersionList;


public class LauncherBody extends Application {

    public static boolean rootDirNofound = false;
    public static File[] roots = File.listRoots();
    public static FXMLLoader fxmlLoader = new FXMLLoader();
    public static BorderPane SettingsPane;
    public static BorderPane LauncherPane;
    public static OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    public static MinecraftDownloader minecraftDownloader = MinecraftDownloaderBuilder.create().build();
    public static File UserData;
    public static File game;
    public static File launcher;
    public static File login;
    public static File rootDir;
    public static File downloadDir;

    static {
        UserData = new File("datas/user.data");
        game = new File("properties/GameSettings.properties");
        launcher = new File("properties/LauncherSettings.properties");
        login = new File("properties/Login.properties");
    }//initialize Files

    public static Properties gameSettings = new Properties();
    public static FileInputStream gameFis;
    public static FileOutputStream gameFos;
    public static Properties launcherSettings = new Properties();
    public static FileInputStream launcherFis;
    public static FileOutputStream launcherFos;
    public static Properties loginSettings = new Properties();
    public static FileInputStream loginFis;
    public static FileOutputStream loginFos;

    //gameSettings
    public static String gameId;
    public static String serverIp;
    public static String serverPort;
    public static ObservableList<String> versionList=FXCollections.observableArrayList();
    public static String version;
    public static String extraJVM;
    public static String extraMC;
    public static long memory = mem.getFreeMemorySize();
    public static long memoryToUse;
    public static String JavaVersion;

    //launcherSettings
    public static Integer launchCase=0;
    public static boolean pathSame;
    public static boolean versionIsolation;
    public static boolean launcherAssestsOn;
    public static StringBuilder TXTStringBuilder;

    //loginSettings
    public static boolean loginSuccess;
    public static boolean wantToLogin;

    //ini Starts From Here
    static {
        try {
            gameFis = new FileInputStream(game);
            gameSettings.load(gameFis);

            launcherFis = new FileInputStream(launcher);
            launcherSettings.load(launcherFis);

            loginFis = new FileInputStream(login);
            loginSettings.load(loginFis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (gameFis!=null){
                    gameFis.close();
                }
                if (launcherFis!=null){
                    launcherFis.close();
                }
                if (loginFis!=null){
                    loginFis.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }//initiate FISs & Properties
        {
            iniRootDir();
            launchCase=Integer.parseInt(launcherSettings.getProperty("launchCase"));
            iniPathSame();
            iniDownloadDir();

            iniVersionIsolation();
            iniLauncherAssests();
            iniTXTStringBuilder();
        }//launcherSettings
        {
            gameId = gameSettings.getProperty("gameId");
            serverIp = gameSettings.getProperty("serverIp");
            serverPort = gameSettings.getProperty("serverPort");
            version = gameSettings.getProperty("version");
            if(rootDir!=null){
                getVersions();
            }
            extraJVM = gameSettings.getProperty("extraJVM");
            extraMC = gameSettings.getProperty("extraMC");
            JavaVersion = gameSettings.getProperty("JavaVersion");
            getMemory();
        }//gameSettings
        {
            iniLoginStatement();
            iniWantToLogin();
        }//loginSettings
    }//iniProperties

    private static void iniTXTStringBuilder() {
        if (launcherAssestsOn){
            TXTStringBuilder = new StringBuilder();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader( new File("txts/readme.txt"), Charset.forName("UTF-8")));
                String get = null;
                while ((get = br.readLine())!=null){
                    TXTStringBuilder.append(get);
                    TXTStringBuilder.append("\n");
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (br!=null){
                    try {
                        br.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }

    private static void iniLauncherAssests() {
        launcherAssestsOn = launcherSettings.getProperty("launcherAssestsOn").equals("1");
    }

    private static void iniWantToLogin() {
        wantToLogin = loginSettings.getProperty("wantToLogin").equals("1");
    }

    private static void iniLoginStatement(){
        loginSuccess = loginSettings.get("loginSuccess").equals("1");
        if (loginSuccess){
            ObjectInputStream ois = null;
            FileInputStream fisTest = null;
            try {
                File file = new File("datas/user.data");
                fisTest = new FileInputStream(file);
                if (fisTest.read()==(-1)){
                    Thread toolThread = new Thread(new toolThread("登录状态异常\n请重新登录",200));
                    toolThread.start();
                    loginSuccess = false;
                }else {
                    ois= new ObjectInputStream(new FileInputStream(file));

                    Thread tryToLogin = new Thread(new TryToLoginThread(ois));
                    tryToLogin.start();
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {

                    if (fisTest!=null){
                        fisTest.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private static void iniVersionIsolation() {
        versionIsolation = launcherSettings.getProperty("versionIsolation").equals("1");
    }

    private static void iniPathSame() {
        pathSame = launcherSettings.getProperty("pathSame").equals("1");
    }

    private static void iniDownloadDir() {
        if (!launcherSettings.get("downloadDir").equals("")){
            downloadDir = new File(launcherSettings.getProperty("downloadDir"));
            if (!downloadDir.exists()){
                downloadDir=null;
            }
        } else if (rootDirNofound&&pathSame) {
            downloadDir = new File(rootDir.getPath());
        }
    }

    private static void iniRootDir() {
        if (!launcherSettings.get("rootDir").equals("")){
            rootDir = new File(launcherSettings.getProperty("rootDir"));
            if (!rootDir.exists()){
                for (int i = 0; i < roots.length; i++) {
                    if (roots[i].toString().equals("C:\\")){
                        if (roots.length==1){
                            rootDir = new File(roots[i]+"SMUMCLauncherGames");
                            if (!rootDir.exists()){
                                rootDir.mkdirs();
                            }
                            rootDirNofound = true;
                            break;
                        }
                        continue;
                    }else{
                        rootDir = new File(roots[i]+"SMUMCLauncherGames");
                        if (!rootDir.exists()){
                            rootDir.mkdirs();
                        }
                        rootDirNofound = true;
                        break;
                    }
                }
            }
        }else {
            for (int i = 0; i < roots.length; i++) {
                if (roots[i].toString().equals("C:\\")){
                    if (roots.length==1){
                        rootDir = new File(roots[i]+"SMUMCLauncherGames");
                        if (!rootDir.exists()){
                            rootDir.mkdirs();
                        }
                        rootDirNofound = true;
                        break;
                    }
                    continue;
                }else{
                    rootDir = new File(roots[i]+"SMUMCLauncherGames");
                    if (!rootDir.exists()){
                        rootDir.mkdirs();
                    }
                    rootDirNofound = true;
                    break;
                }
            }
            launcherSettings.put("rootDir",rootDir.getPath());
        }
    }
    private static void getMemory() {
        if (gameSettings.getProperty("memoryToUse")==null||gameSettings.getProperty("memoryToUse").equals("")){
            memoryToUse = memory/1024/1024/10*7;
            gameSettings.put("memoryToUse", String.valueOf(memoryToUse));
        }else {
            memoryToUse = Long.parseLong(gameSettings.getProperty("memoryToUse"));
            if (memoryToUse>memory/1024/1024/10*8){
                memoryToUse = memory/1024/1024/10*7;
                gameSettings.put("memoryToUse", String.valueOf(memoryToUse));
            }
        }
    }
    public static void getVersions(){
        versionList = FXCollections.observableArrayList();
        File versions = new File(rootDir.toString()+"\\versions");
        if (versions.exists()){
            for (int i = 0; i < versions.list().length; i++) {
                versionList.add(versions.list()[i]);
                if (version==null||version.equals("")){
                    version = versionList.get(0);
                }
            }
        }else {
            if (version==null||version.equals("")){
                version = "无版本";
            }
        }

    }
    @Override
    public void start(Stage stage) throws Exception {
        SettingsPane = fxmlLoader.load(Anchor.class.getResource("fxmls/Settings.fxml"));
        LauncherPane = fxmlLoader.load(Anchor.class.getResource("fxmls/Launcher.fxml"));

        Scene scene = new Scene(LauncherPane);
        stage.setScene(scene);
        stage.setResizable(false);

        stage.getIcons().add(new Image(Anchor.class.getResourceAsStream("pictures/Icon.png" )));
        stage.setTitle("SMU-MC启动器");
        stage.setOnCloseRequest(event -> {
            closeAll();
        });
        stage.show();
        checkFirst();
    }
    public static void main(String[] args) {
        launch();
    }
    private void checkFirst(){
        checkLauncherAssests();
        checkRootDir();
        ArrayList<String> firstCheckWrong = new ArrayList<>();
        if (versionList.size()==0){
            firstCheckWrong.add("版本列表为空");
        }
        if (JavaVersionList.size()==0){
            firstCheckWrong.add("未找到Java版本");
        }
        if (rootDir==null){
            firstCheckWrong.add("游戏路径未设置");
        }
        if (firstCheckWrong.size()!=0){
            Thread toolThread =new Thread(new toolThread(firstCheckWrong.toArray(),200)) ;
            toolThread.start();
        }
    }

    private void checkLauncherAssests() {
        if (launcherAssestsOn){
            Thread toolThread = new Thread(new toolThread(TXTStringBuilder.toString(),900));
            toolThread.start();
        }
    }

    private void checkRootDir() {
        if (rootDirNofound){
            Thread thread = new Thread(new toolThread("未检测到游戏文件夹，自动创建在:\n"+rootDir.toString()+"\n请手动前往“启动器设置”页面进行“确认修改”",400));
            thread.start();
        }
    }

    private void close() throws IOException {
         if (minecraftDownloader!=null && !minecraftDownloader.isShutdown()){
            minecraftDownloader.shutdown();
        }
        if (gameFis!=null){
            gameFis.close();
        }
        if (gameFos!=null){
            gameFos.close();
        }
        if (launcherFis!=null){
            launcherFis.close();
        }
        if (launcherFos!=null){
            launcherFos.close();
        }
    }
    public void closeAll() {
        try {
            loginFos = new FileOutputStream(login);
            if (loginSuccess){
                loginSettings.put("loginSuccess","1");
            }else {
                loginSettings.put("loginSuccess","0");
            }
            if (wantToLogin){
                loginSettings.put("wantToLogin","1");
            }else {
                loginSettings.put("wantToLogin","0");
            }
            loginSettings.store(loginFos,"loginSettings");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (loginFos!=null){
                try {
                    loginFos.close();
                    close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.exit(0);
    }
}