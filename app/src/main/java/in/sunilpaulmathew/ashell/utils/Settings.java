package in.sunilpaulmathew.ashell.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.sunilpaulmathew.ashell.R;
import in.sunilpaulmathew.ashell.activities.aShellActivity;
import in.sunilpaulmathew.ashell.dialogs.SingleChoiceDialog;
import in.sunilpaulmathew.ashell.serializable.CommandItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 21, 2022
 */
public class Settings {

    public static boolean isAmoledBlackEnabled(Context context) {
        return isDarkTheme(context) && Utils.getBoolean("amoledTheme", false, context);
    }

    public static boolean isDarkTheme(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static int getColorAccent(Context context) {
        return getMaterial3Colors(Utils.getColor(R.color.colorBlue, context), context);
    }

    public static int getColorText(Context context) {
        return Utils.getColor(isDarkTheme(context) ? R.color.colorWhite : R.color.colorBlack, context);
    }

    private static int getMaterial3Colors(int defaultColor, Context context) {
        int color = defaultColor;
        if (DynamicColors.isDynamicColorAvailable()) {
            Context dynamicClrCtx = DynamicColors.wrapContextIfAvailable(context, com.google.android.material.R.style.MaterialAlertDialog_Material3);
            TypedArray ta = dynamicClrCtx.obtainStyledAttributes(new int[] {
                    com.google.android.material.R.attr.colorPrimary
            });
            color = ta.getColor(0, defaultColor);
            ta.recycle();
        }
        return color;
    }

    private static int getAppThemePosition(Context context) {
        return Utils.getInt("appTheme", 0, context);
    }

    private static int getLanguagePosition(Context context) {
        String country = getCountry(context);
        switch (getLanguage(context)) {
            case "zh":
                return 8;
            case "ru":
                return 7;
            case "pt":
                return country.equalsIgnoreCase("BR") ? 5 : 6;
            case "in":
                return 4;
            case "hu":
                return 3;
            case "en":
                return country.equalsIgnoreCase("us") ? 2 : 0;
            case "el":
                return 1;
            default:
                return 0;
        }
    }

    public static List<CommandItems> getPolicyData() {
        List<CommandItems> mData = new ArrayList<>();
        mData.add(new CommandItems("Introduction", "aShell is developed by one main developer, sunilpaulmathew, leveraging code from various open-source projects. This Privacy Policy outlines how we handle user privacy."));
        mData.add(new CommandItems("Scope", "This policy applies exclusively to the original version of aShell published by the developer on Google Play, F-Droid, IzzyOnDroid, and GitHub."));
        mData.add(new CommandItems("Personal Information", "We do not collect, store, or share any personal information about our users. User identities remain anonymous. If we inadvertently receive any personal information, we will not disclose or share it with third parties."));
        mData.add(new CommandItems("Permissions", "aShell requires the following permissions to deliver its features:" +
                "\n\uD83D\uDD10 moe.shizuku.manager.permission.API_V23: Permission required to use Shizuku’s privileged APIs." +
                "\n\uD83D\uDCC2 WRITE_EXTERNAL_STORAGE: Allows aShell to write recent command results to device storage."));
        mData.add(new CommandItems("Contact Us", "If you have questions or concerns about this Privacy Policy, please contact us at: smartpack.org@gmail.com"));
        mData.add(new CommandItems("Changes to This Policy", "We may update this policy from time to time. Changes will be posted here."));
        return mData;
    }

    public static String getAppTheme(Context context) {
        int appTheme = Utils.getInt("appTheme", 0, context);
        switch (appTheme) {
            case 2:
                return context.getString(R.string.app_theme_light);
            case 1:
                return context.getString(R.string.app_theme_dark);
            default:
                return context.getString(R.string.app_theme_auto);
        }
    }

    private static String getCountry(Context context) {
        return Utils.getString("country", java.util.Locale.getDefault().getLanguage(), context);
    }

    private static String getLanguage(Context context) {
        return Utils.getString("appLanguage", java.util.Locale.getDefault().getLanguage(), context);
    }

    public static String getLanguageDescription(Context context) {
        String country = getCountry(context);
        switch (getLanguage(context)) {
            case "en":
                return country.equalsIgnoreCase("US") ? context.getString(R.string.language_en, "US")
                        : context.getString(R.string.app_theme_auto);
            case "el":
                return context.getString(R.string.language_el);
            case "pt":
                return context.getString(R.string.language_pt, country.equalsIgnoreCase("BR") ? "BR" : "PT");
            case "ru":
                return context.getString(R.string.language_ru);
            case "hu":
                return context.getString(R.string.language_hu);
            case "in":
                return context.getString(R.string.language_in);
            case "zh":
                return country.equalsIgnoreCase("TW") ? context.getString(R.string.language_zh, "TW")
                        : context.getString(R.string.app_theme_auto);
            default:
                return context.getString(R.string.app_theme_auto);
        }
    }

    private static String[] getAppThemeMenu(Context context) {
        return new String[] {
                context.getString(R.string.app_theme_auto),
                context.getString(R.string.app_theme_dark),
                context.getString(R.string.app_theme_light)
        };
    }

    private static String[] getLanguageMenu(Context context) {
        return new String[] {
                context.getString(R.string.app_theme_auto),
                context.getString(R.string.language_el),
                context.getString(R.string.language_en, "US"),
                context.getString(R.string.language_hu),
                context.getString(R.string.language_in),
                context.getString(R.string.language_pt, "BR"),
                context.getString(R.string.language_pt, "PT"),
                context.getString(R.string.language_ru),
                context.getString(R.string.language_zh, "TW")
        };
    }

    public static void initializeAppTheme(Context context) {
        int appTheme = Utils.getInt("appTheme", 0, context);
        switch (appTheme) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
        context.setTheme(Settings.isAmoledBlackEnabled(context) ? R.style.AppTheme_Amoled : R.style.AppTheme);
    }

    private static Locale getLocale(Context context) {
        if (getCountry(context) != null) {
            return new Locale(getLanguage(context), getCountry(context));
        } else {
            return new Locale(getLanguage(context));
        }
    }

    public static void initializeAppLanguage(Context context) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(getLocale(context));
        res.updateConfiguration(conf, dm);
    }

