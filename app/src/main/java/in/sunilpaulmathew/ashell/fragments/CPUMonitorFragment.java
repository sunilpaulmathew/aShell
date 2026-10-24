package in.sunilpaulmathew.ashell.fragments;

import static android.view.View.VISIBLE;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.CPUMonitorAdapter;
import in.sunilpaulmathew.ashell.serializable.CPUEntry;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class CPUMonitorFragment extends Fragment {

    private CircularProgressIndicator progressBar;
    private final CPUMonitorAdapter adapter = new CPUMonitorAdapter(new CopyOnWriteArrayList<>());
    private MaterialTextView load, progressTxt;
    private RecyclerView recyclerView;
    private ScheduledExecutorService scheduler;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_live_monitor, container, false);

        AppCompatImageButton icon = rootView.findViewById(R.id.icon);
        progressBar = rootView.findViewById(R.id.progress);
        load = rootView.findViewById(R.id.load);
        progressTxt = rootView.findViewById(R.id.progress_text);
        MaterialTextView title = rootView.findViewById(R.id.title);
        recyclerView = rootView.findViewById(R.id.recycler_view);

        icon.setImageResource(R.drawable.ic_charging_high);
        title.setText(R.string.cpu_monitor);
        load.setVisibility(VISIBLE);

        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), getSpanSize()));

        recyclerView.setAdapter(adapter);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment parent = getParentFragment();
                if (parent instanceof ExtrasFragment) {
                    ((ExtrasFragment) parent).showExtrasGrid();
                }
            }
        });

        return rootView;
    }

    private static List<CPUEntry> parse(String dumpsysOutput) {
        List<CPUEntry> processList = new ArrayList<>();
        if (dumpsysOutput == null || dumpsysOutput.isEmpty()) return processList;

        String regex = "\\s*(\\d+)%\\s+(\\d+)/([^:]+):\\s*([\\d.]+)%\\s+user\\s+\\+\\s*([\\d.]+)%\\s+kernel";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dumpsysOutput);

        while (matcher.find()) {
            try {
                float total = Float.parseFloat(Objects.requireNonNull(matcher.group(1)));
                int pid = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
                String pkgName = Objects.requireNonNull(matcher.group(3)).trim();
                float user = Float.parseFloat(Objects.requireNonNull(matcher.group(4)));
                float kernel = Float.parseFloat(Objects.requireNonNull(matcher.group(5)));

                processList.add(new CPUEntry(pkgName, pid, total, user, kernel));
            } catch (NumberFormatException ignored) {
            }
        }

        return processList;
    }

    private int getProgressColor(int level) {
        int colorRes;
        if (level > 80) {
            colorRes = android.R.color.holo_red_dark;
        } else if (level > 50) {
            colorRes = android.R.color.holo_orange_dark;
        } else {
            colorRes = android.R.color.holo_green_dark;
        }
        return ContextCompat.getColor(requireActivity(), colorRes);
    }

    private int getSpanSize() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 2;
        } else {
            return 1;
        }
    }

    private String fetchStats() {
        return ShizukuShell.runCommand("dumpsys cpuinfo");
    }

    private static String parseLoadAverage(String dumpsysOutput) {
        if (dumpsysOutput == null) return "N/A";

        Pattern pattern = Pattern.compile("Load:\\s*([\\d.]+)\\s*/\\s*([\\d.]+)\\s*/\\s*([\\d.]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(dumpsysOutput);

        if (matcher.find()) {
            return String.format("1m: %s | 5m: %s | 15m: %s", matcher.group(1), matcher.group(2), matcher.group(3));
        }
        return "N/A";
    }

    private float parseTotalCpuUsage(String dumpsysOutput) {
        if (dumpsysOutput == null || dumpsysOutput.isEmpty()) {
            return 0.0f;
        }

        Pattern pattern = Pattern.compile("with\\s+(\\d+(?:\\.\\d+)?)%\\s+to\\s+active", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(dumpsysOutput);

        if (matcher.find()) {
            try {
                return Float.parseFloat(Objects.requireNonNull(matcher.group(1)));
            } catch (NumberFormatException ignored) {
            }
        }

        Pattern fallbackPattern = Pattern.compile("TOTAL:\\s+(\\d+(?:\\.\\d+)?)%", Pattern.CASE_INSENSITIVE);
        Matcher fallbackMatcher = fallbackPattern.matcher(dumpsysOutput);
        if (fallbackMatcher.find()) {
            try {
                return Float.parseFloat(Objects.requireNonNull(fallbackMatcher.group(1)));
            } catch (NumberFormatException ignored) {
            }
        }

        return 0.0f;
    }

    private void startPolling() {
        if (scheduler != null && !scheduler.isShutdown()) return;

        if (scheduler != null && !scheduler.isShutdown()) return;

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(() -> {
            String stats = fetchStats();
            String cpuLoad = parseLoadAverage(stats);
            List<CPUEntry> data = parse(stats);
            float cpuUsage = parseTotalCpuUsage(stats);
            int progressColor = getProgressColor((int)cpuUsage);
            String cpuUsageText = cpuUsage + "%";
            mainHandler.post(() -> {
                load.setText(getString(R.string.cpu_monitor_load, cpuLoad));
                if (progressBar != null) {
                    progressBar.setProgress((int) cpuUsage, false);
                    progressBar.setIndicatorColor(progressColor);
                }
                if (progressTxt != null) {
                    progressTxt.setText(cpuUsageText);
                    progressTxt.setTextColor(progressColor);
                }
                if (recyclerView != null) {
                    adapter.updateData(data);
                }
            });
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void stopPolling() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startPolling();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPolling();
    }

}