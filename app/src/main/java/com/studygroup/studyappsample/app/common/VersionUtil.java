package com.studygroup.studyappsample.app.common;

import android.content.Context;
import android.content.pm.PackageInfo;

import static com.studygroup.studyappsample.app.common.StringUtil.*;

/**
 * Version 관련 유틸
 * Created by KHAN on 2015-07-15.
 */
public class VersionUtil {

    public static String getAppVersion(Context applicationContext) {
        try {
            PackageInfo pi = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
            return pi.versionName;
        }
        catch (Exception e) {
            // do nothing....
        }
        return "1.0.0";
    }

    public static boolean isAvailableVersion(String appVersion, String targetVersion) {
        if (isBlank(targetVersion)) {
            return true;
        }
        if (isBlank(appVersion)) {
            return false;
        }

        String[] appVersions = appVersion.split("\\.");
        String[] supportVersions = targetVersion.split("\\.");
        for (int i = 0; i < supportVersions.length; i++) {
            String supportVer = supportVersions[i];
            String appVer = (appVersions.length > i) ? appVersions[i] : "0";
            int support = Integer.valueOf(supportVer);
            int app = Integer.valueOf(appVer);

            if (app != support) {
                return app >= support;
            }
        }

        return true;
    }

}
