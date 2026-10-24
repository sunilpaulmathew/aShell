package in.sunilpaulmathew.ashell.serializable;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class BatteryEntry implements Serializable {

    private final String titleText, descriptionText;
    private final int drawable;

    public BatteryEntry(String titleText, String descriptionText, int drawable) {
        this.titleText = titleText;
        this.descriptionText = descriptionText;
        this.drawable = drawable;
    }

    public int getDrawable() {
        return this.drawable;
    }

    public String getDescription() {
        return this.descriptionText;
    }

    public String getTile() {
        return this.titleText;
    }

}