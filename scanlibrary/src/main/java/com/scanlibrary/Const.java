package com.scanlibrary;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class Const {

    public static ArrayList<Uri> finalList = new ArrayList<>();
    public static boolean isUpdate = false;
    public static int position = 0;
    public static float angle = 0f;
    public static boolean isRotate = false;

    private static final String DOC_DIRECTORY = "/Doc Scan/Documents";
    private static final String SCAN_IMG_DIRECTORY = "/Doc Scan/ScannedImage";
    private static final String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();


    private ArrayList<String> getImgfolder() {

        ArrayList<String> foldername = new ArrayList<>();
        File directory = new File(directoryPath + "/" + SCAN_IMG_DIRECTORY);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            foldername.add(files[i].getName());
        }
        return foldername;

    }

    private ArrayList<String> getPaths() {
        ArrayList<String> foldername = new ArrayList<>();
        File directory = new File(directoryPath + "/" + DOC_DIRECTORY);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            foldername.add(files[i].getName());
        }

        return foldername;
    }

    private ArrayList<String> getImgPaths() {
        ArrayList<String> foldername = new ArrayList<>();
        File directory = new File(directoryPath + "/" + SCAN_IMG_DIRECTORY);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            foldername.add(files[i].getName());
        }

        return foldername;
    }

}
