package in.sunilpaulmathew.ashell.utils;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.color.DynamicColors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.ashell.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 28, 2022
 */
public class Utils {

    public static boolean isBookmarked(String command, Context context) {
        if (isValidFilename(command)) {
            return new File(context.getExternalFilesDir("bookmarks"), command).exists();
        } else {
            if (new File(context.getExternalFilesDir("bookmarks"), "specialCommands").exists()) {
                for (String commands : Objects.requireNonNull(read(new File(context.getExternalFilesDir("bookmarks"), "specialCommands"))).split("\\r?\\n")) {
                    if (commands.trim().equals(command)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean deleteFromBookmark(String command, Context context) {
        if (isValidFilename(command)) {
            return new File(context.getExternalFilesDir("bookmarks"), command).delete();
        } else {
            StringBuilder sb = new StringBuilder();
            for (String commands : Objects.requireNonNull(read(new File(context.getExternalFilesDir("bookmarks"), "specialCommands"))).split("\\r?\\n")) {
                if (!commands.equals(command)) {
                    sb.append(commands).append("\n");
                }
            }
            create(sb.toString(), new File(context.getExternalFilesDir("bookmarks"), "specialCommands"));
            return true;
        }
    }

    public static boolean getBoolean(String name, boolean defaults, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(name, defaults);
    }

    public static boolean isAmoledBlackEnabled(Context context) {
        return isDarkTheme(context) && Utils.getBoolean("amoledTheme", false, context);
    }

    public static boolean isDarkTheme(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    /*
     * Adapted from android.os.FileUtils
     * Ref: https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/os/FileUtils.java;l=972?q=isValidFatFilenameChar
     */
    private static boolean isValidFilename(String s) {
        return !s.contains("*") && !s.contains("/") && !s.contains(":")
                && !s.contains("<") && !s.contains(">") && !s.contains("?")
                && !s.contains("\\") && !s.contains("|");
    }

    public static Drawable getDrawable(int drawable, Context context) {
        return ContextCompat.getDrawable(context, drawable);
    }

    public static int getColor(int color, Context context) {
        return ContextCompat.getColor(context, color);
    }

    public static int getColorAccent(Context context) {
        return getMaterial3Colors(getColor(R.color.colorBlue, context), context);
    }

    private static int getMaterial3Colors(int defaultColor, Context context) {
        int color = defaultColor;
        if (DynamicColors.isDynamicColorAvailable()) {
            Context dynamicClrCtx = DynamicColors.wrapContextIfAvailable(context, com.google.android.material.R.style.MaterialAlertDialog_Material3);
            TypedArray ta = dynamicClrCtx.obtainStyledAttributes(new int[] {
                    com.google.android.material.R.attr.colorPrimary
            });
            color = ta.getColor(0, defaultColor);
            ta.recycle();
        }
        return color;
    }

    public static List<String> getBookmarks(Context context) {
        List<String> mBookmarks = new ArrayList<>();
        for (File file : Objects.requireNonNull(Objects.requireNonNull(context.getExternalFilesDir("bookmarks")).listFiles())) {
            if (!file.getName().equalsIgnoreCase("specialCommands")) {
                mBookmarks.add(file.getName());
            }
        }
        if (new File(context.getExternalFilesDir("bookmarks"), "specialCommands").exists()) {
            for (String commands : Objects.requireNonNull(read(new File(context.getExternalFilesDir("bookmarks"), "specialCommands"))).split("\\r?\\n")) {
                if (!commands.trim().isEmpty()) {
                    mBookmarks.add(commands.trim());
                }
            }
        }
        return mBookmarks;
    }

    public static String getDeviceName() {
        return Build.MODEL;
    }

    private static String read(File file) {
        try (BufferedReader buf = new BufferedReader(new FileReader(file))) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = buf.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString().trim();
        } catch (IOException ignored) {
        }
        return null;
    }

    public static Toast toast(String message, Context context) {
        return Toast.makeText(context, message, Toast.LENGTH_LONG);
    }

    public static void addToBookmark(String command, Context context) {
        if (isValidFilename(command)) {
            create(command, new File(context.getExternalFilesDir("bookmarks"), command));
        } else {
            StringBuilder sb = new StringBuilder();
            if (new File(context.getExternalFilesDir("bookmarks"), "specialCommands").exists()) {
                for (String commands : Objects.requireNonNull(read(new File(context.getExternalFilesDir("bookmarks"), "specialCommands"))).split("\\r?\\n")) {
                    sb.append(commands).append("\n");
                }
                sb.append(command).append("\n");
            } else {
                sb.append(command).append("\n");
            }
            create(sb.toString(), new File(context.getExternalFilesDir("bookmarks"), "specialCommands"));
        }
    }

    public static void copyToClipboard(String text, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied to clipboard", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public static void create(String text, File path) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(text);
            writer.close();
        } catch (IOException ignored) {
        }
    }

    public static void loadShizukuWeb(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://shizuku.rikka.app/"));
            context.startActivity(intent);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public static void saveBoolean(String name, boolean value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(name, value).apply();
    }

}