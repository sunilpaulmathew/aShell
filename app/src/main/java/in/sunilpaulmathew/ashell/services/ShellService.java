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
    public String runShellCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            mProcess = Runtime.getRuntime().exec(command, null, null);
            BufferedReader mInput = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            BufferedReader mError = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));

            mProcess.waitFor();

            String line;
            while ((line = mInput.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = mError.readLine()) != null) {
                output.append(line).append("\n");
            }

        }
        catch (Exception ignored) {
        }
        finally {
            if (mProcess != null)
                mProcess.destroy();
        }

        return output.toString();
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
                    if (command.startsWith("logcat")) {
                        callback.onLine(getLogcatLines(line));
                    } else {
                        callback.onLine(line);
                    }
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

    private static String getLogcatLines(String outputLine) {
        if (outputLine == null) return "";

        // Escape HTML special characters
        String safe = outputLine.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        if (safe.contains(" E ")) {
            return "<font color='#F44336'>" + safe + "</font>";
        } else if (safe.contains(" W ")) {
            return "<font color='#FF9800'>" + safe + "</font>";
        } else if (safe.contains(" I ")) {
            return "<font color='#2196F3'>" + safe + "</font>";
        } else if (safe.contains(" D ")) {
            return "<font color='#9E9E9E'>" + safe + "</font>";
        } else if (safe.contains(" V ")) {
            return "<font color='#BDBDBD'>" + safe + "</font>";
        } else {
            return safe; // fallback: use default TextView color
        }
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