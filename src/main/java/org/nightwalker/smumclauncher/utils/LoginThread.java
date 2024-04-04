package org.nightwalker.smumclauncher.utils;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import jmccc.microsoft.MicrosoftAuthenticator;
import org.to2mbn.jmccc.auth.AuthenticationException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.nightwalker.smumclauncher.LauncherBody.*;
import static org.nightwalker.smumclauncher.concrollers.SettingsConcroller.loginAuthenticator;


public class LoginThread implements Runnable{
    private CheckBox switchLoginCheckBox ;
    private String URL=null;
    private String CODE = null;
    private TextField loginURIText;
    private Label loginStatement;
    @Override
    public void run() {
        ObjectOutputStream oops = null;
        try {
            loginAuthenticator = MicrosoftAuthenticator.login(it ->{
                String[] strings = it.message.split(" ");
                URL = strings[11];
                CODE = strings[16];
                Platform.runLater(()->{
                    loginURIText.setText("网址："+URL+"  代码："+CODE);
                    try {
                        new webPane(URL);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            loginSuccess = true;
            oops = new ObjectOutputStream(new FileOutputStream(UserData,false));
            oops.writeObject(loginAuthenticator.getSession());
            oops.flush();
            loginFos = new FileOutputStream(login,false);
            if (loginSuccess){
                loginSettings.setProperty("loginSuccess","1");
                loginSettings.store(loginFos,"loginSettings");
                Platform.runLater(()->{
                    switchLoginCheckBox.setDisable(!loginSuccess);
                    switchLoginCheckBox.setSelected(wantToLogin);
                    loginStatement.setText("已登录");
                    Thread toolthread = new Thread(new toolThread("登录成功",200));
                    toolthread.start();
                });
            }

        } catch (AuthenticationException e) {
            Platform.runLater(()->{
                Thread toolthread = new Thread(new toolThread("登录失败",200));
                toolthread.start();
            });
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (oops!=null){
                try {
                    oops.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (loginFos!=null){
                try {
                    loginFos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            Runtime.getRuntime().gc();
        }
    }
    public LoginThread(){
    }
    public LoginThread(CheckBox switchLoginCheckBox, TextField loginURIText, Label loginStatement){
        this.switchLoginCheckBox=switchLoginCheckBox;
        this.loginURIText = loginURIText;
        this.loginStatement = loginStatement;
    }
}
