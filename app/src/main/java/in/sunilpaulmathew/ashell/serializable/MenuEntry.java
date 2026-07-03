package in.sunilpaulmathew.ashell.serializable;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 24, 2026
 */
public class MenuEntry implements Serializable {

    private final String titleText, descriptionText;
    private final int id, drawable;

    public MenuEntry(String titleText, int drawable, int id) {
        this.titleText = titleText;
        this.descriptionText = null;
        this.drawable = drawable;
        this.id = id;
    }

    public int getDrawable() {
        return drawable;
    }

    public int getID() {
        return id;
    }

    public String getDescription() {
        return descriptionText;
    }

    public String getTile() {
        return titleText;
    }

}