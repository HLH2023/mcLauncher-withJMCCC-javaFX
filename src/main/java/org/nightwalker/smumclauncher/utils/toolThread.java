package org.nightwalker.smumclauncher.utils;

import javafx.application.Platform;

public class toolThread implements Runnable{
    private int CASE = 0;
    private String arg;
    private Object[] args;
    private int width;
    @Override
    public void run() {

        switch (CASE){
            case 1:
                Platform.runLater(()->{
                    try {
                        new utilPane(arg,width);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                break;
            case 2:
                Platform.runLater(()->{
                    try {
                        new utilPane(args,width);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                break;
        }
    }
    private toolThread(){

    }
    public toolThread(String arg,int width){
        CASE = 1;
        this.arg = arg;
        this.width = width;

    }
    public toolThread(Object[] args,int width){
        CASE = 2;
        this.args = args;
        this.width = width;
    }
}
