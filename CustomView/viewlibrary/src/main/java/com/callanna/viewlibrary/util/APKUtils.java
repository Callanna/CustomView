package com.callanna.viewlibrary.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2017/6/12.
 */

public class APKUtils {

    /**
     * Install the application  mimetype
     */
    public static final String INSTALL_MIMETYPE = "application/vnd.android.package-archive";
    /**
     *
     * @param context context
     * @param packageName packageName
     * @return f
     */
    public static boolean startOtherApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        try {
            intent = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * An application is installed
     * @param context context
     * @param packageName packageName
     * @return f
     */
    public static boolean isAInstallPackage(Context context,String packageName) {
        final PackageManager packageManager = context.getApplicationContext().getPackageManager();// get packagemanager
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);// Access to all installed the package information of the program
        List<String> packages = new ArrayList<String>();// Used to store all installed the package name of the program
        if (packageInfos != null) {
            for (PackageInfo packageInfo : packageInfos) {
                packages.add(packageInfo.packageName);
            }
        }
        return packages.contains(packageName);// Whether in the pName targeted application package name, is TRUE, not FALSE
    }

    /**
     * Get the application version number
     *
     * @param context context
     * @return VersionCode
     */
    public static int getMYVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageInfo pi = getPackageInfoByPackage(context,context.getApplicationContext().getPackageName());
            versionCode = pi.versionCode;
        } catch (Exception e) {
        }
        return versionCode;
    }

    /**
     * getMYVersionName
     *
     * @param context context
     * @return versionName
     */
    public static String getMYVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo pi = getPackageInfoByPackage(context,context.getApplicationContext().getPackageName());
            versionName = pi.versionName;
        } catch (Exception e) {
        }
        return versionName;
    }

    /**
     * installApkByPath
     *@param context context
     * @param path path
     */
    public static void installApkByPath(Context context,String path) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), INSTALL_MIMETYPE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        } catch (Exception e) {
                e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    /**
     * uninstallApkByPackage
     *
     * @param context context
     * @param oldpackage oldpackage
     */
    public static void uninstallApkByPackage(Context context,String oldpackage) {
        try {
            Uri packageURI = Uri.parse("package:" + oldpackage);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            context.getApplicationContext().startActivity(uninstallIntent);
        } catch (Exception e) {
        }
    }

    /**
     * Check whether the local APK is than the application of the new installation
     *@param context context
     * @param apkPath  The full path to the local APK
     * @return f
     */
    public static boolean checkIsNewThanInstallApp(Context context,String apkPath) {
        ApplicationInfo apkInfo = getAppInfoByPath(context,apkPath);
        PackageInfo apkPackageInfo = getPackageInfoByPath(context,apkPath);
        PackageInfo appPackageInfo = getPackageInfoByPackage(context,apkInfo.packageName);
        if (apkPackageInfo.versionCode > appPackageInfo.versionCode) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * According to the local APK path for package information, including name and version number version
     * @param context context
     * @param apkPath apkPath
     * @return PackageInfo
     */
    public static PackageInfo getPackageInfoByPath(Context context,String apkPath) {
        return context.getApplicationContext().getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
    }

    /**
     * getPackageInfoByPackage
     *
     * @param context context
     * @param packageName packageName
     *
     * @return PackageInfo
     */
    public static PackageInfo getPackageInfoByPackage(Context context,String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getApplicationContext().getPackageManager().getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * getAppInfoByPackageName
     *
     * @param context context
     *
     * @param packageName packageName
     *
     * @return ApplicationInfo
     */
    public static ApplicationInfo getAppInfoByPackageName(Context context,String packageName) {
        ApplicationInfo info = null;
        try {
            info = context.getApplicationContext().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return info;
    }

    /**
     * getAppInfoByPath
     * @param context context
     * @param apkPath apkPath
     * @return ApplicationInfo
     */
    public static ApplicationInfo getAppInfoByPath(Context context,String apkPath) {
        PackageInfo info = getPackageInfoByPath(context,apkPath);
        ApplicationInfo appInfo = null;
        if (info != null) {
            appInfo = info.applicationInfo;
        }
        return appInfo;
    }

    /**
     * checkAppInstalledByPackageName
     *
     * @param context context
     * @param packageName packageName
     *
     * @return f
     */
    public static boolean checkAppInstalledByPackageName(Context context,String packageName) {

        List<PackageInfo> pinfo = context.getApplicationContext().getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    /**
     * checkAppInstalledByPath
     * @param context  context
     * @param apkPath apkPath
     * @return f
     */
    public static boolean checkAppInstalledByPath(Context context,String apkPath) {
        ApplicationInfo locationInfo = getAppInfoByPath(context,apkPath);
        if (checkAppInstalledByPackageName(context,locationInfo.packageName)) {
            return true;
        }
        return false;
    }
}
