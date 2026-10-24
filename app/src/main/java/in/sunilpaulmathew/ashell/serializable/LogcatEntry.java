package in.sunilpaulmathew.ashell.serializable;

import android.content.Context;
import android.graphics.Color;

import java.io.Serializable;

import in.sunilpaulmathew.ashell.utils.Settings;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 18, 2026
 */
public class LogcatEntry implements Serializable {

    private final String tag;
    private final String message;
    private final char level;

    public LogcatEntry(char level, String tag, String message) {
        this.level = level;
        this.tag = tag;
        this.message = message;
    }

    public char getLevel() {
        return this.level;
    }

    public int getTextColor(Context context) {
        boolean dark = Settings.isDarkTheme(context);
        String hexColor;
        switch (level) {
            case 'E':
            case 'F':
                hexColor = dark ? "#FF5252" : "#D32F2F";
                break;
            case 'W':
                hexColor = dark ? "#FFB74D" : "#E65100";
                break;
            case 'D':
                hexColor = dark ? "#64B5F6" : "#1976D2";
                break;
            default:
                hexColor = dark ? "#E0E0E0" : "#212121";
                break;
        }
        return Color.parseColor(hexColor);
    }

    public String getTag() {
        return this.tag;
    }

    public String getMessage() {
        return this.message;
    }

}