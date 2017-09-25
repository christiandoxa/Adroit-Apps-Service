package com.adroitdevs.adroitapps.adroitiotservice.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rexchris on 21/08/17.
 */

public class TokenPrefrences {
    public static final String MY_PREFS = "MyPrefs";
    public static final String TOKEN = "Token";

    protected static void setPrefString(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    protected static void clearPrefString(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    protected static String getPrefString(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    public static void setToken(Context context, String value) {
        setPrefString(context, TOKEN, value);
    }

    public static String getToken(Context context) {
        return getPrefString(context, TOKEN);
    }

    public static void clearToken(Context context) {
        clearPrefString(context);
    }
}
