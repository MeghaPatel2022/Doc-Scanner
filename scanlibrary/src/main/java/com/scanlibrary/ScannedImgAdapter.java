package com.scanlibrary;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;

import static com.scanlibrary.Const.finalList;

public class ScannedImgAdapter extends RecyclerView.Adapter<ScannedImgAdapter.MyClassView> {

    private static final String IMAGE_DIRECTORY = "/Doc Scan/Capture/";
    private static ProgressDialogFragment progressDialogFragment;
    ArrayList<Uri> scannedImgList;
    Activity activity;
    File dir;
    private Bitmap original;

    public ScannedImgAdapter(ArrayList<Uri> scannedImgList, Activity activity) {
        this.scannedImgList = scannedImgList;
        this.activity = activity;
        dir = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scanned_data, parent, false);
        return new MyClassView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {

        Uri uri = scannedImgList.get(position);
        if (Const.isRotate) {

            File direcory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY + "/");
            if (!direcory.exists())
                direcory.mkdirs();

            File filename = new File(direcory, Calendar.getInstance().getTimeInMillis() + ".jpeg");

            showProgressDialog("Please wait...");
//            Bitmap bmp = getBirmapFromPath(uri);
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            getLenseBitmap(holder,position);
//            notifyItemChanged(position);
            Matrix m = new Matrix();
            m.postRotate(90);
            Bitmap btmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
            String path = SaveImage(btmp,filename.getAbsolutePath());
//            Bitmap bitmap = BitmapFactory.decodeFile(path);
//            holder.img_scanned.setImageBitmap(bitmap);

//            scannedImgList.remove(position);
            Uri finalUri = FileProvider.getUriForFile(activity, "com.codedevs.camscanner" + ".provider", new File(path));
            finalList.set(position, finalUri);
            holder.img_scanned.setImageURI(finalUri);
//            scannedImgList.set(position,finalUri);
//            notifyDataSetChanged();

//            activity.runOnUiThread(new Runnable(){
//                public void run() {
////                    notifyDataSetChanged();
//                }
//            });

            dismissDialog();

            Const.isRotate = false;
        } else {
            holder.img_scanned.setImageURI(uri);
        }
    }

    @Override
    public int getItemCount() {
        return scannedImgList.size();
    }

    private Bitmap getBitmap(Uri uri1) {
        Uri uri = uri1;
        try {
            original = Utils.getBitmap(activity, uri);
            activity.getContentResolver().delete(uri, null, null);
            return original;
        } catch (IOException e) {
            Log.e("LLLLL_Error: ", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getBirmapFromPath(Uri contentUri) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor =
                    activity.getContentResolver().openAssetFileDescriptor(contentUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap original
                = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
        return original;
    }

    protected synchronized void showProgressDialog(String message) {
        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
            // Before creating another loading dialog, close all opened loading dialogs (if any)
            progressDialogFragment.dismissAllowingStateLoss();
        }
        progressDialogFragment = null;
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = activity.getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected synchronized void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }

    private String SaveImage(Bitmap finalBitmap, String destinationPath) {

        File file = new File(destinationPath);

        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    private Uri getLenseBitmap(MyClassView myClassView, int position) {
        Uri finalUri = null;

        File direcory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY + "/");
        if (!direcory.exists())
            direcory.mkdirs();


        File filename = new File(direcory, Calendar.getInstance().getTimeInMillis() + ".jpg");

        File f = new File(activity.getFilesDir(), "Rotate_1.jpeg");
        myClassView.img_scanned.requestLayout();
        myClassView.img_scanned.setDrawingCacheEnabled(true);
        myClassView.img_scanned.layout(0, 0, myClassView.img_scanned.getMeasuredWidth(), myClassView.img_scanned.getMeasuredHeight());
        myClassView.img_scanned.buildDrawingCache(true);
        try {
            Bitmap bitmap = myClassView.img_scanned.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filename));
//            myClassView.img_scanned.setDrawingCacheEnabled(false);
        } catch (FileNotFoundException e) {
            Log.e("LLLL_Error: ", e.getLocalizedMessage());
        }
//        System.out.println("Sagartext_path:" + filename.getAbsolutePath());
//
//        Bitmap bitmap = BitmapFactory.decodeFile(filename.getAbsolutePath());
//
//        String path = SaveImage(bitmap, f.getAbsolutePath());
        finalUri = FileProvider.getUriForFile(activity, "com.codedevs.camscanner" + ".provider", new File(filename.getAbsolutePath()));

        finalList.set(position, finalUri);

        return finalUri;
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        ImageView img_scanned;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            img_scanned = itemView.findViewById(R.id.img_scanned);
        }
    }
}
