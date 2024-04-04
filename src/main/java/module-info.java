module org.nightwalker.smumclauncher {
    requires javafx.controls;
    requires javafx.fxml;

    requires jmccc;
    requires jmccc.mcdownloader;
    requires jdk.management;
    requires java.prefs;
    requires java.desktop;
    requires jmccc.microsoft.authenticator;
    requires javafx.web;

    opens org.nightwalker.smumclauncher to javafx.fxml;
    exports org.nightwalker.smumclauncher;
    exports org.nightwalker.smumclauncher.concrollers;
    opens org.nightwalker.smumclauncher.concrollers to javafx.fxml;
    exports org.nightwalker.smumclauncher.utils;
    opens org.nightwalker.smumclauncher.utils to javafx.fxml;
}