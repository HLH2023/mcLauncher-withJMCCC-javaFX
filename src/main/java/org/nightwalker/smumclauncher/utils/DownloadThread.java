package org.nightwalker.smumclauncher.utils;


import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloader;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter;
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback;
import org.to2mbn.jmccc.mcdownloader.download.tasks.DownloadTask;
import org.to2mbn.jmccc.option.MinecraftDirectory;
import org.to2mbn.jmccc.version.Version;

import javax.swing.*;
import java.awt.*;

import static org.nightwalker.smumclauncher.LauncherBody.*;
import static org.nightwalker.smumclauncher.concrollers.SettingsConcroller.*;


public class DownloadThread implements Runnable{

    private MinecraftDownloader minecraftDownloader = null;
    private TextArea downloadMessageText = null;
    private String version=null;
    private Label downloadVersionLabel = null;
    private Label downloadStatementLabel = null;

    @Override
    public void run() {
        downloaded = minecraftDownloader.downloadIncrementally(new MinecraftDirectory(downloadDir), version,  new CallbackAdapter<Version>() {
            @Override
            public void done(Version result) {
                downloadStarted = false;
                Platform.runLater(() -> {
                    downloadMessageText.appendText("下载成功:"+result+"\n请重启启动器\n以减轻内存占用\n");
                    downloadVersionLabel.setText("未指定");
                    downloadStatementLabel.setText("空闲");
                    getVersions();
                    iniVersionChooser();
                });
                Thread toolThread =new Thread(new toolThread("下载成功:"+result+"\n请重启启动器\n以减轻内存占用\n",200)) ;
                toolThread.start();
                downloaded = null;
                Runtime.getRuntime().gc();
            }

            @Override
            public void failed(Throwable e) {
                downloadStarted = false;
                Platform.runLater(()->{
                    downloadMessageText.appendText("下载失败\\n原因:"+e.getMessage()+"\n");
                    downloadVersionLabel.setText("未指定");
                    downloadStatementLabel.setText("空闲");});
                Thread toolThread =new Thread(new toolThread("下载失败\n原因:"+e.getMessage(),300)) ;
                toolThread.start();
                Runtime.getRuntime().gc();
            }

            @Override
            public void cancelled() {
                downloadStarted = false;
                Platform.runLater(()->{
                    downloadMessageText.appendText("取消成功"+"\n");
                    downloadVersionLabel.setText("未指定");
                    downloadStatementLabel.setText("空闲");});
                Thread toolThread =new Thread(new toolThread("取消成功",200)) ;
                toolThread.start();
                Runtime.getRuntime().gc();
            }
            @Override
            public void retry(Throwable e, int current, int max) {
                StringBuilder sb = new StringBuilder();
                sb.append("重试中,第[");
                sb.append(current);
                sb.append("/");
                sb.append(max);
                sb.append("]次");
                Platform.runLater(()->{downloadMessageText.appendText("任务"+sb.toString()+"原因"+e.getMessage()+"\n");});
            }

            @Override
            public <R> DownloadCallback<R> taskStart(DownloadTask<R> task) {
                return new DownloadCallback<R>() {
                    @Override
                    public void updateProgress(long l, long l1) {
                    }

                    @Override
                    public void done(R result) {
                        // 当这个DownloadTask完成时调用
                        Platform.runLater(()->{downloadMessageText.appendText("子任务完成:"+task.getURI()+"\n");});

                    }

                    @Override
                    public void failed(Throwable e) {
                        // 当这个DownloadTask失败时调用
                        Platform.runLater(()->{downloadMessageText.appendText("子任务失败："+task.getURI()+"原因："+e.getMessage()+"\n");});
                    }

                    @Override
                    public void cancelled() {
                        // 当这个DownloadTask被取消时调用
                        Platform.runLater(()->{downloadMessageText.appendText("子任务取消："+task.getURI()+"\n");});
                    }

                    @Override
                    public void retry(Throwable e, int current, int max) {
                        // 当这个DownloadTask因出错而重试时调用
                        // 重试不代表着失败
                        // 也就是说，一个DownloadTask可以重试若干次，
                        // 每次决定要进行一次重试时就会调用这个方法
                        // 当最后一次重试失败，这个任务也将失败了，failed()才会被调用
                        // 所以调用顺序就是这样：
                        // retry()->retry()->...->failed()
                        StringBuilder sb = new StringBuilder();
                        sb.append("重试中,第[");
                        sb.append(current);
                        sb.append("/");
                        sb.append(max);
                        sb.append("]次");
                        Platform.runLater(()->{downloadMessageText.appendText("子任务"+task.getURI()+sb.toString()+"原因"+e.getMessage()+"\n");});
                    }
                };
            }
        });
    }

    private void iniVersionChooser() {
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

    public DownloadThread(MinecraftDownloader minecraftDownloader,TextArea downloadMessageText,String version,Label downloadVersionLabel,Label downloadStatementLabel){
        this.minecraftDownloader=minecraftDownloader;
        this.downloadMessageText=downloadMessageText;
        this.downloadVersionLabel=downloadVersionLabel;
        this.downloadStatementLabel=downloadStatementLabel;
        this.version=version;
    }
    private void showJDialog(String response) {
        JDialog jDialog = new JDialog();
        jDialog.setTitle("提示");
        jDialog.setSize(200, 150);
        jDialog.setAlwaysOnTop(true);
        jDialog.setLocationRelativeTo(null);
        jDialog.setModal(true);
        jDialog.setAlwaysOnTop(true);
        jDialog.setResizable(false);

        JLabel jLabel = new JLabel(response);
        jLabel.setFont(new Font("", Font.BOLD, 20));
        jLabel.setHorizontalAlignment(SwingConstants.HORIZONTAL);
        jLabel.setBounds(0, 0, 200, 150);
        jDialog.getContentPane().add(jLabel);


        jDialog.setVisible(true);
    }
}
