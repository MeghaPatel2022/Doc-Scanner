package com.scanlibrary;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.esafirm.imagepicker.features.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.scanlibrary.Const.finalList;
import static com.scanlibrary.Const.isRotate;
import static com.scanlibrary.Const.isUpdate;


/**
 * Created by jhansi on 29/03/15.
 */
public class ResultFragment extends Fragment implements View.OnClickListener {

    final static Pattern PATTERN = Pattern.compile("(.*?)(?:\\((\\d+)\\))?(\\.[^.]*)?");
    private static final String IMAGE_DIRECTORY = "/Doc Scan/Capture";
    private static final String DOC_DIRECTORY = "/Doc Scan/Documents";
    private static final String SCAN_IMG_DIRECTORY = "/Doc Scan/ScannedImage";
    public static String FileName = "";
    private static ProgressDialogFragment progressDialogFragment;
    BottomSheetBehavior behavior1;
    BottomSheetBehavior behavior2;
    int count = 0;
    private View view;
    private RelativeLayout rl_camera_1;
    private RelativeLayout rl_gallery_1;
    private LinearLayout ll_crop;
    private LinearLayout ll_delete;
    private LinearLayout ll_rotate;
    private LinearLayout ll_reorder;
    private LinearLayout ll_add_image;
    private LinearLayout ll_add_image_1;
    private LinearLayout ll_save_dia;
    private EditText et_name;
    private Button btn_cancel, btn_save;
    private TextView tv_fileName;
    private TextView tv_file_name;
    private LinearLayout ll_file_name;
    private ImageView img_edit_name;
    private RelativeLayout rl_save_image, rl_save_pdf;
    private DiscreteScrollView rv_picker;
    private ScannedImgAdapter scannedImgAdapter;
    private LinearLayout ll_change_name;
    private TextView tv_save;
    private ImageView img_back;
    private Bitmap transformed;
    private BottomSheetBehavior behavior;
    private IScanner scanner;
    private boolean isGallery = false;
    private boolean isFrom = false;
    private MediaScannerConnection msConn;

    public ResultFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof IScanner)) {
            throw new ClassCastException("Activity must implement IScanner");
        }
        this.scanner = (IScanner) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.result_layout, null);

        init();
        return view;
    }

    private void init() {

        tv_fileName = view.findViewById(R.id.tv_fileName);
        tv_file_name = view.findViewById(R.id.tv_file_name);
        rl_save_pdf = view.findViewById(R.id.rl_save_pdf);
        rl_save_image = view.findViewById(R.id.rl_save_image);
        ll_file_name = view.findViewById(R.id.ll_file_name);
        ll_change_name = view.findViewById(R.id.ll_change_name);
        img_edit_name = view.findViewById(R.id.img_edit_name);
        tv_save = view.findViewById(R.id.tv_save);
        btn_save = view.findViewById(R.id.btn_save);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        et_name = view.findViewById(R.id.et_name);
        ll_save_dia = view.findViewById(R.id.ll_save_dia);
        ll_crop = view.findViewById(R.id.ll_crop);
        ll_delete = view.findViewById(R.id.ll_delete);
        ll_rotate = view.findViewById(R.id.ll_rotate);
        ll_reorder = view.findViewById(R.id.ll_reorder);
        ll_add_image = view.findViewById(R.id.ll_add_image);
        ll_add_image_1 = view.findViewById(R.id.ll_add_image_1);
        rl_camera_1 = view.findViewById(R.id.rl_camera_1);
        rl_gallery_1 = view.findViewById(R.id.rl_gallery_1);
        img_back = view.findViewById(R.id.img_back);

        rv_picker = view.findViewById(R.id.rv_picker);

        rv_picker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        isFrom = getArguments().getBoolean(ScanConstants.SCANNED_FROM);
        isGallery = getArguments().getBoolean(ScanConstants.ISGALLERY);

        if (FileName.equals("")) {
            FileName = "Doc-Scan_" + getTodayDate();
        }

        Log.e("LLLLL_FileName: ", FileName);
        tv_fileName.setText(FileName);
        tv_file_name.setText(FileName);
        et_name.setText(FileName);

        rl_save_pdf.setOnClickListener(this);
        rl_save_image.setOnClickListener(this);
        ll_file_name.setOnClickListener(this);
        img_edit_name.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        ll_crop.setOnClickListener(this);
        ll_delete.setOnClickListener(this);
        ll_rotate.setOnClickListener(this);
        ll_reorder.setOnClickListener(this);
        ll_add_image_1.setOnClickListener(this);
        rl_camera_1.setOnClickListener(this);
        rl_gallery_1.setOnClickListener(this);

        behavior = BottomSheetBehavior.from(ll_change_name);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        behavior1 = BottomSheetBehavior.from(ll_save_dia);
        behavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        behavior2 = BottomSheetBehavior.from(ll_add_image);
        behavior2.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        backPress(view);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (behavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (behavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior2.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    ScanActivity.isGallery = false;
                    scanner.onScannedCon(isGallery);
                }
            }
        });

        setAdapter();
    }

    private void backPress(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (behavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (behavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior2.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    ScanActivity.isGallery = false;
                    scanner.onScannedCon(isGallery);
                }
                return true;
            }
            return false;
        });
    }

