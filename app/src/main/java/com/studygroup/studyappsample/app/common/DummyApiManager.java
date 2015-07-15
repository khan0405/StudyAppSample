package com.studygroup.studyappsample.app.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Random;

/**
 * api caller dummy
 * Created by KHAN on 2015-07-15.
 */
public class DummyApiManager {

    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager e = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] networkInfos = e.getAllNetworkInfo();

            for (NetworkInfo networkInfo : networkInfos) {
                if(networkInfo.isConnected()) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
    public static int doCallApi() {
        return doCallApi(3000);
    }
    public static int doCallApi(long delayMillis) {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Random r = new Random();
        return r.nextInt(3);
    }
}
