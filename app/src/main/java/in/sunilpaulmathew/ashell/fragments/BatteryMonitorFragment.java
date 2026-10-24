package in.sunilpaulmathew.ashell.fragments;

import static android.view.View.GONE;

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
import in.sunilpaulmathew.ashell.adapters.BatteryMonitorAdapter;
import in.sunilpaulmathew.ashell.serializable.BatteryEntry;
import in.sunilpaulmathew.ashell.serializable.BatteryInfoEntry;
import in.sunilpaulmathew.ashell.utils.ShizukuShell;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class BatteryMonitorFragment extends Fragment {

    private CircularProgressIndicator progressBar;
    private final BatteryMonitorAdapter adapter = new BatteryMonitorAdapter(new CopyOnWriteArrayList<>());
    private MaterialTextView progressTxt;
    private RecyclerView recyclerView;
    private ScheduledExecutorService scheduler;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_live_monitor, container, false);

        AppCompatImageButton icon = rootView.findViewById(R.id.icon);
        progressBar = rootView.findViewById(R.id.progress);
        MaterialTextView load = rootView.findViewById(R.id.load);
        progressTxt = rootView.findViewById(R.id.progress_text);
        MaterialTextView title = rootView.findViewById(R.id.title);
        recyclerView = rootView.findViewById(R.id.recycler_view);

        icon.setImageResource(R.drawable.ic_charging_high);
        title.setText(R.string.battery_monitor);
        load.setVisibility(GONE);

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

    private static BatteryInfoEntry parse(String dumpsysOutput) {
        if (dumpsysOutput == null) return null;

        int level = extractInt(dumpsysOutput, "level:\\s*(-?\\d+)");
        int status = extractInt(dumpsysOutput, "status:\\s*(-?\\d+)");
        int health = extractInt(dumpsysOutput, "health:\\s*(-?\\d+)");
        int temp = extractInt(dumpsysOutput, "temperature:\\s*(-?\\d+)");
        int voltage = extractInt(dumpsysOutput, "voltage:\\s*(-?\\d+)");

        int chargeCounter = extractInt(dumpsysOutput, "Charge counter:\\s*(-?\\d+)");
        int maxCapacity = extractInt(dumpsysOutput, "Maximum capacity:\\s*(-?\\d+)");
        int designCapacity = extractInt(dumpsysOutput, "Design capacity:\\s*(-?\\d+)");

        boolean acPowered = dumpsysOutput.contains("AC powered: true");
        boolean usbPowered = dumpsysOutput.contains("USB powered: true");
        boolean wirelessPowered = dumpsysOutput.contains("Wireless powered: true");
        boolean dockPowered = dumpsysOutput.contains("Dock powered: true");

        Pattern techPattern = Pattern.compile("technology:\\s*(.+)");
        Matcher matcher = techPattern.matcher(dumpsysOutput);
        String tech = matcher.find() ? Objects.requireNonNull(matcher.group(1)).trim() : "Unknown";

        return new BatteryInfoEntry(
                level, status, health, temp, voltage,
                chargeCounter, maxCapacity, designCapacity,
                acPowered, usbPowered, wirelessPowered, dockPowered,
                tech
        );
    }

    private static int extractInt(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                return Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return 0;
    }

    private int getProgressColor(int level) {
        int colorRes;
        if (level > 80) {
            colorRes = android.R.color.holo_green_dark;
        } else if (level > 50) {
            colorRes = android.R.color.holo_orange_dark;
        } else {
            colorRes = android.R.color.holo_red_dark;
        }
        return ContextCompat.getColor(requireActivity(), colorRes);
    }

    private int getSpanSize() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 3;
        } else {
            return 2;
        }
    }

    private static List<BatteryEntry> getData(BatteryInfoEntry entry) {
        List<BatteryEntry> date = new ArrayList<>();
        if (entry.getLevel() >= 0) {
            date.add(new BatteryEntry(entry.getStatusString(), entry.getLevel() + "%", Objects.equals(entry.getStatusString(), entry.getChargingString()) ? R.drawable.ic_charging : R.drawable.ic_cellphone_down));
        }
        if (entry.getVoltageV() >= 0) {
            date.add(new BatteryEntry("Voltage", entry.getVoltageV() + " v", R.drawable.ic_voltage));
        }
        if (entry.getTemperatureC() >= 0) {
            date.add(new BatteryEntry("Temperature", entry.getTemperatureC() + "°C", R.drawable.ic_temperature));
        }
        if (entry.getEstimatedMaximumCapacityMah() >= 0) {
            date.add(new BatteryEntry("Capacity" + (entry.getBatteryHealthPercentage() > 0 ? " (" + (int) entry.getBatteryHealthPercentage() + "%)" : ""), entry.getEstimatedMaximumCapacityMah() + " mAh", R.drawable.ic_charging_high));
        }
        if (entry.getHealthString() != null) {
            date.add(new BatteryEntry("Health", entry.getHealthString(), R.drawable.ic_heart));
        }
        if (entry.getTechnology() != null) {
            date.add(new BatteryEntry("Technology", entry.getTechnology(), R.drawable.ic_technology));
        }
        return date;
    }

    private String fetchStats() {
        return ShizukuShell.runCommand("dumpsys battery");
    }

    private void startPolling() {
        if (scheduler != null && !scheduler.isShutdown()) return;

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(() -> {
            String stats = fetchStats();
            BatteryInfoEntry batteryInfo = parse(stats);
            int level = batteryInfo.getLevel();
            int progressColor = getProgressColor(level);
            String levelAsPercentage = level + "%";
            List<BatteryEntry> data = getData(batteryInfo);
            mainHandler.post(() -> {
                if (progressBar != null) {
                    progressBar.setProgress(level, true);
                    progressBar.setIndicatorColor(progressColor);
                }
                if (progressTxt != null) {
                    progressTxt.setText(levelAsPercentage);
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