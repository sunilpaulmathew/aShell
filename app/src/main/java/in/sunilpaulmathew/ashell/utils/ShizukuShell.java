package in.sunilpaulmathew.ashell.utils;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.List;

import in.sunilpaulmathew.ashell.BuildConfig;
import in.sunilpaulmathew.ashell.services.ShellService;
import rikka.shizuku.Shizuku;
import sunilpaulmathew.ashell.IShellCallback;
import sunilpaulmathew.ashell.IShellService;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 12, 2022
 */
public class ShizukuShell {

    public enum ShellStatus {
        IDLE,
        RUNNING
    }

    private static IShellService mShellService;
    private static List<String> mOutput;
    private static String mCommand;
    private volatile ShellStatus mCurrentStatus = ShellStatus.IDLE;
    private StatusListener mStatusListener;

    public interface StatusListener {
        void onStatusChanged(ShellStatus status);
    }

    public ShizukuShell(List<String> output, String command) {
        mOutput = output;
        mCommand = command;
    }

    public void ensureUserService() {
        if (mShellService != null) return;

        Shizuku.UserServiceArgs args = new Shizuku.UserServiceArgs(
                new ComponentName(BuildConfig.APPLICATION_ID, ShellService.class.getName()))
                .daemon(false)
                .processNameSuffix("shizuku_shell")
                .debuggable(BuildConfig.DEBUG)
                .version(BuildConfig.VERSION_CODE);

        Shizuku.bindUserService(args, mServiceConnection);
    }

    public boolean isBusy() {
        return mShellService != null && mCurrentStatus == ShellStatus.RUNNING;
    }

    public void setStatusListener(StatusListener listener) {
        this.mStatusListener = listener;
        if (listener != null) {
            listener.onStatusChanged(mCurrentStatus); // Notify immediately of current status
        }
    }

    private void updateStatus(ShellStatus status) {
        mCurrentStatus = status;
        if (mStatusListener != null) {
            mStatusListener.onStatusChanged(status);
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder == null || !iBinder.pingBinder()) return;

            mShellService = IShellService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mShellService = null;
        }
    };

    public void destroy() {
        try {
            if (mShellService != null) mShellService.destroyProcess();
        } catch (RemoteException ignored) {
        }
        updateStatus(ShellStatus.IDLE);
    }

    public void exec() {
        if (mShellService == null) {
            ensureUserService();
            return;
        }

        updateStatus(ShellStatus.RUNNING);

        try {
            mShellService.runCommand(mCommand, new IShellCallback.Stub() {
                @Override
                public void onLine(String line) {
                    mOutput.add(line);
                }

                @Override
                public void onFinished(int exitCode) {
                    updateStatus(ShellStatus.IDLE);
                }
            });
        } catch (RemoteException ignored) {
            updateStatus(ShellStatus.IDLE);
        }
    }

}