package com.mikpuk.vava_project;

import android.content.Context;
import android.content.res.Resources;

import com.hypertrack.hyperlog.HyperLog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is used for retrieving data from config file
 */
This class is used for retrieving data from config file
public class ConfigManager {

    //Names in config file
    private final static String TOKEN = "token";
    private final static String API_URL = "api_url";
    private final static String SITE_KEY = "site_key";
    private final static String SECRET_KEY = "secret_key";
    private final static String TAG = "Config manager";

    //Returns token which is used in REST request header for authorization
    public static String getAuthToken(Context context)
    {
        HyperLog.i(TAG,"Getting authorization token");
        InputStream rawResource = null;
        try {
            Resources resources = context.getResources();
            rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);

            return MD5Hashing.getSecurePassword(properties.getProperty(TOKEN));
        } catch (IOException e) {
            HyperLog.e(TAG,"Authorization not found",e);
            return "";
        } finally {
            try {
                if (rawResource != null)
                    rawResource.close();
            } catch (IOException e) {
                HyperLog.e(TAG,"Error closing rawResource",e);
            }
        }
    }

    //Returns site key which is used in recaptcha
    public static String getSiteKey(Context context)
    {
        HyperLog.i(TAG,"Getting site key");
        InputStream rawResource=null;
        try {
            Resources resources = context.getResources();
            rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);

            return properties.getProperty(SITE_KEY);
        } catch (IOException e) {
            HyperLog.e(TAG,"Site key not found",e);
            return "";
        } finally {
            try {
                if (rawResource != null)
                    rawResource.close();
            } catch (IOException e) {
                HyperLog.e(TAG,"Error closing rawResource",e);
            }
        }
    }

    //Returns secret key which is used in recaptcha
    public static String getSecretKey(Context context)
    {
        HyperLog.i(TAG,"Getting secret key");
        InputStream rawResource = null;
        try {
            Resources resources = context.getResources();
            rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);

            return properties.getProperty(SECRET_KEY);
        } catch (IOException e) {
            HyperLog.e(TAG,"Secret key not found",e);
            return "";
        } finally {
            try {
                if (rawResource != null)
                    rawResource.close();
            } catch (IOException e) {
                HyperLog.e(TAG,"Error closing rawResource",e);
            }
        }

    }

    //Returns base URL for REST service
    public static String getApiUrl(Context context)
    {
        HyperLog.i(TAG,"Getting api url");
        InputStream rawResource = null;
        try {
            Resources resources = context.getResources();
            rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(API_URL);
        } catch (IOException e) {
            HyperLog.e(TAG,"Api url not found",e);
            return "";
        } finally {
            try {
                if (rawResource != null)
                    rawResource.close();
            } catch (IOException e) {
                HyperLog.e(TAG,"Error closing rawResource",e);
            }
        }
    }

}
