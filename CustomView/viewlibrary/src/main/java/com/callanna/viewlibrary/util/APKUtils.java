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
     * 安装应用程序的mimetype
     */
    public static final String INSTALL_MIMETYPE = "application/vnd.android.package-archive";
    /**
     *
     * @description 调用第三方应用
     * @author Joe
     * @param context
     * @param packageName
     * @return
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
     * 是否安装了某个应用
     *
     * @author YOLANDA
     * @param packageName
     * @return
     */
    public static boolean isAInstallPackage(Context context,String packageName) {
        final PackageManager packageManager = context.getApplicationContext().getPackageManager();// 获取packagemanager
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        List<String> packages = new ArrayList<String>();// 用于存储所有已安装程序的包名
        if (packageInfos != null) {
            for (PackageInfo packageInfo : packageInfos) {
                packages.add(packageInfo.packageName);
            }
        }
        return packages.contains(packageName);// 判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }

    /**
     * 获得本应用版本号
     *
     * @author YOLANDA
     * @param context
     * @return 返回版本号
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
     * 获得应用版本名称
     *
     * @author YOLANDA
     * @param context
     * @return
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
     * 启动安装APK
     *
     * @author YOLANDA
     * @param path
     * @return
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
     * 启动卸载
     *
     * @author YOLANDA
     * @param context
     * @param oldpackage
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
     * 检查本地APK是否比安装的应用新
     *
     * @author YOLANDA
     * @param apkPath
     *            本地APK完整路径
     * @return
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
     * 按本地APK路径获取包信息，包含版本名称和版本号
     *
     * @author YOLANDA
     * @param context
     * @param apkPath
     * @return
     */
    public static PackageInfo getPackageInfoByPath(Context context,String apkPath) {
        return context.getApplicationContext().getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
    }

    /**
     * 按已安装程序报名获取包信息
     *
     * @author YOLANDA
     * @param context
     * @param packageName
     *            已安装程序的包名
     * @return
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
     * 按已安装程序报名读取应用信息
     *
     * @author YOLANDA
     * @param context
     *            上下文
     * @param packageName
     *            包名
     * @return
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
     * 按本地APK路径读取APK应用信息
     *
     * @author YOLANDA
     *   apkPath         APK完整路径
     * @return
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
     * 按报名检查某个应用是否安装
     *
     * @author YOLANDA
     * @param context
     * @param packageName
     *            包名
     * @return
     */
    public static boolean checkAppInstalledByPackageName(Context context,String packageName) {
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = context.getApplicationContext().getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    /**
     * 按本地APK完整路径检查应用是否安装
     *
     * @author YOLANDA
     *            本地APK完整路径
     * @return
     */
    public static boolean checkAppInstalledByPath(Context context,String apkPath) {
        ApplicationInfo locationInfo = getAppInfoByPath(context,apkPath);
        if (checkAppInstalledByPackageName(context,locationInfo.packageName)) {
            return true;
        }
        return false;
    }
}