//    private Bitmap getBitmap() {
//        Uri uri = getUri();
//        try {
//            original = Utils.getBitmap(getActivity(), uri);
//            getActivity().getContentResolver().delete(uri, null, null);
//            return original;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private void setAdapter() {
        ArrayList<Uri> uri = getArguments().getParcelableArrayList(ScanConstants.SCANNED_RESULT);
        scannedImgAdapter = new ScannedImgAdapter(finalList, getActivity());
        rv_picker.setAdapter(scannedImgAdapter);
    }

    private String getTodayDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
        String formattedDate = df.format(c);

        return formattedDate;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_save_image) {
            String foldername = "";
            behavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
            showProgressDialog("Saving...");

            String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + SCAN_IMG_DIRECTORY;
            if (!ScanActivity.isModification)
                foldername = getImgFolderName();
            else
                foldername = FileName;


            File file = new File(directoryPath + "/" + foldername);
            if (!file.exists()) {
                file.mkdirs();
            }
            for (int i = 0; i < finalList.size(); i++) {
                try {
                    try {
                        copyFile(new File(getRealPathFromURI(finalList.get(i))), new File(directoryPath + "/" + foldername + "/" + tv_file_name.getText().toString().trim() + "_" + i + ".jpg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(new File(finalList.get(i).getPath()).getAbsolutePath());
                        SaveImage(bitmap, new File(directoryPath + "/" + foldername + "/" + tv_file_name.getText().toString().trim() + "_" + i + ".jpg").getAbsolutePath());
//                        copyFile(new File(finalList.get(i).getPath()), new File(filena + "/" + et_name.getText().toString().trim() + "_" + i + ".jpg"));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
            System.gc();
            getActivity().runOnUiThread(() -> {
                deleteFolder();
                finalList.clear();
                dismissDialog();
                try {
                    Intent intent = new Intent(getActivity(), Class.forName("com.codedevs.camscanner.Activity.MainActivity"));
                    getActivity().startActivity(intent);
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else if (id == R.id.rl_save_pdf) {
            String foldername = "";
            FileName = et_name.getText().toString().trim();

            behavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
            showProgressDialog("Saving...");
            Document document = new Document();
            PdfDocument pdfDocument = new PdfDocument();
            if (!ScanActivity.isModification)
                foldername = getFolderName();
            else
                foldername = FileName;

            String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + DOC_DIRECTORY + "/" + foldername;
            FileOutputStream fileOutputStream = null;

            File file = new File(directoryPath);
            if (!file.exists()) {
                file.mkdirs();
            }


            try {
                fileOutputStream = new FileOutputStream(directoryPath + "/" + foldername + ".pdf");
                PdfWriter.getInstance(document, fileOutputStream); //  Change pdf's name.
            } catch (Exception e) {
                e.printStackTrace();
            }
            document.open();

            for (int i = 0; i < finalList.size(); i++) {

                try {

                    String filena = directoryPath + "/.img";
                    File file1 = new File(filena);

                    if (!file1.exists()) {
                        file1.mkdirs();
                    }
                    Image image;
                    try {
                        File file2 = new File(filena + "/" + tv_file_name.getText().toString().trim() + "_" + i + ".jpg");
                        copyFile(new File(getRealPathFromURI(finalList.get(i))), file2);
//                        image = Image.getInstance(getRealPathFromURI(finalList.get(i)));  // Change image's name and extension.
//
//                        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
//                                - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
//                        image.scalePercent(scaler);
//                        image.setAlignment(Image.ALIGN_CENTER);
//
//                        document.add(image);

                        Bitmap bitmap = BitmapFactory.decodeFile(file2.getAbsolutePath());
                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), (i + 1)).create();
                        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                        Canvas canvas = page.getCanvas();
                        Paint paint = new Paint();
                        paint.setColor(Color.BLUE);
                        canvas.drawPaint(paint);
                        canvas.drawBitmap(bitmap, 0f, 0f, null);
                        pdfDocument.finishPage(page);
                        bitmap.recycle();

                    } catch (IOException e) {
                        Log.e("LLLLLL_E1: ", Objects.requireNonNull(e.getMessage()));
                        e.printStackTrace();
                    }


                } catch (Exception e) {
                    Log.e("LLLLL_Exc: ", Objects.requireNonNull(e.getLocalizedMessage()));
                    e.printStackTrace();
                    try {
                        String filena = directoryPath + "/.img";
                        File file1 = new File(filena);

                        if (!file1.exists()) {
                            file1.mkdirs();
                        }

                        Log.e("LLLL_file_: ", finalList.get(i).toString() + "    " + new File(finalList.get(i).toString()).getAbsolutePath());
//                        Bitmap bitmap = BitmapFactory.decodeFile(new File(finalList.get(i).toString()).getAbsolutePath());
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), finalList.get(i));
//                        copyFile(new File(finalList.get(i).getPath()), new File(filena + "/" + et_name.getText().toString().trim() + "_" + i + ".jpg"));

//                        Image image = Image.getInstance(SaveImage(bitmap, new File(filena + "/" + et_name.getText().toString().trim() + "_" + i + ".jpg").getAbsolutePath()));  // Change image's name and extension.
//
//                        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
//                                - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
//                        image.scalePercent(scaler);
//                        image.setAlignment(Image.ALIGN_CENTER);
                        SaveImage(bitmap, new File(filena + "/" + et_name.getText().toString().trim() + "_" + i + ".jpg").getAbsolutePath());
                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), (i + 1)).create();
                        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                        Canvas canvas = page.getCanvas();
                        Paint paint = new Paint();
                        paint.setColor(Color.BLUE);
                        canvas.drawPaint(paint);
                        canvas.drawBitmap(bitmap, 0f, 0f, null);
                        pdfDocument.finishPage(page);
                        bitmap.recycle();

//                        document.add(image);
                    } catch (Exception e2) {
                        Log.e("LLLLLL_E2: ", Objects.requireNonNull(e2.getMessage()));
                        e2.printStackTrace();
                    }


                }
            }
            try {
                pdfDocument.writeTo(fileOutputStream);
                pdfDocument.close();
            } catch (Exception e){
                e.printStackTrace();
                Log.e("LLLL_E3: ",e.getMessage());
            }

//            document.close();

            System.gc();
            getActivity().runOnUiThread(() -> {
                deleteFolder();
                finalList.clear();
                dismissDialog();
                try {
                    Intent intent = new Intent(getActivity(), Class.forName("com.codedevs.camscanner.Activity.MainActivity"));
                    getActivity().startActivity(intent);
                    getActivity().finish();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            });

        } else if (v.getId() == R.id.ll_file_name) {
            ll_change_name.setVisibility(View.VISIBLE);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (v.getId() == R.id.img_edit_name) {
            ll_change_name.setVisibility(View.VISIBLE);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (v.getId() == R.id.btn_save) {
            FileName = et_name.getText().toString().trim();
            tv_fileName.setText(FileName);
            tv_file_name.setText(FileName);
            hideKeyboard(getActivity());
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            ll_change_name.setVisibility(View.GONE);
        } else if (v.getId() == R.id.btn_cancel) {
            hideKeyboard(getActivity());
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            ll_change_name.setVisibility(View.GONE);
        } else if (v.getId() == R.id.tv_save) {
            behavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
            hideKeyboard(getActivity());
        } else if (v.getId() == R.id.ll_crop) {
            scanner.onBitmapSelect(finalList.get(rv_picker.getCurrentItem()));
            Const.position = rv_picker.getCurrentItem();
            isUpdate = true;
        } else if (v.getId() == R.id.ll_delete) {
            if (finalList.size() > 0) {
                Log.e("LLLL_FileList: ", new File(finalList.get(rv_picker.getCurrentItem()).getPath()).getParent());
                deleteFolder(new File(finalList.get(rv_picker.getCurrentItem()).getPath()).getAbsolutePath());
            }

        } else if (v.getId() == R.id.ll_rotate) {
            isRotate = true;
            rv_picker.getAdapter().notifyItemChanged(rv_picker.getCurrentItem());
//            showProgressDialog("Please wait...");
//            File dir = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY + "/");
//            Bitmap bmp = getBirmapFromPath(finalList.get(rv_picker.getCurrentItem()));
////            holder.img_scanned.setImageBitmap(getBirmapFromPath(uri));
//
////            holder.img_scanned.setRotation(holder.img_scanned.getRotation() + 90);
//            Matrix m = new Matrix();
//            m.postRotate(90);
//            String filename = new File(dir.getAbsolutePath() + Calendar.getInstance().getTimeInMillis() + ".png").getAbsolutePath();
//            Bitmap bitmap = BitmapFactory.decodeFile(path);
//            Bitmap bitmap = getBirmapFromPath(finalList.get(rv_picker.getCurrentItem()));
//
//            Matrix m = new Matrix();
//            m.postRotate(90, bitmap.getHeight(), bitmap.getWidth());
//
//            Bitmap rotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
//            finalList.set(rv_picker.getCurrentItem(), saveRotateImg(rotate));
//            dismissDialog();

            rv_picker.getAdapter().notifyItemChanged(rv_picker.getCurrentItem());
        } else if (v.getId() == R.id.ll_reorder) {
            scanner.onReorder();
        } else if (v.getId() == R.id.ll_add_image_1) {
            behavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (v.getId() == R.id.rl_camera_1) {
            isGallery = false;
            ScanActivity.isGallery = true;
            behavior2.setState(BottomSheetBehavior.STATE_COLLAPSED);
            scanner.onScannedCon(false);
        } else if (v.getId() == R.id.rl_gallery_1) {
            isGallery = true;
            behavior2.setState(BottomSheetBehavior.STATE_COLLAPSED);
            ScanActivity.isGallery = true;
            ImagePicker.create(getActivity())
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

        }
    }

    private Uri saveRotateImg(Bitmap result) {
        Uri finalUri = null;
        String filePath = "";
        try {
            File direcory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY + "/");
            if (!direcory.exists())
                direcory.mkdirs();

            try {

                File f = new File(direcory, Calendar.getInstance().getTimeInMillis() + ".jpg");
                f.createNewFile();
                f.getAbsoluteFile();

                Log.e("LLLL_selectedfile", f + "");
                FileOutputStream fo = new FileOutputStream(f);
                result.compress(Bitmap.CompressFormat.JPEG, 100, fo);
                fo.flush();
                fo.close();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    MediaScannerConnection.scanFile(getActivity(), new String[]{f.getPath()}, new String[]{"image/jpeg"}, null);
                }
                filePath = f.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();

            }

            finalUri = FileProvider.getUriForFile(getActivity(), "com.codedevs.camscanner" + ".provider", new File(filePath));
            return finalUri;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalUri;
    }

    public Bitmap getBirmapFromPath(Uri contentUri) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor =
                    getActivity().getContentResolver().openAssetFileDescriptor(contentUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap original
                = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
        return original;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        ScanActivity.isGallery = true;
//    }

    private String rotateImage(String destinationPath) {

        File direcory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY + "/");
        if (!direcory.exists())
            direcory.mkdirs();


        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;

        Bitmap bm = BitmapFactory.decodeFile(finalList.get(rv_picker.getCurrentItem()).getPath(), bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) 600);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) 800);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(finalList.get(rv_picker.getCurrentItem()).getPath(), bmpFactoryOptions);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        File file = new File(destinationPath);

        if (file.exists()) file.delete();

        // recreate the new Bitmap
        Bitmap src = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

        try {
            FileOutputStream out = new FileOutputStream(file);
            src.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    private String SaveImage(Bitmap finalBitmap, String destinationPath) {

        File direcory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY + "/");
        if (!direcory.exists())
            direcory.mkdirs();

        File file = new File(destinationPath);

        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }


    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void deleteFolder(String path) {
        Log.e("LLLLLL_Path: ", path);
        File dir = new File(path);
        try {
            if (dir.exists())
                FileUtils.forceDelete(dir);
            finalList.remove(rv_picker.getCurrentItem());
        } catch (IOException e) {
            e.printStackTrace();
        }
        getActivity().runOnUiThread(() -> {
            try {
                if (finalList.size() == 0) {
                    Intent intent = new Intent(getActivity(), Class.forName("com.codedevs.camscanner.Activity.MainActivity"));
                    getActivity().startActivity(intent);
                    getActivity().finish();
                } else {
                    scannedImgAdapter = new ScannedImgAdapter(finalList, getActivity());
                    rv_picker.setAdapter(scannedImgAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }


    private void copyFile(File sourceFile, File destFile) throws IOException {
        Log.e("LLLLL_FilePAth: ", sourceFile.getAbsolutePath());
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }


    private String getFolderName() {

        String foldername = et_name.getText().toString().trim();
        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + DOC_DIRECTORY;
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains(et_name.getText().toString().trim())) {
                    Matcher m = PATTERN.matcher(files[i].getName());
                    if (m.matches()) {
                        String prefix = m.group(1);
                        String last = m.group(2);
                        String suffix = m.group(3);
                        if (suffix == null) suffix = "";

                        int count = last != null ? Integer.parseInt(last) : 0;

                        do {
                            count++;
                            foldername = prefix + "(" + count + ")" + suffix;
                        } while (fileDocExists(files[i].getName()));
                    }
                }
            }
        }

        return foldername;
    }

    private String getImgFolderName() {

//        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + SCAN_IMG_DIRECTORY + "/" + foldername;

        String foldername = et_name.getText().toString().trim();
        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + SCAN_IMG_DIRECTORY;
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains(et_name.getText().toString().trim())) {
                    Matcher m = PATTERN.matcher(files[i].getName());
                    if (m.matches()) {
                        String prefix = m.group(1);
                        String last = m.group(2);
                        String suffix = m.group(3);
                        if (suffix == null) suffix = "";

                        int count = last != null ? Integer.parseInt(last) : 0;

                        do {
                            count++;
                            foldername = prefix + "(" + count + ")" + suffix;
                        } while (fileImgExists(files[i].getName()));
                    }
                }
            }
        }


        return foldername;
    }

    private boolean fileDocExists(String fileName) {

        boolean isExist = false;
        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + DOC_DIRECTORY;
        File file = new File(directoryPath + "/" + fileName + ".pdf");
        if (file.exists()) {
            isExist = true;
        }

        return isExist;
    }

    private boolean fileImgExists(String fileName) {

        boolean isExist = false;
        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + SCAN_IMG_DIRECTORY;
        File file = new File(directoryPath + "/" + fileName + ".pdf");
        if (file.exists()) {
            isExist = true;
        }

        return isExist;
    }

    private void deleteFolder() {
        File dir = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }
        }
        new File(dir.getAbsolutePath()).delete();
    }


    protected synchronized void showProgressDialog(String message) {
        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
            // Before creating another loading dialog, close all opened loading dialogs (if any)
            progressDialogFragment.dismissAllowingStateLoss();
        }
        progressDialogFragment = null;
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected synchronized void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<com.esafirm.imagepicker.model.Image> images = ImagePicker.getImages(data);
            // or get a single image only