    public static void restartApp(Activity activity) {
        Intent mainActivity = new Intent(activity, aShellActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(mainActivity);
        activity.finish();
    }

    public static void setAppLanguage(Activity activity) {
        new SingleChoiceDialog(R.drawable.ic_language, activity.getString(R.string.language),
                getLanguageMenu(activity), getLanguagePosition(activity), activity) {

            @Override
            public void onItemSelected(int position) {
                switch (position) {
                    case 0:
                        if (Objects.equals(getLanguage(activity), Locale.getDefault().getLanguage()) && Objects.equals(getCountry(activity), Locale.getDefault().getCountry())) {
                            return;
                        }
                        Utils.saveString("appLanguage", java.util.Locale.getDefault().getLanguage(), activity);
                        Utils.saveString("country", java.util.Locale.getDefault().getCountry(), activity);
                        break;
                    case 1:
                        if (Objects.equals(getLanguage(activity), "el") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "el", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 2:
                        if (Objects.equals(getLanguage(activity), "en") && Objects.equals(getCountry(activity), "US")) {
                            return;
                        }
                        Utils.saveString("appLanguage", "en", activity);
                        Utils.saveString("country", "US", activity);
                        break;
                    case 3:
                        if (Objects.equals(getLanguage(activity), "hu") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "hu", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 4:
                        if (Objects.equals(getLanguage(activity), "in") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "in", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 5:
                        if (Objects.equals(getLanguage(activity), "pt") && Objects.equals(getCountry(activity), "BR")) {
                            return;
                        }
                        Utils.saveString("appLanguage", "pt", activity);
                        Utils.saveString("country", "BR", activity);
                        break;
                    case 6:
                        if (Objects.equals(getLanguage(activity), "pt") && Objects.equals(getCountry(activity), "PT")) {
                            return;
                        }
                        Utils.saveString("appLanguage", "pt", activity);
                        Utils.saveString("country", "PT", activity);
                        break;
                    case 7:
                        if (Objects.equals(getLanguage(activity), "ru") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "ru", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 8:
                        if (Objects.equals(getLanguage(activity), "zh") && Objects.equals(getCountry(activity), "TW")) {
                            return;
                        }
                        Utils.saveString("appLanguage", "zh", activity);
                        Utils.saveString("country", "TW", activity);
                        break;
                }
                restartApp(activity);
            }
        }.show();
    }

    public static void setAppTheme(Context context) {
        new SingleChoiceDialog(R.drawable.ic_theme, context.getString(R.string.app_theme),
                getAppThemeMenu(context), getAppThemePosition(context), context) {

            @Override
            public void onItemSelected(int position) {
                if (position == Utils.getInt("appTheme", 0, context)) {
                    return;
                }
                switch (position) {
                    case 2:
                        Utils.saveInt("appTheme", 2, context);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case 1:
                        Utils.saveInt("appTheme", 1, context);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    default:
                        Utils.saveInt("appTheme", 0, context);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                }
            }
        }.show();
    }

}