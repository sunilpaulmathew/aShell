package in.sunilpaulmathew.ashell.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.sunilpaulmathew.ashell.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 17, 2026
 */
public class Bookmarks {

    public static boolean isBookmarked(String command, Context context) {
        if (command == null || command.trim().isEmpty()) return false;

        File file = getBookmarkFile(context);
        if (!file.exists()) return false;

        try {
            JSONObject root = new JSONObject(Objects.requireNonNull(Utils.read(file)));
            JSONArray array = root.optJSONArray("bookmarks");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    if (item != null && command.equals(item.optString("command"))) {
                        return true;
                    }
                }
            }
        } catch (JSONException ignored) {
        }
        return false;
    }

    private static File getBookmarkFile(Context context) {
        return new File(context.getExternalFilesDir("bookmarks"), "bookmarks.json");
    }

    private static JSONObject createRootObject() throws JSONException {
        JSONObject root = new JSONObject();
        root.put("content", "bookMarkedShellCommands");
        root.put("bookmarks", new JSONArray());
        return root;
    }

    public static List<String> getBookmarks(Context context) {
        List<String> list = new ArrayList<>();
        File file = getBookmarkFile(context);
        if (!file.exists()) return list;

        try {
            JSONObject root = new JSONObject(Objects.requireNonNull(Utils.read(file)));
            JSONArray array = root.optJSONArray("bookmarks");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    if (item != null && item.has("command")) {
                        list.add(item.getString("command"));
                    }
                }
            }
        } catch (JSONException e) {
            return list;
        }
        return list;
    }

    public static void addToBookmark(String command, Context context) {
        if (command == null || command.trim().isEmpty()) return;

        File file = getBookmarkFile(context);
        try {
            JSONObject root = file.exists() ? new JSONObject(Objects.requireNonNull(Utils.read(file))) : createRootObject();
            JSONArray array = root.optJSONArray("bookmarks");
            if (array == null) array = new JSONArray();

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.optJSONObject(i);
                if (item != null && command.equals(item.optString("command"))) {
                    return;
                }
            }

            JSONObject entry = new JSONObject();
            entry.put("command", command);
            array.put(entry);

            root.put("bookmarks", array);
            Utils.create(root.toString(2), file);
        } catch (JSONException ignored) {
        }
    }

    public static void deleteBookMarks(Context context) {
        getBookmarkFile(context).delete();
    }

    public static void deleteFromBookmark(String command, Context context) {
        File file = getBookmarkFile(context);
        if (!file.exists()) return;

        try {
            JSONObject root = new JSONObject(Objects.requireNonNull(Utils.read(file)));
            JSONArray oldArray = root.optJSONArray("bookmarks");
            if (oldArray == null) return;

            JSONArray newArray = new JSONArray();
            for (int i = 0; i < oldArray.length(); i++) {
                JSONObject item = oldArray.optJSONObject(i);
                if (item != null && !command.equals(item.optString("command"))) {
                    newArray.put(item);
                }
            }
            root.put("bookmarks", newArray);
            Utils.create(root.toString(2), file);
        } catch (JSONException ignored) {
        }
    }

    public static void exportBackup(Context context) {
        File dbFile = getBookmarkFile(context);
        String timestamp = new SimpleDateFormat("yyMMdd-HHmm", Locale.getDefault())
                .format(new Date());
        File exportFile = new File(Utils.getExportPath(context), "aShellBookmarks_" + timestamp + ".txt");
        if (dbFile.exists()) {
            Utils.create(Utils.read(dbFile), exportFile);
            Utils.toast(context.getString(R.string.copying_succeed_toast, context.getString(R.string.bookmarks)), context).show();
        }
    }

    public static void migrateBookmarks(Context context) {
        new Thread(() -> {
            File jsonFile = getBookmarkFile(context);
            if (!jsonFile.exists()) {
                try {
                    JSONObject root = createRootObject();
                    JSONArray array = new JSONArray();

                    for (File file : Objects.requireNonNull(Objects.requireNonNull(context.getExternalFilesDir("bookmarks")).listFiles())) {
                        if (file.getName().equalsIgnoreCase("specialCommands")) {
                            for (String commands : Objects.requireNonNull(Utils.read(new File(context.getExternalFilesDir("bookmarks"), "specialCommands"))).split("\\r?\\n")) {
                                if (!commands.trim().isEmpty()) {
                                    JSONObject entry = new JSONObject();
                                    entry.put("command", commands);
                                    array.put(entry);
                                }
                            }
                        } else {
                            JSONObject entry = new JSONObject();
                            entry.put("command", file.getName());
                            array.put(entry);
                        }
                        file.delete();
                    }

                    root.put("bookmarks", array);
                    Utils.create(root.toString(2), jsonFile);
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

}