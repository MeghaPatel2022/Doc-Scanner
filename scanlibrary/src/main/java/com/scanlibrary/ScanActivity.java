package com.scanlibrary;

import android.app.FragmentTransaction;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.scanlibrary.Const.finalList;

/**
 * Created by jhansi on 28/03/15.
 */
public class ScanActivity extends AppCompatActivity implements IScanner, ComponentCallbacks2 {

    public static boolean isGallery = false;
    public static boolean isModification = false;

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("Scanner");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("LLLL_Pref: ", getIntent().getStringExtra("Pref"));
        if (getIntent().getStringExtra("Pref").equals("Gallery")) {
            if (!isGallery) {
                Intent intent = null;
                try {
                    intent = new Intent(ScanActivity.this, Class.forName("com.codedevs.camscanner.Activity.MainActivity"));
                    startActivity(intent);

                    finish();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            isGallery = false;
        }
    }

    private void init() {

        isModification = getIntent().getBooleanExtra("isModification",false);
        ResultFragment.FileName = getIntent().getStringExtra("folderName");

        if (!getIntent().getBooleanExtra("from", false)) {
            Const.finalList.clear();
            if (getIntent().getStringExtra("Pref").equals("Gallery")) {
                ImagePicker.create(ScanActivity.this)
                        .language("in") // Set image picker language
                        .theme(R.style.ImagePickerTheme)
                        .includeVideo(false) // include video (false by default)
                        .onlyVideo(false) // include video (false by default)
                        .folderMode(false)
                        .multi()
                        .limit(30)
                        .showCamera(false)
                        .toolbarArrowColor(Color.BLACK) // set toolbar arrow up color
                        .toolbarFolderTitle("Folder") // folder selection title
                        .toolbarImageTitle("Select Pictures") // image selection title
                        .toolbarDoneButtonText("DONE")
                        .start(); // done button text
            } else {
                PickImageFragment fragment = new PickImageFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(ScanConstants.OPEN_INTENT_PREFERENCE, getPreferenceContent());
                fragment.setArguments(bundle);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content, fragment);
                fragmentTransaction.commit();
            }
        } else {
            ArrayList<Uri> fileLsit = new ArrayList<>(Objects.requireNonNull(getIntent().getParcelableArrayListExtra("fileList")));
            finalList.addAll(fileLsit);
            onScanFinish(fileLsit, true, getIntent().getStringExtra("folderName"), false);
        }
        isGallery = true;
    }

    protected int getPreferenceContent() {
        return getIntent().getIntExtra(ScanConstants.OPEN_INTENT_PREFERENCE, 0);
    }

    @Override
    public void onBitmapSelect(Uri uri) {
        ScanFragment fragment = new ScanFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ScanConstants.SELECTED_BITMAP, uri);
        fragment.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.addToBackStack(ScanFragment.class.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onScanFinish(ArrayList<Uri> uri, boolean from, String Folder, boolean isGallery) {
        ResultFragment fragment = new ResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ScanConstants.SCANNED_RESULT, uri);
        bundle.putBoolean(ScanConstants.SCANNED_FROM, from);
        bundle.putString(ScanConstants.SCANNED_NAME, Folder);
        bundle.putBoolean(ScanConstants.ISGALLERY, isGallery);
        fragment.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.addToBackStack(ResultFragment.class.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onScannedCon(boolean isGallery) {
        PickImageFragment fragment = new PickImageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ScanConstants.OPEN_INTENT_PREFERENCE, getPreferenceContent());
        bundle.putBoolean(ScanConstants.ISGALLERY, isGallery);
        fragment.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.addToBackStack(PickImageFragment.class.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onReorder() {
        ReOrderDocFragment fragment = new ReOrderDocFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ScanConstants.SCANNED_RESULT, Const.finalList);
        fragment.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.addToBackStack(ReOrderDocFragment.class.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onTrimMemory(int level) {
        switch (level) {
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */
                break;
            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */
//                new AlertDialog.Builder(this)
//                        .setTitle(R.string.low_memory)
//                        .setMessage(R.string.low_memory_message)
//                        .create()
//                        .show();
                break;
            default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            isGallery = true;
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);
            // or get a single image only
            for (int i = 0; i < images.size(); i++) {
//                Image image = ImagePicker.getFirstImageOrNull(data);
                finalList.add(images.get(i).getUri());
            }

            onScanFinish(finalList, false, "", true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public native Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

    public native Bitmap getGrayBitmap(Bitmap bitmap);

    public native Bitmap getMagicColorBitmap(Bitmap bitmap);

    public native Bitmap getBWBitmap(Bitmap bitmap);

    public native float[] getPoints(Bitmap bitmap);

    @Override
    public void onBackPressed() {
        Intent intent = null;
        try {
            intent = new Intent(ScanActivity.this, Class.forName("com.codedevs.camscanner.Activity.MainActivity"));
            startActivity(intent);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}