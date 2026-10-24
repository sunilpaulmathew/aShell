package in.sunilpaulmathew.ashell.serializable;

import com.google.android.material.button.MaterialButton;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class PackageHeaderEntry implements Serializable {

    private final int iconRes;
    private final String titleRes;

    public PackageHeaderEntry(String titleRes, int iconRes) {
        this.titleRes = titleRes;
        this.iconRes = iconRes;
    }

    public void load(MaterialButton button) {
        if (titleRes != null) {
            button.setText(titleRes);
        }
        if (iconRes != Integer.MIN_VALUE) {
            button.setIconResource(iconRes);
        }
    }

}