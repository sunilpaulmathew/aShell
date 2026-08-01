package in.sunilpaulmathew.ashell.services;

import android.os.RemoteException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

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

            /*
             * Both pipes have to be emptied before waiting for the process. They hold
             * 64 KB each, and a process blocked writing to a full pipe never exits, so
             * waiting first hangs on any command with sizeable output.
             */
            StringBuilder errorOutput = new StringBuilder();
            Thread errorThread = new Thread(() -> {
                try {
                    String errorLine;
                    while ((errorLine = mError.readLine()) != null) {
                        errorOutput.append(errorLine).append("\n");
                    }
                } catch (Exception ignored) {
                }
            });
            errorThread.start();

            String line;
            while ((line = mInput.readLine()) != null) {
                output.append(line).append("\n");
            }

            errorThread.join();
            output.append(errorOutput);

            mProcess.waitFor();
        }
        catch (Exception ignored) {
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

                /*
                 * stderr has to be drained while stdout is still being read. The pipe
                 * buffer only holds 64 KB, so a process blocked writing to a full stderr
                 * never closes stdout, and reading the streams one after the other hangs
                 * forever on anything that logs to stderr (dumpsys, pm, ...).
                 */
                AtomicBoolean hasError = new AtomicBoolean(false);
                Thread errorThread = new Thread(() -> {
                    try {
                        String errorLine;
                        while ((errorLine = error.readLine()) != null) {
                            hasError.set(true);
                            callback.onLine("<font color=#FF0000>" + errorLine + "</font>");
                        }
                    } catch (Exception ignored) {
                    }
                });
                errorThread.start();

                String line;
                while ((line = reader.readLine()) != null) {
                    if (command.startsWith("logcat")) {
                        callback.onLine(getLogcatLines(line));
                    } else {
                        callback.onLine(line);
                    }
                }

                errorThread.join();

                int exit = mProcess.waitFor();

                // Handle current directory
                if (command.startsWith("cd ") && !hasError.get()) {
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