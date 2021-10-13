package com.codedevs.camscanner.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public class Pref {

    public static final String MyPREFERENCES = "Doc Scan";
    public static String Flash = "LOGIN";


    public static boolean getLogin(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean ans = sharedpreferences.getBoolean(Flash, false);
        return ans;
    }

    public static void setLogin(Context c1, boolean value) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(Flash, value);
        editor.apply();
    }

    public static void openDocument(Activity activity, String name) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        File file = new File(name);
        Uri fileUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(fileUri, "text/*");
        } else {
//            intent.setDataAndType(fileUri, mimetype);
            intent.setDataAndType(fileUri, mimetype);
        }
        // custom message for the intent
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//DO NOT FORGET THIS EVER
        activity.startActivity(Intent.createChooser(intent, "Choose an Application:"));
    }

}
