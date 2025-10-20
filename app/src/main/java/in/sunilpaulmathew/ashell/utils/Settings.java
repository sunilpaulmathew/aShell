package in.sunilpaulmathew.ashell.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

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
                    androidx.appcompat.R.attr.colorPrimary
            });
            color = ta.getColor(0, defaultColor);
            ta.recycle();
        }
        return color;
    }

    public static int getAppThemePosition(Context context) {
        return Utils.getInt("appTheme", 0, context);
    }

    private static int getLanguagePosition(Context context) {
        String country = getCountry(context);
        switch (getLanguage(context)) {
            case "it":
                return 21;
            case "cs":
                return 20;
            case "de":
                return country.equalsIgnoreCase("BE") ? 19 : 18;
            case "ar":
                return 17;
            case "pl":
                return 16;
            case "fr":
                return 15;
            case "ja":
                return 14;
            case "ko":
                return 13;
            case "zh":
                return country.equalsIgnoreCase("HK") ? 12 : country.equalsIgnoreCase("CN") ? 11 : 10;
            case "ru":
                return 9;
            case "es":
                return country.equalsIgnoreCase("MX") ? 8 : 7;
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
            case "ar":
                return context.getString(R.string.language_ar, "AR");
            case "de":
                return context.getString(R.string.language_de, country.equalsIgnoreCase("BE") ? "BE" : "DE");
            case "en":
                return country.equalsIgnoreCase("US") ? context.getString(R.string.language_en, "US")
                        : context.getString(R.string.app_theme_auto);
            case "el":
                return context.getString(R.string.language_el);
            case "es":
                return context.getString(R.string.language_es, country.equalsIgnoreCase("MX") ? "MX" : "ES");
            case "fr":
                return context.getString(R.string.language_fr, "FR");
            case "pl":
                return context.getString(R.string.language_pl);
            case "pt":
                return context.getString(R.string.language_pt, country.equalsIgnoreCase("BR") ? "BR" : "PT");
            case "ru":
                return context.getString(R.string.language_ru);
            case "hu":
                return context.getString(R.string.language_hu);
            case "in":
                return context.getString(R.string.language_in);
            case "ja":
                return context.getString(R.string.language_ja);
            case "ko":
                return context.getString(R.string.language_ko);
            case "cs":
                return context.getString(R.string.language_cs);
            case "it":
                return context.getString(R.string.language_it);
            case "zh":
                return country.equalsIgnoreCase("HK") ? context.getString(R.string.language_zh, "Hant")
                        : country.equalsIgnoreCase("CN") ? context.getString(R.string.language_zh, "Hans")
                        : country.equalsIgnoreCase("TW") ? context.getString(R.string.language_zh, "TW")
                        : context.getString(R.string.app_theme_auto);
            default:
                return context.getString(R.string.app_theme_auto);
        }
    }

    public static String[] getAppThemeMenu(Context context) {
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
                context.getString(R.string.language_es, "ES"),
                context.getString(R.string.language_es, "MX"),
                context.getString(R.string.language_ru),
                context.getString(R.string.language_zh, "TW"),
                context.getString(R.string.language_zh, "Hans"),
                context.getString(R.string.language_zh, "Hant"),
                context.getString(R.string.language_ko),
                context.getString(R.string.language_ja),
                context.getString(R.string.language_fr, "FR"),
                context.getString(R.string.language_pl),
                context.getString(R.string.language_ar, "AR"),
                context.getString(R.string.language_de, "DE"),
                context.getString(R.string.language_de, "BE"),
                context.getString(R.string.language_cs),
                context.getString(R.string.language_it)
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
                        if (Objects.equals(getLanguage(activity), "es") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "es", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 8:
                        if (Objects.equals(getLanguage(activity), "es") && Objects.equals(getCountry(activity), "MX")) {
                            return;
                        }
                        Utils.saveString("appLanguage", "es", activity);
                        Utils.saveString("country", "MX", activity);
                        break;
                    case 9:
                        if (Objects.equals(getLanguage(activity), "ru") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "ru", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 10:
                        if (Objects.equals(getLanguage(activity), "zh") && Objects.equals(getCountry(activity), "TW")) {
                            return;
                        }
                        Utils.saveString("appLanguage", "zh", activity);
                        Utils.saveString("country", "TW", activity);
                        break;
                    case 11:
                        if (Objects.equals(getLanguage(activity), "zh") && Objects.equals(getCountry(activity), "CN")) {
                            return;
                        }
                        Utils.saveString("appLanguage", "zh", activity);
                        Utils.saveString("country", "CN", activity);
                        break;
                    case 12:
                        if (Objects.equals(getLanguage(activity), "zh") && Objects.equals(getCountry(activity), "HK")) {
                            return;
                        }
                        Utils.saveString("appLanguage", "zh", activity);
                        Utils.saveString("country", "HK", activity);
                        break;
                    case 13:
                        if (Objects.equals(getLanguage(activity), "ko") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "ko", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 14:
                        if (Objects.equals(getLanguage(activity), "ja") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "ja", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 15:
                        if (Objects.equals(getLanguage(activity), "fr") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "fr", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 16:
                        if (Objects.equals(getLanguage(activity), "pl") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "pl", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 17:
                        if (Objects.equals(getLanguage(activity), "ar") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "ar", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 18:
                        if (Objects.equals(getLanguage(activity), "de") && Objects.equals(getCountry(activity), "DE")) {
                            return;
                        }
                        Utils.saveString("appLanguage", "de", activity);
                        Utils.saveString("country", "DE", activity);
                        break;
                    case 19:
                        if (Objects.equals(getLanguage(activity), "de") && Objects.equals(getCountry(activity), "BE")) {
                            return;
                        }
                        Utils.saveString("appLanguage", "de", activity);
                        Utils.saveString("country", "BE", activity);
                        break;
                    case 20:
                        if (Objects.equals(getLanguage(activity), "cs") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "cs", activity);
                        Utils.saveString("country", null, activity);
                        break;
                    case 21:
                        if (Objects.equals(getLanguage(activity), "it") && Objects.equals(getCountry(activity), null)) {
                            return;
                        }
                        Utils.saveString("appLanguage", "it", activity);
                        Utils.saveString("country", null, activity);
                        break;
                }
                restartApp(activity);
            }
        }.show();
    }

    public static void setSlideInAnimation(final View viewToAnimate, int position) {
        // Only animate items appearing for the first time
        if (position > -1) {
            viewToAnimate.setTranslationY(50f);
            viewToAnimate.setAlpha(0f);

            viewToAnimate.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(150)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        } else {
            // Reset properties to ensure recycled views are displayed correctly
            viewToAnimate.setTranslationY(0f);
            viewToAnimate.setAlpha(1f);
        }
    }

}