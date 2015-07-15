package com.studygroup.studyappsample.app.common;

/**
 * String Util
 * Created by KHAN on 2015-07-15.
 */
public class StringUtil {
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }
}
