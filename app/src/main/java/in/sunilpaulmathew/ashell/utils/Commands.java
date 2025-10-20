package in.sunilpaulmathew.ashell.utils;

import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.ashell.serializable.CommandEntry;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 05, 2022
 */
public class Commands {

    private static List<CommandEntry> mPackages = null;

    public static List<CommandEntry> commandList() {
        List<CommandEntry> mCommands = new ArrayList<>();
        mCommands.add(new CommandEntry("am force-stop <package>", "Completely stop a given package", "am force-stop com.android.package"));
        mCommands.add(new CommandEntry("am kill <package>", "Kill all background processes associated with a given package", "am kill com.android.package"));
        mCommands.add(new CommandEntry("am kill-all", "Kill all processes that are safe to kill (cached, etc)"));
        mCommands.add(new CommandEntry("am start -n <package>/<activity>", "Launch an app’s activity directly by component name", "am start -n com.android.package/com.android.package.exampleActivity"));
        mCommands.add(new CommandEntry("appops get <package>", "Show ops for a package", "appops get com.android.package"));
        mCommands.add(new CommandEntry("appops set <package> <Operation> <Mode>", "Change an app’s permission mode (Modes: allow, ignore, deny, default)", "appops set com.android.package OPERATION_NAME deny"));
        mCommands.add(new CommandEntry("cat <file_path>", "Display the contents of a text file", "cat /system/build.prop"));
        mCommands.add(new CommandEntry("cmd appops get <package>", "Show ops for a package", "cmd appops get com.android.package"));
        mCommands.add(new CommandEntry("cmd appops set <package> <Operation> <Mode>", "Change an app’s permission mode (Modes: allow, ignore, deny, default)", "cmd appops set com.android.package OPERATION_NAME deny"));
        mCommands.add(new CommandEntry("cmd package resolve-activity --brief <package>", "Resolve the default launchable activity for the given package", "cmd package resolve-activity --brief com.android.package"));
        mCommands.add(new CommandEntry("cmd package install-existing <package>", "Reinstall (enable) a package that is present on the system but uninstalled for the user", "cmd package install-existing com.android.package"));
        mCommands.add(new CommandEntry("cmd role add-role-holder android.app.role.SMS <package>", "Assign the SMS role to an app, making it the default SMS handler", "cmd role add-role-holder android.app.role.SMS com.android.package"));
        mCommands.add(new CommandEntry("clear", "Clear terminal screen"));
        mCommands.add(new CommandEntry("cp <from> <to>", "Copy a file", "cp /system/build.prop /sdcard"));
        mCommands.add(new CommandEntry("cp -r <from> <to>", "Copy a file or directory", "cp -r /system/app /sdcard"));
        mCommands.add(new CommandEntry("dpm set-device-owner <package>/<receiver>", "Set the given app as device owner (requires a fresh device or factory reset)","dpm set-device-owner com.android.package/com.android.package.exampleDeviceAdminReceiver"));
        mCommands.add(new CommandEntry("dumpsys activity", "Print activity info"));
        mCommands.add(new CommandEntry("dumpsys battery", "Print battery stats"));
        mCommands.add(new CommandEntry("dumpsys battery set level <n>", "Change the level from 0 to 100", "dumpsys battery set level 95"));
        mCommands.add(new CommandEntry("dumpsys battery set status <n>", "Change the level to unknown, charging, discharging, not charging or full", "dumpsys battery set status 0"));
        mCommands.add(new CommandEntry("dumpsys battery reset", "Reset battery"));
        mCommands.add(new CommandEntry("dumpsys display", "Print display stats"));
        mCommands.add(new CommandEntry("dumpsys iphonesybinfo", "Get IMEI"));
        mCommands.add(new CommandEntry("echo <message>", "Display message on screen", "echo Hallo World"));
        mCommands.add(new CommandEntry("exit", "Exit the shell"));
        mCommands.add(new CommandEntry("file <file_path>", "Determine file type", "file /system/build.prop"));
        mCommands.add(new CommandEntry("grep", "Search file(s) for lines that match a given pattern"));
        mCommands.add(new CommandEntry("kill <pid>", "Kill a process by specifying its PID"));
        mCommands.add(new CommandEntry("logcat", "Display real-time log of system messages, including stack traces"));
        mCommands.add(new CommandEntry("ls", "List contents of a directory", "ls /system"));
        mCommands.add(new CommandEntry("ls -R", "List subdirectories recursively", "ls -R /system"));
        mCommands.add(new CommandEntry("ls -s", "Print size of each file", "ls -s /system"));
        mCommands.add(new CommandEntry("mkdir <file_path>", "Create a directory", "mkdir /sdcard/abc"));
        mCommands.add(new CommandEntry("mv <from> <to>", "Move a file or directory", "mv /system/app /sdcard"));
        mCommands.add(new CommandEntry("netstat", "List TCP connectivity"));
        mCommands.add(new CommandEntry("ping", "Test a network connection"));
        mCommands.add(new CommandEntry("pm clear <package>", "Delete all data associated with an app", "pm clear com.android.package"));
        mCommands.add(new CommandEntry("pm clear --cache-only <package>", "Only clear cache data associated with an app", "pm clear --cache-only com.android.package"));
        mCommands.add(new CommandEntry("pm clear --user <user_id> <package>", "Only delete the data associated with a given user", "pm clear --user 0 com.android.package"));
        mCommands.add(new CommandEntry("pm disable <package/component>", "Disable a given package or component (written as package/class", "pm disable com.android.package/com.android.package.exampleActivity"));
        mCommands.add(new CommandEntry("pm dump <package>", "List info of one app", "pm dump com.android.package"));
        mCommands.add(new CommandEntry("pm dump package packages", "List info of all apps"));
        mCommands.add(new CommandEntry("pm enable <package/component>", "Enable a given package or component (written as package/class", "pm enable com.android.package/com.android.package.exampleActivity"));
        mCommands.add(new CommandEntry("pm grant <package> <Permission>", "Grant a permission to an app", "pm grant com.android.package android.permission.WRITE_EXTERNAL_STORAGE"));
        mCommands.add(new CommandEntry("pm install <apk_path>", "Install an apk file", "pm install /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install -d <apk_path>", "Allow version code downgrade (debuggable packages only)", "pm install -d /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install -f <apk_path>", "Install application on internal flash", "pm install -f /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install -g <apk_path>", "Grant all runtime permissions", "pm install -g /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install -i <installer> <apk_path>", "Specify package name of installer owning the app", "pm install -i com.google.android.packageinstaller /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install -p <split_apk_path>", "Partial application install (new split on top of existing pkg)", "pm install -p /data/local/tmp/base.apk"));
        mCommands.add(new CommandEntry("pm install -R <apk_path>", "Update an existing apps, but disallow replacement of existing one", "pm install -R /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install -t <apk_path>", "Allow installing test packages", "pm install -t /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install --abi <apk_path>", "Override the default ABI of the platform", "pm install --abi /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install --dont-kill <apk_path>", "Install a new feature split without killing the running app", "pm install --dont-kill /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install --full <apk_path>", "Cause the app to be installed as a non-ephemeral full app", "pm install --full /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install --install-location <location> <apk_path>", "Force the install location (Options, 0=auto, 1=internal only, 2=prefer external)", "pm install --install-location 1 /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install --install-reason <reason> <apk_path>", "Indicates why the app is being installed (Options, 0=unknown, 1=admin policy, 2=device restore,3=device setup, 4=user request)", "install --install-reason 2 /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install --instant <apk_path>", "Cause the app to be installed as an ephemeral install app", "pm install --instant /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install --restrict-permissions <apk_path>", "Don't whitelist restricted permissions at install", "pm install --restrict-permissions /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install --pkg <package> <apk_path>", "Specify expected package name of app being installed", "pm install --pkg in.sunilpaulmathew.ashell /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install --user <user_id> <apk_path>", "Install for a given user", "pm install --user0 /data/local/tmp/aShell.apk"));
        mCommands.add(new CommandEntry("pm install-abandon <session_id>", "Delete the given active install session", "pm install-abandon 01234567"));
        mCommands.add(new CommandEntry("pm install-commit <session_id>", "Commit the given active install session, installing the app", "pm install-commit 01234567"));
        mCommands.add(new CommandEntry("pm install-create", "Create an install session"));
        mCommands.add(new CommandEntry("pm install-existing", "Installs an existing application for a new user", "pm install-existing com.android.package"));
        mCommands.add(new CommandEntry("pm install-existing --full <package>", "Install as a full app", "pm install-existing --full com.android.package"));
        mCommands.add(new CommandEntry("pm install-existing --instant <package>", "Install as an instant app", "pm install-existing --instant com.android.package"));
        mCommands.add(new CommandEntry("pm install-existing --restrict-permissions <package>", "Don't whitelist restricted permissions", "pm install-existing --restrict-permissions com.android.package"));
        mCommands.add(new CommandEntry("pm install-existing --user <user_id> <package>", "Install for a given user", "pm install-existing --user 0 com.android.package"));
        mCommands.add(new CommandEntry("pm install-existing --wait <package>", "Wait until the package is installed", "pm install-existing --wait com.android.package"));
        mCommands.add(new CommandEntry("pm install-remove <session_id> <split_name>", "Mark SPLIT(s) as removed in the given install session", "pm install-remove 01234567 /data/local/tmp/base.apk"));
        mCommands.add(new CommandEntry("pm install-write <size> <session_id> <split_name> <split_path>", "Write an apk into the given install session", "pm install-write 123 01234567 base.apk /data/local/tmp/base.apk"));
        mCommands.add(new CommandEntry("pm list features", "List phone features"));
        mCommands.add(new CommandEntry("pm list libraries", "List all system libraries"));
        mCommands.add(new CommandEntry("pm list packages", "List package names"));
        mCommands.add(new CommandEntry("pm list packages -3", "List only third party packages"));
        mCommands.add(new CommandEntry("pm list packages -a", "List all known packages, but excluding APEXes"));
        mCommands.add(new CommandEntry("pm list packages -d", "List all disabled packages"));
        mCommands.add(new CommandEntry("pm list packages -e", "List all enabled packages"));
        mCommands.add(new CommandEntry("pm list packages -f", "List package names along with their associated file"));
        mCommands.add(new CommandEntry("pm list packages -i", "List package names along with their installer"));
        mCommands.add(new CommandEntry("pm list packages -s", "List only system packages"));
        mCommands.add(new CommandEntry("pm list packages --show-versioncode", "List package names along with their version code"));
        mCommands.add(new CommandEntry("pm list packages -u", "List package names of all apps including the uninstalled ones"));
        mCommands.add(new CommandEntry("pm list packages -U", "List package names along with their package UID"));
        mCommands.add(new CommandEntry("pm list permissions", "Print all known permission groups"));
        mCommands.add(new CommandEntry("pm list permissions -d", "Print only dangerous permissions"));
        mCommands.add(new CommandEntry("pm list permissions -f", "Print all information about the known permissions"));
        mCommands.add(new CommandEntry("pm list permissions -g", "Organize all known permission by group"));
        mCommands.add(new CommandEntry("pm list permissions -s", "Print a short summary about the known permissions"));
        mCommands.add(new CommandEntry("pm list permissions -u", "Print only permissions that users will see"));
        mCommands.add(new CommandEntry("pm list users", "List all user names"));
        mCommands.add(new CommandEntry("pm path <package>", "Show apk file path of an app", "pm path com.android.package"));
        mCommands.add(new CommandEntry("pm revoke <package> <Permission>", "Revoke a permission from an app", "pm revoke com.android.package android.permission.WRITE_EXTERNAL_STORAGE"));
        mCommands.add(new CommandEntry("pm reset-permissions -p <package>", "Reset permissions of an app", "pm reset-permissions -p com.android.package"));
        mCommands.add(new CommandEntry("pm uninstall <package>", "Uninstall an app", "pm uninstall com.android.package"));
        mCommands.add(new CommandEntry("pm uninstall -k <package>", "Uninstall an app but keep the data and cache untouched", "pm uninstall -k com.android.package"));
        mCommands.add(new CommandEntry("pm uninstall --user <user_id> <package>", "Uninstall an app from a given user", "pm uninstall --user 0 com.android.package"));
        mCommands.add(new CommandEntry("pm uninstall --versionCode <version_code> <package>", "Only uninstall if the app has the given version code", "pm uninstall --versionCode 123 com.android.package"));
        mCommands.add(new CommandEntry("pm uninstall-system-updates", "Removes updates to all system applications and falls back to its system version"));
        mCommands.add(new CommandEntry("pm uninstall-system-updates <package>", "Removes updates to a given system application and falls back to its system version", "pm uninstall-system-updates com.android.package"));
        mCommands.add(new CommandEntry("ps", "Print process status"));
        mCommands.add(new CommandEntry("pwd", "Print current working directory"));
        mCommands.add(new CommandEntry("reboot", "Reboot device"));
        mCommands.add(new CommandEntry("reboot -p", "Shutdown device"));
        mCommands.add(new CommandEntry("reboot recovery", "Reboot device into recovery mode"));
        mCommands.add(new CommandEntry("reboot fastboot", "Reboot device into fastboot"));
        mCommands.add(new CommandEntry("reboot bootloader", "Reboot device into bootloader"));
        mCommands.add(new CommandEntry("rm <file_path>", "Delete a file", "rm /sdcard/example.txt"));
        mCommands.add(new CommandEntry("rm -r <file_path>", "Delete a file or directory", "rm -r /sdcard/abc"));
        mCommands.add(new CommandEntry("service list", "List all services"));
        mCommands.add(new CommandEntry("settings get secure default_input_method", "Get the package name of the current default keyboard"));
        mCommands.add(new CommandEntry("settings put secure enabled_accessibility_services <package>/<service>","Enable an accessibility service for a given package", "settings put secure enabled_accessibility_services com.android.package/com.android.package.exampleAccessibilityService"));
        mCommands.add(new CommandEntry("sleep <second>", "Delay for a specified time", "sleep 5"));
        mCommands.add(new CommandEntry("sync", "Synchronize data on disk with memory"));
        mCommands.add(new CommandEntry("top", "List processes running on the system"));
        mCommands.add(new CommandEntry("top -n <number>", "Update display <number> times, then exit", "top -n1"));
        mCommands.add(new CommandEntry("whoami", "Print the current user id and name"));
        mCommands.add(new CommandEntry("wm density", "Displays current screen density"));
        mCommands.add(new CommandEntry("wm density reset", "Reset screen density to default"));
        mCommands.add(new CommandEntry("wm size", "Displays the current screen resolution"));
        mCommands.add(new CommandEntry("wm size reset", "Reset screen resolution to default"));
        return mCommands;
    }

    public static List<CommandEntry> getCommand(String command) {
        List<CommandEntry> mCommands = new ArrayList<>();
        for (CommandEntry commands: commandList()) {
            if (commands == null || commands.getTitle().startsWith(command)) {
                mCommands.add(commands);
            }
        }
        return mCommands;
    }

    public static List<CommandEntry> getPackageInfo(String command) {
        List<CommandEntry> mCommands = new ArrayList<>();
        for (CommandEntry packages: mPackages) {
            if (packages.getTitle().startsWith(command)) {
                mCommands.add(packages);
            }
        }
        return mCommands;
    }

    public static void loadPackageInfo() {
        mPackages = new ArrayList<>();

        try {
            String packages = ShizukuShell.runCommand("pm list packages");
            for (String line : packages.trim().split("\\r?\\n")) {
                if (line != null && !line.trim().isEmpty() && line.trim().startsWith("package:")) {
                    String[] parts = line.split(":", 2);
                    mPackages.add(new CommandEntry(parts[1], null));
                }
            }
        } catch (Exception ignored) {}
    }

}