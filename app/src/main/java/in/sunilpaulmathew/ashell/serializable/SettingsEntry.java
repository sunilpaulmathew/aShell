package in.sunilpaulmathew.ashell.serializable;

import android.content.Context;
import android.graphics.Color;

import java.io.Serializable;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 21, 2022
 */
public class SettingsEntry implements Serializable {

    private final boolean mEnabled, mSwitch;
    private final int mIcon, mPosition;
    private final String mTitle;
    private String mDescription;

    public SettingsEntry(String title) {
        this.mPosition = 0;
        this.mIcon = Integer.MIN_VALUE;
        this.mTitle = title;
        this.mDescription = null;
        this.mSwitch = false;
        this.mEnabled = false;
    }

    public SettingsEntry(int position, int icon, String title, String description) {
        this.mPosition = position;
        this.mIcon = icon;
        this.mTitle = title;
        this.mDescription = description;
        this.mSwitch = false;
        this.mEnabled = false;
    }

    public SettingsEntry(int position, int icon, String title, String description, boolean isSwitch, boolean enabled) {
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

    public int getIconColor(Context context) {
        boolean dark = Settings.isDarkTheme(context);
        String hexColor;
        if (this.mIcon == R.drawable.ic_language ||
                this.mIcon == R.drawable.ic_translate) {
            hexColor = dark ? "#4DB6AC" : "#00796B";
        } else if (this.mIcon == R.drawable.ic_amoled_theme) {
            hexColor = dark ? "#58A6FF" : "#0969DA";
        } else if (this.mIcon == R.drawable.ic_extras) {
            hexColor = dark ? "#CE93D8" : "#7B1FA2";
        } else if (this.mIcon == R.drawable.ic_email) {
            hexColor = dark ? "#F48FB1" : "#C2185B";
        } else if (this.mIcon == R.drawable.ic_privacy) {
            hexColor = dark ? "#90A4AE" : "#455A64";
        } else if (this.mIcon == R.drawable.ic_help) {
            hexColor = dark ? "#4FC3F7" : "#0277BD";
        } else if (this.mIcon == R.drawable.ic_theme) {
            hexColor = dark ? "#FFB74D" : "#E65100";
        } else if (this.mIcon == R.drawable.ic_learn) {
            hexColor = dark ? "#9FA8DA" : "#3F51B5";
        } else if (this.mIcon == R.mipmap.ic_launcher) {
            return Integer.MIN_VALUE;
        } else {
            hexColor = dark ? "#FFFFFF" : "#000000";
        }
        return Color.parseColor(hexColor);
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