package in.sunilpaulmathew.ashell.services;

import android.os.RemoteException;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import in.sunilpaulmathew.ashell.utils.Utils;
import sunilpaulmathew.ashell.IShellCallback;
import sunilpaulmathew.ashell.IShellService;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on Sept. 18, 2025
 */
public class ShellService extends IShellService.Stub {

    private static final int BATCH_SIZE = 500;
    private static final long BATCH_INTERVAL_MS = 50;

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
                 *
                 * Each reader keeps its own batch, so the two never share state.
                 */
                AtomicBoolean hasError = new AtomicBoolean(false);
                Thread errorThread = new Thread(() -> {
                    try {
                        List<String> errorBatch = new ArrayList<>();
                        long lastFlush = System.currentTimeMillis();
                        String errorLine;
                        while ((errorLine = error.readLine()) != null) {
                            hasError.set(true);
                            errorBatch.add("<font color=#FF0000>" + Utils.escapeHtml(errorLine) + "</font>");
                            lastFlush = flushBatch(errorBatch, lastFlush, callback);
                        }
                        flushBatch(errorBatch, callback);
                    } catch (Exception ignored) {
                    }
                });
                errorThread.start();

                List<String> batch = new ArrayList<>();
                long lastFlush = System.currentTimeMillis();
                String line;
                while ((line = reader.readLine()) != null) {
                    batch.add(command.startsWith("logcat") ? getLogcatLines(line) : Utils.escapeHtml(line));
                    lastFlush = flushBatch(batch, lastFlush, callback);
                }
                flushBatch(batch, callback);

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

    /*
     * Lines go over binder in batches. One transaction per line overruns the 1 MB
     * per-process transaction buffer on anything chatty (logcat produces thousands
     * of lines a second) and the callback dies with DeadObjectException.
     */
    private static long flushBatch(List<String> batch, long lastFlush, IShellCallback callback) {
        long now = System.currentTimeMillis();
        if (batch.size() < BATCH_SIZE && now - lastFlush < BATCH_INTERVAL_MS) {
            return lastFlush;
        }
        flushBatch(batch, callback);
        return now;
    }

    /*
     * Delivery is best effort and never throws at the caller. A reader that stops
     * on a failed callback stops emptying its pipe, and the process then blocks on
     * a full buffer and never exits -- the very hang this batching sits on top of.
     * Dropping a batch loses output; failing to drain loses the whole shell.
     */
    private static void flushBatch(List<String> batch, IShellCallback callback) {
        if (batch.isEmpty()) return;
        try {
            callback.onLines(new ArrayList<>(batch));
        } catch (Exception ignored) {
        }
        batch.clear();
    }

    private static String getLogcatLines(String outputLine) {
        if (outputLine == null) return "";

        String safe = Utils.escapeHtml(outputLine);

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