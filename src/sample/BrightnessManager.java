package sample;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Created by AlaaShammaa on 8/2/2016.
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class BrightnessManager {
    public static void setBrightness(int brightness)
            throws IOException {
        //Creates a powerShell command that will set the brightness to the requested value (0-100), after the requested delay (in milliseconds) has passed.
        String s = String.format("$brightness = %d;", brightness)
                + "$delay = 800;"
                + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                + "$myMonitor.wmisetbrightness($delay, $brightness)";
        String command = "powershell.exe  " + s;
        // Executing the command
        Process powerShellProcess = Runtime.getRuntime().exec(command);

        powerShellProcess.getOutputStream().close();

        //Report any error messages
        String line;

        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        line = stderr.readLine();
        if (line != null) {
            System.err.println("Standard Error:");
            do {
                System.err.println(line);
            } while ((line = stderr.readLine()) != null);

        }
        stderr.close();

    }

    public static int computeBrightness() throws AWTException {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage bi = new Robot().createScreenCapture(screenRect);
        int countLoop = 0;
        double red = 0, green = 0, blue = 0;
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                Color c = new Color(bi.getRGB(x, y));
                red += c.getRed();
                green += c.getGreen();
                blue += c.getBlue();
                countLoop++;
            }
        }
        red /= countLoop;
        green /= countLoop;
        blue /= countLoop;
        return (int) Math.sqrt((red * red * .241) + (green * green * .691) + (blue * blue * .068));
    }
}
