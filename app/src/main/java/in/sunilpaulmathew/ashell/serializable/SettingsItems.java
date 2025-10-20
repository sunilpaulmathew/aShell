package in.sunilpaulmathew.ashell.serializable;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 21, 2022
 */
public class SettingsItems implements Serializable {

    private final boolean mEnabled, mSwitch;
    private final int mIcon, mPosition;
    private final String mTitle;
    private String mDescription;

    public SettingsItems(String title) {
        this.mPosition = 0;
        this.mIcon = Integer.MIN_VALUE;
        this.mTitle = title;
        this.mDescription = null;
        this.mSwitch = false;
        this.mEnabled = false;
    }

    public SettingsItems(int position, int icon, String title, String description) {
        this.mPosition = position;
        this.mIcon = icon;
        this.mTitle = title;
        this.mDescription = description;
        this.mSwitch = false;
        this.mEnabled = false;
    }

    public SettingsItems(int position, int icon, String title, String description, boolean isSwitch, boolean enabled) {
        this.mPosition = position;
        this.mIcon = icon;
        this.mTitle = title;
        this.mDescription = description;
        this.mSwitch = isSwitch;
        this.mEnabled = enabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public boolean isSwitch() {
        return mSwitch;
    }

    public int getIcon() {
        return mIcon;
    }

    public int getPosition() {
        return mPosition;
    }

    public String geTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

}