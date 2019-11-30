package kr.ac.ssu.edugochi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class eduPreManger {
    public static final String PREFERENCES_NAME = "edu_preference";

    private static final String TAG = eduPreManger.class.getSimpleName();

    private static final String DEFAULT_VALUE_STRING = "";

    private static final boolean DEFAULT_VALUE_BOOLEAN = false;

    private static final int DEFAULT_VALUE_INT = -1;



    private static SharedPreferences getPreferences(Context context) {

        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

    }


    public static void setString(Context context, String key, String value) {
        Log.d(TAG, String.valueOf(value));

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);

        editor.commit();

    }



    public static void setBoolean(Context context, String key, boolean value) {
        Log.d(TAG, String.valueOf(value));
        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(key, value);

        editor.commit();

    }


    public static void setInt(Context context, String key, int value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }





    /**

     * String 값 로드

     * @param whiteNoise
     * @param context

     * @param key

     * @return

     */

    public static String getString(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        String value = prefs.getString(key, DEFAULT_VALUE_STRING);

        return value;

    }


    public static boolean getBoolean(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        boolean value = prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);

        return value;

    }


    public static int getInt(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        int value = prefs.getInt(key, DEFAULT_VALUE_INT);

        return value;

    }


    public static void removeKey(Context context, String key) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor edit = prefs.edit();

        edit.remove(key);

        edit.commit();

    }


    //preference 초기화!!!!
    public static void clear(Context context) {

        SharedPreferences prefs = getPreferences(context);

        SharedPreferences.Editor edit = prefs.edit();

        edit.clear();

        edit.commit();

    }

}
