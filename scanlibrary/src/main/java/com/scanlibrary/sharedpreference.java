package com.scanlibrary;

import android.content.Context;
import android.content.SharedPreferences;

public class sharedpreference {

    public static final String MyPREFERENCES = "Doc Scan";
    public static String Flash = "Flash";


    public static String getFlash(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String ans = sharedpreferences.getString(Flash, "OFF");
        return ans;
    }

    public static void setFlash(Context c1, String value) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Flash, value);
        editor.apply();
    }


}
