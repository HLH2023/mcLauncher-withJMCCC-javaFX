package org.nightwalker.smumclauncher.utils;

import jmccc.microsoft.MicrosoftAuthenticator;
import jmccc.microsoft.entity.MicrosoftSession;
import jmccc.microsoft.entity.MicrosoftVerification;
import org.to2mbn.jmccc.auth.AuthenticationException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.function.Consumer;

import static org.nightwalker.smumclauncher.concrollers.SettingsConcroller.loginAuthenticator;

public class TryToLoginThread implements Runnable {
    private ObjectInputStream ois = null;

    @Override
    public void run() {
        try {
            loginAuthenticator = MicrosoftAuthenticator.session((MicrosoftSession) ois.readObject(), new Consumer<MicrosoftVerification>() {
                @Override
                public void accept(MicrosoftVerification microsoftVerification) {
                    String[] strings = microsoftVerification.message.split(" ");
                    String URL = strings[11];
                    String CODE = strings[16];
                    new ConfirmLoginPane(URL,CODE);
                }
            });
        } catch (AuthenticationException e) {
            Thread toolThread = new Thread(new toolThread("登录失败",200));
            toolThread.start();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            if (ois!=null){
                try {
                    ois.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public TryToLoginThread(ObjectInputStream ois){
        this.ois = ois;
    }
}
