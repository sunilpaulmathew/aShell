package in.sunilpaulmathew.ashell.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.sunilpaulmathew.ashell.serializable.PackageEntry;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2022
 */
public class Packages {

    private static volatile List<PackageEntry> mPackages = Collections.emptyList();

    public static List<PackageEntry> getPackages() {
        return mPackages;
    }

    public static List<PackageEntry> getMatchingPackages(String command) {
        List<PackageEntry> mCommands = new ArrayList<>();
        for (PackageEntry packages: mPackages) {
            if (packages.getPackageName().startsWith(command)) {
                mCommands.add(packages);
            }
        }
        return mCommands;
    }

    public static String sdkToAndroidVersion(int sdkVersion) {
        switch (sdkVersion) {
            case 37:
                return "17 (CINNAMON_BUN, " + sdkVersion + ")";
            case 36:
                return "16 (BAKLAVA, " + sdkVersion + ")";
            case 35:
                return "15 (VANILLA_ICE_CREAM, " + sdkVersion + ")";
            case 34:
                return "14 (UPSIDE_DOWN_CAKE, " + sdkVersion + ")";
            case 33:
                return "13 (TIRAMISU, " + sdkVersion + ")";
            case 32:
                return "12.1 (S_V2, " + sdkVersion + ")";
            case 31:
                return "12 (S, " + sdkVersion + ")";
            case 30:
                return "11 (R, " + sdkVersion + ")";
            case 29:
                return "10 (Q, " + sdkVersion + ")";
            case 28:
                return "9 (P, " + sdkVersion + ")";
            case 27:
                return "8 (O_MR1, " + sdkVersion + ")";
            case 26:
                return "8.0 (0, " + sdkVersion + ")";
            case 25:
                return "7.1.1 (N_MRI, " + sdkVersion + ")";
            case 24:
                return "7.0 (N, " + sdkVersion + ")";
            case 23:
                return "6.0 (M, " + sdkVersion + ")";
            case 22:
                return "5.1 (LOLLIPOP_MR1, " + sdkVersion + ")";
            case 21:
                return "5.0 (LOLLIPOP, " + sdkVersion + ")";
            case 20:
                return "4.4 (KITKAT_WATCH, " + sdkVersion + ")";
            case 19:
                return "4.4 (KITKAT, " + sdkVersion + ")";
            case 18:
                return "4.3 (JELLY_BEAN_MR2, " + sdkVersion + ")";
            case 17:
                return "4.2 (JELLY_BEAN_MR1, " + sdkVersion + ")";
            case 16:
                return "4.1 (JELLY_BEAN, " + sdkVersion + ")";
            case 15:
                return "4.0.3 (ICE_CREAM_SANDWICH_MR1, " + sdkVersion + ")";
            case 14:
                return "4.0 (ICE_CREAM_SANDWICH, " + sdkVersion + ")";
            case 13:
                return "3.2 (HONEYCOMB_MR2, " + sdkVersion + ")";
            case 12:
                return "3.1 (HONEYCOMB_MR1, " + sdkVersion + ")";
            case 11:
                return "3.0 (HONEYCOMB, " + sdkVersion + ")";
            case 10:
                return "2.3.3 (GINGERBREAD_MR1, " + sdkVersion + ")";
            case 9:
                return "2.3 (GINGERBREAD, " + sdkVersion + ")";
            case 8:
                return "2.2 (FROYO, " + sdkVersion + ")";
            case 7:
                return "2.1 (ECLAIR_MR1, " + sdkVersion + ")";
            case 6:
                return "2.0.1 (ECLAIR_0_1, " + sdkVersion + ")";
            case 5:
                return "2.0 (ECLAIR, " + sdkVersion + ")";
            case 4:
                return "1.6 (DONUT, " + sdkVersion + ")";
            case 3:
                return "1.5 (CUPCAKE, " + sdkVersion + ")";
            case 2:
                return "1.1 (BASE_1_1, " + sdkVersion + ")";
            case 1:
                return "1.0 (BASE, " + sdkVersion + ")";
            default:
                return String.valueOf(sdkVersion);
        }
    }

    public static void acquireData() {
        List<PackageEntry> packages = new ArrayList<>();

        try {
            String output = ShizukuShell.runCommand("echo === ENABLED USER APPS === && pm list packages -f -e -3 && echo === DISABLED USER APPS === && pm list packages -f -d -3 && echo === ENABLED SYSTEM APPS === && pm list packages -f -e -s && echo === DISABLED SYSTEM APPS === && pm list packages -f -d -s");
            String currentCategory = "";

            for (String rawLine : output.split("\\r?\\n")) {
                String line = rawLine.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("===") && line.endsWith("===")) {
                    currentCategory = line; // e.g., "=== ENABLED USER APPS ==="
                    continue;
                }

                if (line.startsWith("package:")) {
                    int lastEquals = line.lastIndexOf('=');

                    if (lastEquals > 8) {
                        String apkPath = line.substring(8, lastEquals);
                        String packageName = line.substring(lastEquals + 1);

                        packages.add(new PackageEntry(packageName, apkPath, currentCategory));
                    }
                }
            }
        } catch (Exception ignored) {}
        mPackages = packages;
    }

}