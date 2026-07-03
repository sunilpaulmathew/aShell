package in.sunilpaulmathew.ashell.serializable;

import static android.view.View.VISIBLE;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.google.android.material.textview.MaterialTextView;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 07, 2026
 */
public class FilesEntry implements Serializable {

    private final boolean empty;
    private final long size;
    private final String name, owner, group, lastModified, parentPath, permission;
    private static final Set<String> CAN_WRITE_PATHS = new HashSet<>(Arrays.asList(
            "/tmp", "/storage/emulated/0"
    ));
    private static final Set<String> SHELL_ACCESSIBLE_GROUPS = new HashSet<>(Arrays.asList(
            "shell", "input", "log", "adb", "sdcard_rw", "sdcard_r", "ext_data_rw", "ext_obb_rw",
            "net_bt_admin", "net_bt", "net_bw_stats", "readproc", "uhid", "readtracefs"
    ));
    private static final Set<String> TEXT_EXTENSIONS = new HashSet<>(Arrays.asList(
            "txt", "log", "conf", "cfg", "ini", "csv", "xml", "json", "sh", "bat", "prop", "md"
    ));

    public FilesEntry(String name, String owner, String group, String lastModified, String parentPath, String permission, long size, boolean empty) {
        this.name = name;
        this.owner = owner;
        this.group = group;
        this.lastModified = lastModified;
        this.parentPath = parentPath;
        this.permission = permission;
        this.size = size;
        this.empty = empty;
    }

    public boolean canWrite() {
        return CAN_WRITE_PATHS.stream().anyMatch(getAbsolutePath()::contains);
    }

    public boolean hasExtension() {
        if (name == null) {
            return false;
        }
        int lastIndexOfDot = name.lastIndexOf('.');
        return lastIndexOfDot > 0 && lastIndexOfDot < name.length() - 1;
    }

    public boolean isAccessible() {
        return SHELL_ACCESSIBLE_GROUPS.stream().anyMatch(owner::equalsIgnoreCase) ||
                SHELL_ACCESSIBLE_GROUPS.stream().anyMatch(group::equalsIgnoreCase);
    }

    public boolean isBlockDevice() {
        return getTypeChar() == 'b';
    }

    public boolean isCharacterDevice() {
        return getTypeChar() == 'c';
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isFile() {
        return getTypeChar() == '-';
    }

    public boolean isDirectory() {
        return getTypeChar() == 'd';
    }

    public boolean isTextFile() {
        String lowerName = name.toLowerCase(Locale.ROOT);
        return TEXT_EXTENSIONS.stream().anyMatch(lowerName::endsWith);
    }

    public boolean isNamedPipe() {
        return getTypeChar() == 'p';
    }

    public boolean isSocket() {
        return getTypeChar() == 's';
    }

    public boolean isSymbolicLink() {
        return getTypeChar() == 'l';
    }

    public char getTypeChar() {
        return permission.charAt(0);
    }

    public String decodeSymLink() {
        return name.trim().split(" -> ")[1];
    }

    public String getAbsolutePath() {
        return Objects.equals(parentPath, "/") ? parentPath + name : parentPath + "/" + name;
    }

    public String getGroup() {
        return group;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getParentPath() {
        return parentPath;
    }

    public String getExtension() {
        if (name == null) {
            return null;
        }
        int lastIndexOfDot = name.lastIndexOf('.');
        if (lastIndexOfDot > 0 && lastIndexOfDot < name.length() - 1) {
            return name.substring(lastIndexOfDot + 1).trim();
        }
        return null;
    }

    public String lastModified() {
        return lastModified;
    }

    public String getPermissions() {
        return permission;
    }

    public int getIcon() {
        switch (getTypeChar()) {
            case 'l':
                return R.drawable.ic_symlink;
            // fileType = "Symbolic Link";
            case 'c':
                return R.drawable.ic_keyboard;
            // fileType = "Character Device";
            case 'b':
                return R.drawable.ic_disk;
            // fileType = "Block Device";
            case 's':
                return R.drawable.ic_lan;
            // fileType = "Socket";
            case 'p':
                // fileType = "Named Pipe";
                return R.drawable.ic_timeline;
            case '-':
                // fileType = "File";
            default:
                if (getExtension() == null) return R.drawable.ic_file;
                switch (getExtension()) {
                    case "apk":
                        return R.drawable.ic_apks;
                    case "abb":
                    case "xapk":
                    case "apkm":
                    case "apks":
                        return R.drawable.ic_bundle;
                    case "jpg":
                    case "jpeg":
                    case "png":
                    case "gif":
                    case "bmp":
                    case "webp":
                    case "svg":
                    case "heic":
                    case "heif":
                        return R.drawable.ic_image;
                    case "mp4":
                    case "avi":
                    case "mov":
                        return R.drawable.ic_video;
                    case "mp3":
                    case "wav":
                        return R.drawable.ic_audio;
                    case "pdf":
                        return R.drawable.ic_pdf;
                    case "doc":
                    case "docx":
                        return R.drawable.ic_document;
                    case "jks":
                    case "P12":
                        return R.drawable.ic_security;
                    default:
                        return R.drawable.ic_file;
                }
        }
    }

    public String getSize() {
        return Utils.longToFormattedSize(size);
    }

    public void loadFileInfo(ImageView icon, MaterialTextView textView, MaterialTextView sizeText) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                Drawable drawable = Utils.getDrawable(getIcon(), icon.getContext());
                handler.post(() -> {
                    textView.setText(name);
                    icon.setImageDrawable(drawable);
                    textView.setTypeface(textView.getTypeface(), Typeface.BOLD_ITALIC);
                    sizeText.setVisibility(VISIBLE);
                    sizeText.setText(getSize());
                });
            });
        }
    }

}