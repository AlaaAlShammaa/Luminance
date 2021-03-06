package sample;

import java.awt.*;
import java.io.IOException;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static sample.Main.User32DLL.GetForegroundWindow;
import static sample.Main.User32DLL.GetWindowTextW;

public class Main extends Application {
    private Stage stage;
    MainThread mainThread;
    public static boolean running = true;
    public static boolean noExit = true;
    private static final int MAX_TITLE_LENGTH = 1024;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        Platform.setImplicitExit(false);
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
        stage.initStyle(StageStyle.TRANSPARENT);
        StackPane layout = new StackPane();
        layout.setPrefSize(1, 1);
        Scene scene = new Scene(layout);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        mainThread = new MainThread("MainThread");
        mainThread.start();
    }

    private void addAppToTray() {
        try {
            java.awt.Toolkit.getDefaultToolkit();
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting. ");
                Platform.exit();
            }
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            URL imagePath = Main.class.getResource("icon.png");
            //File f = new File(imagePath.getFile());
            java.awt.Image image = ImageIO.read(imagePath);
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);
            java.awt.MenuItem stopItem = new java.awt.MenuItem("Stop");
            // stop the execution of the thread
            stopItem.addActionListener(event -> {
                if (stopItem.getLabel().equals("Stop")) {
                    stopItem.setLabel("Resume");
                    running = false;
                } else {
                    stopItem.setLabel("Stop");
                    running = true;
                }
            });
            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            stopItem.setFont(boldFont);
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.exit();
                tray.remove(trayIcon);
                noExit = false;
            });
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(stopItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
            tray.add(trayIcon);
        } catch (IOException | AWTException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class MainThread extends Thread {
        Thread t;
        String threadName;
        String ForeWindTitle;
        boolean changed = false;
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        int brightnessLevel = 0;

        MainThread(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("Running Thread is " + threadName);
            GetWindowTextW(GetForegroundWindow(), buffer, MAX_TITLE_LENGTH);
            ForeWindTitle = Native.toString(buffer);

            while (noExit) {
                while (!running)
                    yield();
                try {
                    GetWindowTextW(GetForegroundWindow(), buffer, MAX_TITLE_LENGTH);
                    if (!ForeWindTitle.equals(Native.toString(buffer))) {
                        changed = true;
                        ForeWindTitle = Native.toString(buffer);
                    } else {
                        changed = false;
                    }
                    if (changed && ForeWindTitle.length() != 0 && !ForeWindTitle.contains("Task Switching")) {
                        int brightnessVal = BrightnessManager.computeBrightness();
                        brightnessLevel = Math.round(BrightnessManager.getCurrentBrightness() / 10) * 10;
                        if (brightnessVal < 100 && brightnessVal > 80) {
                            if (brightnessLevel <= 20) {
                                while (brightnessLevel != 60) {
                                    brightnessLevel += 10;
                                    BrightnessManager.setBrightness(brightnessLevel);
                                }
                            } else if (brightnessLevel > 90) {
                                BrightnessManager.setBrightness(80);
                                BrightnessManager.setBrightness(60);
                            }
                        } else if (brightnessVal < 80 && brightnessVal > 60) {
                            if (brightnessLevel <= 50) {
                                while (brightnessLevel != 80) {
                                    brightnessLevel += 10;
                                    BrightnessManager.setBrightness(brightnessLevel);
                                }
                            } else {
                                BrightnessManager.setBrightness(80);
                            }
                        } else if (brightnessVal < 60) {
                            if (brightnessLevel <= 70) {
                                while (brightnessLevel != 100) {
                                    brightnessLevel += 10;
                                    BrightnessManager.setBrightness(brightnessLevel);
                                }
                            } else {
                                BrightnessManager.setBrightness(100);
                            }
                        } else if (brightnessVal > 100 && brightnessVal < 150) {
                            if (brightnessLevel > 70) {
                                while (brightnessLevel != 40) {
                                    brightnessLevel -= 10;
                                    BrightnessManager.setBrightness(brightnessLevel);
                                }
                            } else if (brightnessLevel <= 10) {
                                BrightnessManager.setBrightness(20);
                                BrightnessManager.setBrightness(40);
                            } else {
                                BrightnessManager.setBrightness(40);
                            }

                        } else if (brightnessVal > 150 && brightnessVal < 200) {
                            if (brightnessLevel > 40) {
                                while (brightnessLevel != 20) {
                                    brightnessLevel -= 10;
                                    BrightnessManager.setBrightness(brightnessLevel);
                                }
                            } else {
                                BrightnessManager.setBrightness(20);
                            }
                        } else {
                            if (brightnessLevel >= 30) {
                                while (brightnessLevel != 0) {
                                    brightnessLevel -= 10;
                                    BrightnessManager.setBrightness(brightnessLevel);
                                }
                            }
                            else{
                                BrightnessManager.setBrightness(0);
                            }
                        }
                    }
                    Thread.sleep(600);
                } catch (AWTException | IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public synchronized void start() {
            super.start();
            System.out.println("Starting Thread : " + threadName);
            if (t == null) {
                t = new Thread(this, threadName);
                t.start();
            }
        }


    }

    static class User32DLL {
        static {
            Native.register("user32");
        }

        public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);

        public static native HWND GetForegroundWindow();

        public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
    }
}
