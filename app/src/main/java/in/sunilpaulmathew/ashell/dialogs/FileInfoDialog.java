package in.sunilpaulmathew.ashell.dialogs;

import android.content.Context;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.adapters.FileInfoAdapter;
import in.sunilpaulmathew.ashell.serializable.FilesEntry;
import in.sunilpaulmathew.ashell.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 24, 2026
 */
public class FileInfoDialog extends BottomSheetDialog {

    public FileInfoDialog(FilesEntry filesEntry, Context context) {
        super(context);

        View root = View.inflate(context, R.layout.layout_file_info, null);
        AppCompatImageView image = root.findViewById(R.id.image);
        MaterialTextView title = root.findViewById(R.id.title);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);

        image.setImageDrawable(Utils.getDrawable(filesEntry.isDirectory() ? R.drawable.ic_folder : filesEntry.getIcon(), context));
        title.setText(filesEntry.getName());

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(new FileInfoAdapter(getData(filesEntry, context)));

        setContentView(root);
        show();
    }

    private List<String> getData(FilesEntry filesEntry, Context context) {
        List<String> data = new CopyOnWriteArrayList<>();
        data.add(context.getString(R.string.file_type, filesEntry.isDirectory() ? context.getString(R.string.file_type_directory) : filesEntry.getExtension() == null ? context.getString(R.string.file_type_unknown) : getFileExtDescription(filesEntry.getExtension(), context)));
        data.add(context.getString(R.string.file_path, filesEntry.getAbsolutePath()));
        data.add(context.getString(R.string.file_modified, filesEntry.lastModified()));
        if (!filesEntry.isDirectory()) {
            data.add(context.getString(R.string.file_size, filesEntry.getSize()));
        }
        data.add(context.getString(R.string.file_permissions, filesEntry.getPermissions()));
        data.add(context.getString(R.string.file_owner, filesEntry.getOwner()));
        data.add(context.getString(R.string.file_group, filesEntry.getGroup()));
        return data;
    }

    private static String getFileExtDescription(String ext, Context context) {
        switch (ext) {
            case "apk":
                return "Android Package Kit, the file format for installing apps on Android devices" + " (." + ext + ")";
            case "abb":
                return "Android App Bundle, the newer format for distributing apps on Google Play, taking over from APK" + " (." + ext + ")";
            case "xapk":
            case "apkm":
            case "apks":
                return "A packaged Android app format that includes an APK and additional data like OBB files, used for installing larger or more complex apps" + " (." + ext + ")";
            case "jpg":
            case "jpeg":
                return "Joint Photographic Experts Group, a common format for digital photos" + " (." + ext + ")";
            case "png":
                return "Portable Network Graphics, a lossless image format good for logos and text" + " (." + ext + ")";
            case "gif":
                return "Graphics Interchange Format, used for animated images and simple animations" + " (." + ext + ")";
            case "bmp":
                return "Bitmap, a simple raster image format, often associated with Windows" + " (." + ext + ")";
            case "webp":
                return "Google's modern image format, offering good compression and quality" + " (." + ext + ")";
            case "svg":
                return "Scalable Vector Graphics, a format for vector images that can be scaled without losing quality" + " (." + ext + ")";
            case "heic":
            case "heif":
                return "High Efficiency Image File Format, a newer, more efficient format for images" + " (." + ext + ")";
            case "mp4":
                return "Moving Picture Experts Group 4, a widely supported format for video" + " (." + ext + ")";
            case "avi":
                return "Audio Video Interleave, a legacy video format" + " (." + ext + ")";
            case "mov":
                return "QuickTime Movie, another video format, often associated with Apple products" + " (." + ext + ")";
            case "mp3":
                return "Moving Picture Experts Group Audio Layer 3, a compressed audio format" + " (." + ext + ")";
            case "wav":
                return "Waveform Audio File Format, an uncompressed audio format, often used for high-quality audio" + " (." + ext + ")";
            case "pdf":
                return "Portable Document Format, a standard format for documents that maintain formatting across different platforms" + " (." + ext + ")";
            case "doc":
            case "docx":
                return "Microsoft Word document formats" + " (." + ext + ")";
            case "jks":
                return "Java KeyStore used to store cryptographic keys and certificates for signing Android apps or securing Java applications" + " (." + ext + ")";
            case "P12":
                return "PKCS #12 keystore, a binary format for storing a private key and its associated certificate chain, commonly used for secure authentication and code signing." + " (." + ext + ")";
            case "txt":
                return "The universal standard for unformatted text. It contains raw letters and numbers with no fonts, bolding, or styling." + " (." + ext + ")";
            case "log":
                return "A plain text file used by apps and operating systems to automatically record events, background activities, errors, or timestamps (like a diary for software)." + " (." + ext + ")";
            case "conf":
            case "cfg":
                return "Short for configuration. These files store settings and parameters for applications, web servers, or systems, often written in plain text keys and values." + " (." + ext + ")";
            case "ini":
                return "A simple configuration file structure popularized by Windows. It organizes settings into clear sections using brackets, like [Settings]." + " (." + ext + ")";
            case "prop":
                return "Highly common in Java and Android (e.g., build.prop). It stores configurations in straight-forward key=value pairs." + " (." + ext + ")";
            case "csv":
                return "A text file that stores tabular data (like a spreadsheet or database table). Each line is a row, and each piece of data is separated by a comma." + " (." + ext + ")";
            case "json":
                return "A lightweight, human-readable format used extensively to send data back and forth between apps and web servers using structured curly braces {} and brackets []." + " (." + ext + ")";
            case "xml":
                return "A data format that uses custom tags (similar to HTML, like <user>...</user>) to structure and describe data, commonly used in enterprise systems and Android layouts." + " (." + ext + ")";
            case "sh":
                return "A text file containing a sequence of commands for Linux, Unix, or macOS terminals to execute automatically." + " (." + ext + ")";
            case "bat":
                return "The Windows equivalent of a shell script. It contains a series of commands executed by the Windows Command Prompt (cmd.exe)." + " (." + ext + ")";
            case "md":
                return "A lightweight plain-text file that uses simple symbols to add formatting (like using  for bold, # for headings, or * for bullet points). It is the universal standard for documentation like README files." + " (." + ext + ")";
            default:
                return context.getString(R.string.file_type_unknown) + " (" + ext + ")";
        }
    }

}