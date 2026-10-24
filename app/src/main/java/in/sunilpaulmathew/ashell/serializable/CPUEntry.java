package in.sunilpaulmathew.ashell.serializable;

import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class CPUEntry implements Serializable {

    private final float kernelPercent, totalCpuPercent, userPercent;
    private final int pid;
    private final String packageName;

    public CPUEntry(String packageName, int pid, float totalCpuPercent, float userPercent, float kernelPercent) {
        this.packageName = packageName;
        this.pid = pid;
        this.totalCpuPercent = totalCpuPercent;
        this.userPercent = userPercent;
        this.kernelPercent = kernelPercent;
    }

    public void load(ProgressBar progressBar, ImageButton icon, TextView textPackageName, TextView textCpuPercent, TextView textPidDetails, int number) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                String cpuPercentage = (int) this.totalCpuPercent + "%";
                handler.post(() -> {
                    textPackageName.setText(this.packageName);
                    textCpuPercent.setText(cpuPercentage);
                    textPidDetails.setText(String.format(Locale.getDefault(), "PID: %d | User: %.1f%% | Kernel: %.1f%%",
                            this.pid, this.userPercent, this.kernelPercent));
                    icon.setImageBitmap(Settings.generateAlphabetIcon(String.valueOf(number), 24, icon.getContext()));
                    progressBar.setProgress((int) this.totalCpuPercent);
                });
            });
        }
    }

}