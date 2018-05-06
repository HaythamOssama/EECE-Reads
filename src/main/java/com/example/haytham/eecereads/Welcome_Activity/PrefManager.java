package com.example.haytham.eecereads.Welcome_Activity;
/**
 * Created by haytham on 26/04/18.
 * Controls the first launch of EECE Reads
 */
 import android.content.Context;
 import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "eecereads-welcome";

    private static final String ISFIRST_TIMELAUNCH = "IsFirstTimeLaunch";
    
    public PrefManager(Context context) {
        this._context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(ISFIRST_TIMELAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(ISFIRST_TIMELAUNCH, true);
    }

}
