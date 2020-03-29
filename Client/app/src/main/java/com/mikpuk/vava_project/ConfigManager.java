package com.mikpuk.vava_project;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    //Mena v config subore
    private final static String TOKEN = "token";
    private final static String API_URL = "api_url";

    public static String getAuthToken(Context context)
    {
        try {
            Resources resources = context.getResources();
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return MD5Hashing.getSecurePassword(properties.getProperty(TOKEN));
        } catch (IOException e) {
            System.out.println("NOT FOUND! :(");
            e.printStackTrace();
            return "";
        }
    }

    public static String getApiUrl(Context context)
    {
        try {
            Resources resources = context.getResources();
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(API_URL);
        } catch (IOException e) {
            System.out.println("NOT FOUND! :(");
            e.printStackTrace();
            return "";
        }
    }

}
