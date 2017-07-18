package com.diplomatiki.krikonis.rangefinder.app.app;

/**
 * Created by Arvitis on 22/6/2017.
 * his class maintains session data across the app using the SharedPreferences.
 * We store a boolean flag isLoggedIn in shared preferences to check the login status.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "RangeFinderLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_IS_PRO= "isPro";
    private static final String KEY_UID = "uid";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn, String isPro) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_IS_PRO, isPro);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }
    public void setuid(String uid) {

        editor.putString(KEY_UID, uid);
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
    public String isPro(){
        return pref.getString(KEY_IS_PRO,"");
    }
    public String getuid(){
        return pref.getString(KEY_UID,"");
    }
}