//            com.esafirm.imagepicker.model.Image image = ImagePicker.getFirstImageOrNull(data);
            for (int i = 0; i < images.size(); i++) {
                finalList.add(images.get(i).getUri());
            }
            rv_picker.getAdapter().notifyDataSetChanged();

//            scanner.onBitmapSelect(image.getUri());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


//    private class BWButtonClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(final View v) {
//            showProgressDialog(getResources().getString(R.string.applying_filter));
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        transformed = ((ScanActivity) getActivity()).getBWBitmap(original);
//                    } catch (final OutOfMemoryError e) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                transformed = original;
//                                scannedImageView.setImageBitmap(original);
//                                e.printStackTrace();
//                                dismissDialog();
//                                onClick(v);
//                            }
//                        });
//                    }
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            scannedImageView.setImageBitmap(transformed);
//                            dismissDialog();
//                        }
//                    });
//                }
//            });
//        }
//    }
//
//    private class MagicColorButtonClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(final View v) {
//            showProgressDialog(getResources().getString(R.string.applying_filter));
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        transformed = ((ScanActivity) getActivity()).getMagicColorBitmap(original);
//                    } catch (final OutOfMemoryError e) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                transformed = original;
//                                scannedImageView.setImageBitmap(original);
//                                e.printStackTrace();
//                                dismissDialog();
//                                onClick(v);
//                            }
//                        });
//                    }
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            scannedImageView.setImageBitmap(transformed);
//                            dismissDialog();
//                        }
//                    });
//                }
//            });
//        }
//    }
//
//    private class OriginalButtonClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            try {
//                showProgressDialog(getResources().getString(R.string.applying_filter));
//                transformed = original;
//                scannedImageView.setImageBitmap(original);
//                dismissDialog();
//            } catch (OutOfMemoryError e) {
//                e.printStackTrace();
//                dismissDialog();
//            }
//        }
//    }
//
//    private class GrayButtonClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(final View v) {
//            showProgressDialog(getResources().getString(R.string.applying_filter));
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        transformed = ((ScanActivity) getActivity()).getGrayBitmap(original);
//                    } catch (final OutOfMemoryError e) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                transformed = original;
//                                scannedImageView.setImageBitmap(original);
//                                e.printStackTrace();
//                                dismissDialog();
//                                onClick(v);
//                            }
//                        });
//                    }
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            scannedImageView.setImageBitmap(transformed);
//                            dismissDialog();
//                        }
//                    });
//                }
//            });
//        }
//    }

}