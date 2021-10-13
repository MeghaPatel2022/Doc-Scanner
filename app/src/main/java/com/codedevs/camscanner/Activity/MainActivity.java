package com.codedevs.camscanner.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.codedevs.camscanner.Adapter.DrawerAdapter;
import com.codedevs.camscanner.Adapter.GridDocAdapter;
import com.codedevs.camscanner.Adapter.ListDocAdapter;
import com.codedevs.camscanner.R;
import com.codedevs.camscanner.Utils.ListViewUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static Pattern PATTERN = Pattern.compile("(.*?)(?:\\((\\d+)\\))?(\\.[^.]*)?");
    private static final int REQUEST_CODE = 99;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private static final String DOC_DIRECTORY = "/Doc Scan/Documents";
    private static final String SCAN_IMG_DIRECTORY = "/Doc Scan/ScannedImage";
    private static final String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();
    private static int position = 0;
    public PrintedPdfDocument myPdfDocument;
    @BindView(R.id.nav_list)
    ListViewUtil nav_list;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;
    @BindView(R.id.img_menu)
    ImageView img_menu;
    @BindView(R.id.rv_grid_doc)
    RecyclerView rv_grid_doc;
    @BindView(R.id.rv_list_doc)
    RecyclerView rv_list_doc;
    @BindView(R.id.img_grid)
    ImageView img_grid;
    @BindView(R.id.img_list)
    ImageView img_list;
    @BindView(R.id.rl_camera_1)
    RelativeLayout rl_camera_1;
    @BindView(R.id.rl_gallery_1)
    RelativeLayout rl_gallery_1;
    @BindView(R.id.ll_send)
    LinearLayout ll_send;
    @BindView(R.id.rl_save_as_pdf)
    RelativeLayout rl_save_as_pdf;
    @BindView(R.id.rl_save_jpeg)
    RelativeLayout rl_save_jpeg;
    @BindView(R.id.rl_modify_scan)
    RelativeLayout rl_modify_scan;
    @BindView(R.id.rl_rename)
    RelativeLayout rl_rename;
    @BindView(R.id.rl_print)
    RelativeLayout rl_print;
    @BindView(R.id.rl_delete)
    RelativeLayout rl_delete;
    @BindView(R.id.rl_main1)
    RelativeLayout rl_main1;
    @BindView(R.id.rl_main2)
    RelativeLayout rl_main2;
    @BindView(R.id.rl_camera)
    RelativeLayout rl_camera;
    @BindView(R.id.rl_gallery)
    RelativeLayout rl_gallery;
    @BindView(R.id.tv_filename)
    TextView tv_filename;
    @BindView(R.id.tv_date_created)
    TextView tv_date_created;
    @BindView(R.id.img_doc)
    ImageView img_doc;
    @BindView(R.id.ll_delete)
    LinearLayout ll_delete;
    @BindView(R.id.btn_cancel)
    Button btn_cancel;
    @BindView(R.id.btn_delete)
    Button btn_delete;
    @BindView(R.id.ll_change_path)
    LinearLayout ll_change_path;
    @BindView(R.id.ll_rename)
    LinearLayout ll_rename;
    @BindView(R.id.et_rename)
    EditText et_rename;
    @BindView(R.id.btn_cancel_2)
    Button btn_cancel_2;
    @BindView(R.id.btn_save_2)
    Button btn_save_2;
    @BindView(R.id.rl_search)
    RelativeLayout rl_search;
    @BindView(R.id.search_file)
    SearchView search_file;
    @BindView(R.id.img_search)
    ImageView img_search;
    @BindView(R.id.et_name)
    EditText et_name;
    BottomSheetBehavior behavior;
    BottomSheetBehavior behavior1;
    BottomSheetBehavior behavior2;
    BottomSheetBehavior behavior3;
    Context context;
    private ArrayList<String> menuItems = new ArrayList<>();
    private ArrayList<Integer> menuimages = new ArrayList<>();
    private GridDocAdapter gridDocAdapter;
    private ListDocAdapter listDocAdapter;
    private int pageHeight;
    private int pageWidth;
    private int CHOOSE_FILE_REQUESTCODE = 100;
    private boolean isModification = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(MainActivity.this);
        setNavigationDrawerMenu();

        rv_grid_doc.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        gridDocAdapter = new GridDocAdapter(MainActivity.this, getAllPath());
        rv_grid_doc.setAdapter(gridDocAdapter);

        rv_list_doc.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
        listDocAdapter = new ListDocAdapter(MainActivity.this, getAllPath());
        rv_list_doc.setAdapter(listDocAdapter);

        img_grid.setOnClickListener(this);
        img_list.setOnClickListener(this);
        img_menu.setOnClickListener(this);
        rl_camera_1.setOnClickListener(this);
        rl_gallery_1.setOnClickListener(this);
        rl_save_as_pdf.setOnClickListener(this);
        rl_save_jpeg.setOnClickListener(this);
        rl_modify_scan.setOnClickListener(this);
        rl_rename.setOnClickListener(this);
        rl_print.setOnClickListener(this);
        rl_delete.setOnClickListener(this);
        rl_camera.setOnClickListener(this);
        rl_gallery.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_cancel_2.setOnClickListener(this);
        btn_save_2.setOnClickListener(this);
        img_search.setOnClickListener(this);

        if (getAllPath().size() == 0) {
            rl_main2.setVisibility(View.GONE);
            rl_main1.setVisibility(View.VISIBLE);
        } else {
            rl_main1.setVisibility(View.GONE);
            rl_main2.setVisibility(View.VISIBLE);
        }

        behavior = BottomSheetBehavior.from(ll_send);
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

        behavior1 = BottomSheetBehavior.from(ll_delete);
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

        behavior2 = BottomSheetBehavior.from(ll_change_path);
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

        behavior3 = BottomSheetBehavior.from(ll_rename);
        behavior3.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        EditText searchEditText = search_file.findViewById(androidx.appcompat.R.id.search_src_text);
        Typeface myFont = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            myFont = getResources().getFont(R.font.poppins_regular);
            searchEditText.setTypeface(myFont);
        }
        searchEditText.setTextColor(getResources().getColor(R.color.black));
        searchEditText.setHintTextColor(getResources().getColor(R.color.black));

        search_file.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                gridDocAdapter.getFilter().filter(query);
                listDocAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                gridDocAdapter.getFilter().filter(query);
                listDocAdapter.getFilter().filter(query);
                return false;
            }
        });

        adapterOnClick();
    }

    private void adapterOnClick() {
        gridDocAdapter.setClickListener((view, pos) -> {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            position = pos;

            File file = new File(getAllPath().get(pos));

            tv_filename.setText(getAllfolder().get(pos));
            et_rename.setText(getAllfolder().get(pos));
            Date lastModDate = new Date(file.lastModified());
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(lastModDate);

            tv_date_created.setText(formattedDate);

            if (getAllPath().get(position).contains("Documents")) {

                File directory = new File(getPdfAllfiles(getAllfolder().get(position)).get(0));
                Uri uri = Uri.parse("file://" + directory.getAbsolutePath());
                File file1 = new File(uri.getPath());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeFile(file1.getAbsolutePath(), options);

                Glide
                        .with(MainActivity.this)
                        .asBitmap()
                        .load(bitmap)
                        .into(img_doc);
            } else {
                File file1 = new File(getImgAllfiles(getAllfolder().get(position)).get(0));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeFile(file1.getAbsolutePath(), options);

                Glide
                        .with(MainActivity.this)
                        .asBitmap()
                        .load(bitmap)
                        .into(img_doc);
            }

        });

        listDocAdapter.setClickListener((view, pos) -> {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            position = pos;

            tv_filename.setText(getAllfolder().get(pos));
            et_rename.setText(getAllfolder().get(pos));

            File file = new File(getAllPath().get(pos));

            Date lastModDate = new Date(file.lastModified());
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(lastModDate);

            tv_date_created.setText(formattedDate);

            if (getAllPath().get(position).contains("Documents")) {

                File directory = new File(getPdfAllfiles(getAllfolder().get(position)).get(0));
                Uri uri = Uri.parse("file://" + directory.getAbsolutePath());
                File file1 = new File(uri.getPath());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeFile(file1.getAbsolutePath(), options);

                Glide
                        .with(MainActivity.this)
                        .asBitmap()
                        .load(bitmap)
                        .into(img_doc);

            } else {
                File file1 = new File(getImgAllfiles(getAllfolder().get(position)).get(0));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeFile(file1.getAbsolutePath(), options);

                Glide
                        .with(MainActivity.this)
                        .asBitmap()
                        .load(bitmap)
                        .into(img_doc);
            }


        });

    }

    private void deleteFolder(String path) {
        Log.e("LLLLLL_Path: ", path);
        File dir = new File(path);
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        runOnUiThread(() -> {
            if (getAllPath().size() == 0) {
                rl_main2.setVisibility(View.GONE);
                rl_main1.setVisibility(View.VISIBLE);
            } else {
                rl_main1.setVisibility(View.GONE);
                rl_main2.setVisibility(View.VISIBLE);
            }

            gridDocAdapter = new GridDocAdapter(MainActivity.this, getAllPath());
            rv_grid_doc.setAdapter(gridDocAdapter);

            listDocAdapter = new ListDocAdapter(MainActivity.this, getAllPath());
            rv_list_doc.setAdapter(listDocAdapter);

            adapterOnClick();
        });

    }


    private ArrayList<String> getAllfolder() {

        ArrayList<String> foldername = new ArrayList<>();
        File directory = new File(directoryPath + "/" + DOC_DIRECTORY);
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {

            for (File file : Objects.requireNonNull(files)) {
                if (file.listFiles().length > 0) {
                    File[] files1 = file.listFiles();
                    long fileSizeInBytes = files1[0].length();
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    long fileSizeInMB = fileSizeInKB / 1024;

                    Log.e("LLLLLL_SizeOfFile: ", String.valueOf(fileSizeInMB));

                    foldername.add(file.getName());
                }
            }
        }

        File directory1 = new File(directoryPath + "/" + SCAN_IMG_DIRECTORY);
        File[] files1 = directory1.listFiles();
        if (files1 != null && files1.length > 0) {
            for (File file : Objects.requireNonNull(files1)) {
                if (file.listFiles().length > 0) {
                    File[] files2 = file.listFiles();
                    long fileSizeInBytes = files2[0].length();
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    long fileSizeInMB = fileSizeInKB / 1024;

                    Log.e("LLLLLL_SizeOfFile: ", String.valueOf(fileSizeInMB));

                    foldername.add(file.getName());
                }
            }
        }

        return foldername;

    }


    private ArrayList<String> getAllPath() {
        ArrayList<String> pathList = new ArrayList<>();

        File directory = new File(directoryPath + "/" + DOC_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (File file : Objects.requireNonNull(files)) {
                if (file.listFiles().length > 0) {
                    File[] files1 = file.listFiles();
                    long fileSizeInBytes = files1[0].length();
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    long fileSizeInMB = fileSizeInKB / 1024;

                    Log.e("LLLLLL_SizeOfFile: ", String.valueOf(fileSizeInMB));

                    pathList.add(file.getAbsolutePath());
                }
            }
        }

        File directory1 = new File(directoryPath + "/" + SCAN_IMG_DIRECTORY);
        File[] files1 = directory1.listFiles();
        if (files1 != null && files1.length > 0) {
            for (File file : Objects.requireNonNull(files1)) {
                if (file.listFiles().length > 0) {
                    File[] files2 = file.listFiles();
                    long fileSizeInBytes = files2[0].length();
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    long fileSizeInMB = fileSizeInKB / 1024;

                    Log.e("LLLLLL_SizeOfFile: ", String.valueOf(fileSizeInMB));

                    pathList.add(file.getAbsolutePath());
                }
            }
        }

        return pathList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gridDocAdapter != null && rv_grid_doc != null)
            gridDocAdapter.notifyDataSetChanged();

        if (listDocAdapter != null && rv_list_doc != null)
            listDocAdapter.notifyDataSetChanged();
    }

    private void setNavigationDrawerMenu() {

        menuItems.add("Scans");
        menuItems.add("About Doc Scan");
        menuItems.add("Rate App");

        menuimages.add(R.drawable.select_camera);
        menuimages.add(R.drawable.app_info);
        menuimages.add(R.drawable.rate_app);

        nav_list.setAdapter(new DrawerAdapter(this, menuItems, menuimages));
        nav_list.setOnItemClickListener((parent, view, position, id) -> {

            switch (position) {
                case 0:
                    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                        drawer_layout.closeDrawer(GravityCompat.START);
                    }
                    break;
                case 1:
                    Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    launchMarket();
                    break;
                default:
                    break;
            }

            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void launchMarket() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else
            finishAffinity();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_menu:
                drawer_layout.openDrawer(GravityCompat.START);
                break;
            case R.id.img_list:
                if (rv_grid_doc.getVisibility() == View.VISIBLE) {
                    rv_grid_doc.setVisibility(View.GONE);
                    rv_list_doc.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.img_grid:
                if (rv_list_doc.getVisibility() == View.VISIBLE) {
                    rv_list_doc.setVisibility(View.GONE);
                    rv_grid_doc.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rl_camera_1:
            case R.id.rl_camera:
                isModification = false;
                startScan("Camera", false, new ArrayList<>(), "");
                break;
            case R.id.rl_gallery_1:
            case R.id.rl_gallery:
                isModification = false;
                startScan("Gallery", false, new ArrayList<>(), "");
                break;
            case R.id.rl_save_as_pdf:
                if (getAllPath().get(position).contains("Documents")) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    Toast.makeText(MainActivity.this, "This is already a PDF file...", Toast.LENGTH_LONG).show();
                } else {
                    try {

                        Document document = new Document();

                        String foldername = getFolderName();
                        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + DOC_DIRECTORY + "/" + foldername;

                        File file = new File(directoryPath);
                        if (!file.exists()) {
                            file.mkdirs();
                        }

                        PdfWriter.getInstance(document, new FileOutputStream(directoryPath + "/" + foldername + ".pdf")); //  Change pdf's name.

                        document.open();

                        for (int i = 0; i < getImgAllfiles(getAllfolder().get(position)).size(); i++) {
                            Log.e("LLLLL_JPEG_Path: ", getImgAllfiles(getAllfolder().get(position)).get(i));
                            try {
                                String filena = directoryPath + "/.img";
                                File file1 = new File(filena);

                                if (!file1.exists()) {
                                    file1.mkdirs();
                                }

                                copyFile(new File(Objects.requireNonNull(getImgAllfiles(getAllfolder().get(position)).get(i))), new File(filena + "/" + foldername + "_" + i + ".jpg"));
                            } catch (IOException e) {
                                Log.e("LLLLLL_E: ", Objects.requireNonNull(e.getMessage()));
                                e.printStackTrace();
                            }

                            com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(new File(Objects.requireNonNull(getImgAllfiles(getAllfolder().get(position)).get(i))).getAbsolutePath());  // Change image's name and extension.

                            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                                    - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                            image.scalePercent(scaler);
                            image.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER | com.itextpdf.text.Image.ALIGN_TOP);

                            document.add(image);
                        }

                        document.close();

                        System.gc();

                        runOnUiThread(() -> {
                            if (getAllPath().size() == 0) {
                                rl_main2.setVisibility(View.GONE);
                                rl_main1.setVisibility(View.VISIBLE);
                            } else {
                                rl_main1.setVisibility(View.GONE);
                                rl_main2.setVisibility(View.VISIBLE);
                            }
                            gridDocAdapter = new GridDocAdapter(MainActivity.this, getAllPath());
                            rv_grid_doc.setAdapter(gridDocAdapter);

                            listDocAdapter = new ListDocAdapter(MainActivity.this, getAllPath());
                            rv_list_doc.setAdapter(listDocAdapter);

                            adapterOnClick();
                        });

                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.rl_save_jpeg:
                if (getAllPath().get(position).contains("Documents")) {
                    String directoryPath1 = android.os.Environment.getExternalStorageDirectory().toString() + SCAN_IMG_DIRECTORY;
                    String foldername = getImgFolderName();

                    Log.e("LLLLLL_Folder: ", foldername);

                    File file = new File(directoryPath1 + "/" + foldername);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    for (int i = 0; i < getPdfAllfiles(getAllfolder().get(position)).size(); i++) {
                        try {
                            File directory = new File(getPdfAllfiles(getAllfolder().get(position)).get(i));
                            Uri uri = Uri.parse("file://" + directory.getAbsolutePath());

                            copyFile(new File(Objects.requireNonNull(uri.getPath())), new File(directoryPath1 + "/" + foldername + "/" + foldername + "_" + i + ".jpg"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    System.gc();

                    runOnUiThread(() -> {
                        if (getAllPath().size() == 0) {
                            rl_main2.setVisibility(View.GONE);
                            rl_main1.setVisibility(View.VISIBLE);
                        } else {
                            rl_main1.setVisibility(View.GONE);
                            rl_main2.setVisibility(View.VISIBLE);
                        }

                        gridDocAdapter = new GridDocAdapter(MainActivity.this, getAllPath());
                        rv_grid_doc.setAdapter(gridDocAdapter);

                        listDocAdapter = new ListDocAdapter(MainActivity.this, getAllPath());
                        rv_list_doc.setAdapter(listDocAdapter);

                        adapterOnClick();
                    });

                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    Toast.makeText(MainActivity.this, "This is already a Image file...", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.rl_modify_scan:
                ArrayList<Uri> uriList = new ArrayList<>();

                if (getAllPath().get(position).contains("Documents")) {
                    ArrayList<String> path = new ArrayList<>(getPdfAllfiles(getAllfolder().get(position)));
                    for (int i = 0; i < path.size(); i++) {
                        Uri uri = Uri.fromFile(new File(path.get(i)));
                        uriList.add(uri);
                    }
                } else {
                    ArrayList<String> path = new ArrayList<>(getImgAllfiles(getAllfolder().get(position)));
                    for (int i = 0; i < path.size(); i++) {
                        Uri uri = Uri.fromFile(new File(path.get(i)));
                        uriList.add(uri);
                    }
                }
                isModification = true;
                startScan("", true, uriList, getAllfolder().get(position));
                break;
            case R.id.rl_rename:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                behavior3.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.btn_cancel_2:
                behavior3.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.btn_save_2:
                behavior3.setState(BottomSheetBehavior.STATE_COLLAPSED);

                if (getAllPath().get(position).contains("Documents")) {
                    Log.e("LLLLLL_Foldername: ", getAllfolder().get(position));
                    File oldFolder = new File(directoryPath + "/" + DOC_DIRECTORY, getAllfolder().get(position));
                    File newFolder = new File(directoryPath + "/" + DOC_DIRECTORY, et_rename.getText().toString().trim());
                    boolean success = oldFolder.renameTo(newFolder);
                    Log.e("LLLLLL_Success: ", String.valueOf(success));
                } else {
                    Log.e("LLLLLL_Foldername: ", getAllfolder().get(position));
                    File oldFolder = new File(directoryPath + "/" + SCAN_IMG_DIRECTORY, getAllfolder().get(position));
                    File newFolder = new File(directoryPath + "/" + SCAN_IMG_DIRECTORY, et_rename.getText().toString().trim());
                    boolean success = oldFolder.renameTo(newFolder);
                    Log.e("LLLLLL_Success: ", String.valueOf(success));
                }
                runOnUiThread(() -> {
                    if (getAllPath().size() == 0) {
                        rl_main2.setVisibility(View.GONE);
                        rl_main1.setVisibility(View.VISIBLE);
                    } else {
                        rl_main1.setVisibility(View.GONE);
                        rl_main2.setVisibility(View.VISIBLE);
                    }

                    gridDocAdapter = new GridDocAdapter(MainActivity.this, getAllPath());
                    rv_grid_doc.setAdapter(gridDocAdapter);

                    listDocAdapter = new ListDocAdapter(MainActivity.this, getAllPath());
                    rv_list_doc.setAdapter(listDocAdapter);

                    adapterOnClick();
                });
                break;
            case R.id.rl_print:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Log.e("LLLLL_Pos: ", String.valueOf(position) + getPdfAllfiles(getAllfolder().get(position)));
                if (getAllPath().get(position).contains("Documents")) {

                    ArrayList<String> path = new ArrayList<>(getPdfAllfiles(getAllfolder().get(position)));

                    Log.e("LLLLLL+PathSize: ", String.valueOf(path.size()));
                    printDocument(path);

                } else {
                    ArrayList<String> path = new ArrayList<>(getImgAllfiles(getAllfolder().get(position)));

                    Log.e("LLLLLL_PathSize: ", String.valueOf(path.size()));
                    printDocument(path);

                }
                break;
            case R.id.rl_delete:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                behavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.btn_cancel:
                behavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.btn_delete:
                behavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (getAllPath().size() > 0)
                    deleteFolder(getAllPath().get(position));
                break;
            case R.id.img_search:
                if (rl_search.getVisibility() == View.VISIBLE) {
                    rl_search.setVisibility(View.GONE);
                } else {
                    rl_search.setVisibility(View.VISIBLE);
                }
                break;

        }

    }

    private String getFolderName() {

        String foldername = getAllfolder().get(position);
        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + DOC_DIRECTORY;
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains(getAllfolder().get(position))) {
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

        String foldername = getAllfolder().get(position);
        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + SCAN_IMG_DIRECTORY;
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains(getAllfolder().get(position))) {
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

    private void copyFile(File sourceFile, File destFile) throws IOException {
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

    private ArrayList<String> getImgAllfiles(String foldername) {
        ArrayList<String> filename = new ArrayList<>();
        Log.d("Files", "Path: " + directoryPath);
        File directory = new File(directoryPath + "/" + SCAN_IMG_DIRECTORY + "/" + foldername);

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                filename.add(file.getAbsolutePath());
                Log.d("LLLLL_PDF_Files", "FileName:" + file.getAbsolutePath());
            }
        }
        return filename;
    }

    private ArrayList<String> getPdfAllfiles(String foldername) {
        ArrayList<String> filename = new ArrayList<>();
        Log.d("Files", "Path: " + directoryPath);
        File directory = new File(directoryPath + "/" + DOC_DIRECTORY + "/" + foldername + "/" + ".img/");

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                filename.add(file.getAbsolutePath());
                Log.d("LLLLL_PDF_Files ", "FileName: " + file.getAbsolutePath());
            }
        }
        return filename;
    }

    protected void startScan(String preference, boolean isFrom, ArrayList<Uri> fileList, String Foldername) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("Pref", preference);
        intent.putExtra("from", isFrom);
        intent.putExtra("folderName", Foldername);
        intent.putExtra("isModification", isModification);
        intent.putParcelableArrayListExtra("fileList", fileList);
        startActivityForResult(intent, REQUEST_CODE);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);
            Image image = ImagePicker.getFirstImageOrNull(data);
            Log.e("LLLLL_image: ", image.getPath());
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CHOOSE_FILE_REQUESTCODE) {
            String PathHolder = data.getData().getPath();
            Toast.makeText(MainActivity.this, PathHolder, Toast.LENGTH_LONG).show();
        }

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_WRITE_PERMISSION);
        }
    }

    private boolean pageInRange(PageRange[] pageRanges, int page) {
        for (int i = 0; i < pageRanges.length; i++) {
            if ((page >= pageRanges[i].getStart()) &&
                    (page <= pageRanges[i].getEnd()))
                return true;
        }
        return false;
    }

    private void drawPage(PdfDocument.Page page,
                          int pagenumber, String Path) {

        File directory = new File(Path);

        Uri uri = Uri.parse("file://" + directory.getAbsolutePath());

        File file1 = new File(uri.getPath());

        Bitmap bitmap1 = BitmapFactory.decodeFile(file1.getAbsolutePath());

        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap1,
                page.getCanvas().getWidth(),
                page.getCanvas().getWidth(), true
        );

        Canvas canvas = page.getCanvas();

        canvas.drawBitmap(bitmap2, 0, 0, null);
    }

    public void printDocument(ArrayList<String> totalDoc) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        String jobName = getString(R.string.app_name) +
                " Document";

        printManager.print(jobName, new
                        MyPrintDocumentAdapter(MainActivity.this, totalDoc),
                null);
    }

    // Print PDF Documents
    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
        Context context;
        ArrayList<String> path;

        public MyPrintDocumentAdapter(Context context, ArrayList<String> path) {
            this.context = context;
            this.path = path;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes,
                             PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback,
                             Bundle metadata) {

            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            pageHeight =
                    newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
            pageWidth =
                    newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            if (path.size() > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("print_output.pdf").setContentType(
                        PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(path.size());

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }

        }

        @Override
        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal
                                    cancellationSignal,
                            final WriteResultCallback callback) {

            for (int i = 0; i < path.size(); i++) {
                if (pageInRange(pageRanges, i)) {
                    android.graphics.pdf.PdfDocument.PageInfo newPage = new android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth,
                            pageHeight, i).create();

                    PdfDocument.Page page =
                            myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }
                    drawPage(page, i, path.get(i));
                    myPdfDocument.finishPage(page);
                }
            }

            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pageRanges);

        }
    }


}