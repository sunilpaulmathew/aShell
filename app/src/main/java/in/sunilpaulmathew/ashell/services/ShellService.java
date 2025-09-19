package in.sunilpaulmathew.ashell.services;

import android.os.RemoteException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import sunilpaulmathew.ashell.IShellCallback;
import sunilpaulmathew.ashell.IShellService;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 18, 2025
 */
public class ShellService extends IShellService.Stub {

    private static Process mProcess = null;
    private static String mDir = "/";

    @Override
    public void destroyProcess() {
        if (mProcess != null) mProcess.destroy();
    }

    @Override
    public void runCommand(String command, IShellCallback callback) {
        new Thread(() -> {
            try {
                mProcess = Runtime.getRuntime().exec(
                        new String[] {
                                "sh", "-c", command
                        }, null, new File(mDir)
                );
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(mProcess.getInputStream())
                );
                BufferedReader error = new BufferedReader(
                        new InputStreamReader(mProcess.getErrorStream())
                );

                String line;
                boolean hasError = false;
                while ((line = reader.readLine()) != null) {
                    callback.onLine(line);
                }

                while ((line = error.readLine()) != null) {
                    hasError = true;
                    callback.onLine("<font color=#FF0000>" + line + "</font>");
                }

                int exit = mProcess.waitFor();

                // Handle current directory
                if (command.startsWith("cd ") && !hasError) {
                    mDir = getDir(command);
                }

                callback.onFinished(exit);
            } catch (Exception e) {
                try {
                    callback.onFinished(-1);
                } catch (RemoteException ignored) {}
            }
        }).start();
    }

    private static String getDir(String command) {
        String[] array = command.split("\\s+");
        String dir;
        if (array[array.length - 1].equals("/")) {
            dir = "/";
        } else if (array[array.length - 1].startsWith("/")) {
            dir = array[array.length - 1];
        } else {
            dir = mDir + array[array.length - 1];
        }
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        return dir;
    }

}