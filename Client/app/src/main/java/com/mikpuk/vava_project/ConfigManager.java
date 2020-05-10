package com.mikpuk.vava_project;

import android.content.Context;
import android.content.res.Resources;

import com.hypertrack.hyperlog.HyperLog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

   //TODO  commit
    private final static String TOKEN = "token";
    private final static String API_URL = "api_url";
    private static final String TAG = "Config manager";
    public static String getAuthToken(Context context)
    {
        HyperLog.i(TAG,"Getting authorization token");
        try {
            Resources resources = context.getResources();
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);

            return MD5Hashing.getSecurePassword(properties.getProperty(TOKEN));
        } catch (IOException e) {
            HyperLog.e(TAG,"Authorization not found",e);
            System.out.println("NOT FOUND! :(");
            e.printStackTrace();
            return "";
        }
    }

    public static String getApiUrl(Context context)
    {
        HyperLog.i(TAG,"Getting api url");
        try {
            Resources resources = context.getResources();
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(API_URL);
        } catch (IOException e) {
            HyperLog.e(TAG,"Api url not found",e);
            System.out.println("NOT FOUND! :(");
            e.printStackTrace();
            return "";
        }
    }

}
