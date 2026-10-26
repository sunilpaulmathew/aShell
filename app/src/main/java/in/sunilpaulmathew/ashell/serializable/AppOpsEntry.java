package in.sunilpaulmathew.ashell.serializable;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.sunilpaulmathew.ashell.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 25, 2026
 */
public class AppOpsEntry implements Serializable {

    private final String name;
    private String status;

    public AppOpsEntry(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return this.status;
    }

    public void load(TextView nameTxt, TextView statusTxt, TextView descriptionTxt) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Handler handler = new Handler(Looper.getMainLooper());
            StringBuilder descBuilder = new StringBuilder(), statusBuilder = new StringBuilder();
            executor.execute(() -> {

                if (this.status.contains("; ")) {
                    for (String parts : this.status.split("; ")) {
                        if (parts.trim().startsWith("time=")) {
                            descBuilder.append(descriptionTxt.getContext().getString(R.string.app_ops_status_allowed, formatAppOpsTime(parts.trim(), true, descriptionTxt.getContext()))).append("\n");
                        } else if (parts.trim().startsWith("rejectTime=")) {
                            descBuilder.append(descriptionTxt.getContext().getString(R.string.app_ops_status_rejected, formatAppOpsTime(parts.trim(), true, descriptionTxt.getContext()))).append("\n");
                        } else if (parts.trim().startsWith("duration=")) {
                            descBuilder.append(descriptionTxt.getContext().getString(R.string.app_ops_duration, formatAppOpsTime(parts.trim(), false, descriptionTxt.getContext()))).append("\n");
                        } else {
                            statusBuilder.append(parts.trim()).append("\n");
                        }
                    }
                } else {
                    statusBuilder.append(this.status).append("\n");
                }

                handler.post(() -> {
                    nameTxt.setText(this.name);
                    if (statusBuilder.toString().trim().isEmpty()) {
                        statusTxt.setVisibility(GONE);
                    } else {
                        statusTxt.setText(statusTxt.getContext().getString(R.string.app_ops_status_current, getStatusDescription(statusBuilder.toString().trim(), statusTxt.getContext())));
                        statusTxt.setVisibility(VISIBLE);
                    }
                    if (descBuilder.toString().trim().isEmpty()) {
                        descriptionTxt.setVisibility(GONE);
                    } else {
                        descriptionTxt.setText(descriptionTxt.getContext().getString(R.string.app_ops_status_recent, descBuilder.toString().trim()));
                        descriptionTxt.setVisibility(VISIBLE);
                    }
                });
            });
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private static long parseComponent(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Long.parseLong(Objects.requireNonNull(matcher.group(1)));
        }
        return 0;
    }

    private static String formatAppOpsTime(String rawInput, boolean isTimestamp, Context context) {
        if (rawInput == null || rawInput.trim().isEmpty()) {
            return "Unknown";
        }

        long durationMillis = 0;

        durationMillis += parseComponent(rawInput, "(\\d+)d") * 24 * 60 * 60 * 1000;
        durationMillis += parseComponent(rawInput, "(\\d+)h") * 60 * 60 * 1000;
        durationMillis += parseComponent(rawInput, "(\\d+)m(?!s)") * 60 * 1000;
        durationMillis += parseComponent(rawInput, "(\\d+)s") * 1000;
        durationMillis += parseComponent(rawInput, "(\\d+)ms");

        if (durationMillis == 0) {
            return isTimestamp ? context.getString(R.string.never) : "0ms";
        }

        if (isTimestamp) {
            long eventTimestamp = System.currentTimeMillis() - durationMillis;
            return DateUtils.getRelativeTimeSpanString(
                    eventTimestamp,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString();
        }

        if (durationMillis < 60000) {
            double totalSeconds = durationMillis / 1000.0;
            return String.format(Locale.getDefault(), "%.1fs", totalSeconds);
        } else {
            return DateUtils.formatElapsedTime(durationMillis / 1000);
        }
    }

    private static String getStatusDescription(String rawStatus, Context context) {
        switch (rawStatus) {
            case "allow":
                return context.getString(R.string.app_ops_allow_title);
            case "deny":
                return context.getString(R.string.app_ops_deny_title);
            case "ignore":
                return context.getString(R.string.app_ops_ignore_title);
            case "foreground":
                return context.getString(R.string.app_ops_foreground_title);
            default:
                return context.getString(R.string.app_ops_default_title);
        }
    }

}