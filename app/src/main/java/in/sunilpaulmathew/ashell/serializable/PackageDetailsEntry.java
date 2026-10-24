package in.sunilpaulmathew.ashell.serializable;

import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class PackageDetailsEntry implements Serializable {

    private final List<File> splits;
    private final String title, text;

    public PackageDetailsEntry(String title, String text) {
        this.title = title;
        this.text = text;
        splits = null;
    }

    public PackageDetailsEntry(String title, String text, List<File> splits) {
        this.title = title;
        this.text = text;
        this.splits = splits;
    }

    public String getTitle() {
        return title;
    }

    public List<File> getSplits() {
        return this.splits;
    }

    public void load(TextView title, TextView text) {
        if (title != null) {
            title.setText(this.title);
        }
        if (text != null) {
            String updatedTxt =  " : " + this.text;
            text.setText(updatedTxt);
        }
    }

}