package in.sunilpaulmathew.ashell.serializable;

import static android.view.View.VISIBLE;

import android.content.Context;
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
    private final char typeChar;
    private final long size;
    private final String name, owner, group, parentPath;
    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".bmp", ".jpg", ".jpeg", ".png", ".webp", ".gif", ".heic"
    ));

    public FilesEntry(String name, String owner, String group, String parentPath, long size, char typeChar, boolean empty) {
        this.name = name;
        this.owner = owner;
        this.group = group;
        this.parentPath = parentPath;
        this.size = size;
        this.typeChar = typeChar;
        this.empty = empty;
    }

    public boolean isBlockDevice() {
        return typeChar == 'b';
    }

    public boolean isCharacterDevice() {
        return typeChar == 'c';
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isFile() {
        return typeChar == '-';
    }

    private static boolean isImageFile(String name) {
        String lowerName = name.toLowerCase(Locale.ROOT);
        return IMAGE_EXTENSIONS.stream().anyMatch(lowerName::endsWith);
    }

    public boolean isNamedPipe() {
        return typeChar == 'p';
    }

    public boolean isSocket() {
        return typeChar == 's';
    }

    public boolean isSymbolicLink() {
        return typeChar == 'l';
    }

    public String decodeSymLink() {
        return name.trim().split(" -> ")[1];
    }

    public String getName() {
        return name;
    }

    public String getParentPath() {
        return parentPath;
    }

    private static Drawable getIcon(char typeChar, String name, Context context) {
        switch (typeChar) {
            case 'l':
                return Utils.getDrawable(R.drawable.ic_symlink, context);
            // fileType = "Symbolic Link";
            case 'c':
                return Utils.getDrawable(R.drawable.ic_keyboard, context);
                // fileType = "Character Device";
            case 'b':
                return Utils.getDrawable(R.drawable.ic_disk, context);
                // fileType = "Block Device";
            case 's':
                return Utils.getDrawable(R.drawable.ic_lan, context);
                // fileType = "Socket";
            case 'p':
                // fileType = "Named Pipe";
                return Utils.getDrawable(R.drawable.ic_timeline, context);
            case '-':
                // fileType = "File";
            default:
                // fileType = "Unknown";
                if (isImageFile(name)) {
                    return Utils.getDrawable(R.drawable.ic_image, context);
                } else if (name.endsWith(".apk")) {
                    return Utils.getDrawable(R.drawable.ic_apks, context);
                } else {
                    return Utils.getDrawable(R.drawable.ic_file, context);
                }
        }
    }

    private static String getSize(long size) {
        if (size < 1024) return size + " B";
        final String[] units = {
                "KB", "MB", "GB", "TB", "PB", "EB"
        };
        int index = -1;
        double sizeInDouble = size;
        while (sizeInDouble >= 1024 && index < units.length - 1) {
            sizeInDouble /= 1024;
            index++;
        }
        return String.format(Locale.getDefault(),"%.2f %s", sizeInDouble, units[index]);
    }

    public void loadFileInfo(ImageView icon, MaterialTextView textView, MaterialTextView sizeText) {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                Drawable drawable = getIcon(typeChar, name, icon.getContext());
                handler.post(() -> {
                    textView.setText(name);
                    icon.setImageDrawable(drawable);
                    textView.setTypeface(textView.getTypeface(), Typeface.BOLD_ITALIC);
                    sizeText.setVisibility(VISIBLE);
                    sizeText.setText(getSize(size));
                });
            });
        }
    }

}