package com.codedevs.camscanner.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codedevs.camscanner.Activity.ImageShownActivity;
import com.codedevs.camscanner.R;
import com.codedevs.camscanner.Utils.Pref;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListDocAdapter extends RecyclerView.Adapter<ListDocAdapter.MyClassView> implements Filterable {

    final static Pattern PATTERN = Pattern.compile("(.*?)(?:\\((\\d+)\\))?(\\.[^.]*)?");
    private static final String DOC_DIRECTORY = "/Doc Scan/Documents";
    private static final String SHARE_DIRECTORY = "/Doc Scan/Share";
    private static final String SCAN_IMG_DIRECTORY = "/Doc Scan/ScannedImage";
    private static final String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();
    private OnItemClickListener listener;
    private Activity activity;
    private ArrayList<String> imgfoldername;
    private ArrayList<String> filterList = new ArrayList<>();

    public ListDocAdapter(Activity activity, ArrayList<String> imgfoldername) {
        this.activity = activity;
        this.imgfoldername = imgfoldername;
        this.filterList = imgfoldername;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    imgfoldername = filterList;
                } else {
                    ArrayList<String> filteredList = new ArrayList<>();
                    for (String row : imgfoldername) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String foldername = row.substring(row.lastIndexOf("/") + 1);
                        if (foldername.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    imgfoldername = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = imgfoldername;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                imgfoldername = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_doc, parent, false);
        return new MyClassView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {

        String foldername = imgfoldername.get(position).substring(imgfoldername.get(position).lastIndexOf("/") + 1);

        if (imgfoldername.get(position).contains("Documents")) {

            if (getAllfiles(foldername).size() > 0) {

                File directory = new File(directoryPath + "/" + DOC_DIRECTORY + "/" + foldername + "/" + ".img/" + getAllfiles(foldername).get(0));

                Uri uri = Uri.parse("file://" + directory.getAbsolutePath());

                File file1 = new File(uri.getPath());

                Bitmap bitmap = BitmapFactory.decodeFile(file1.getAbsolutePath());

                Glide
                        .with(activity)
                        .asBitmap()
                        .load(bitmap)
                        .into(holder.img_doc);
            }

        } else {

            if (getImgAllfiles(foldername).size() > 0) {

                File file1 = new File(directoryPath + "/" + SCAN_IMG_DIRECTORY + "/" + foldername + "/" + getImgAllfiles(foldername).get(0));

                Bitmap bitmap = BitmapFactory.decodeFile(file1.getAbsolutePath());

                Glide
                        .with(activity)
                        .asBitmap()
                        .load(bitmap)
                        .into(holder.img_doc);
            }
        }

        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imgfoldername.get(position).contains("Documents")) {
                    String foldername = imgfoldername.get(position).substring(imgfoldername.get(position).lastIndexOf("/") + 1);
                    Log.e("LLLLL_URI: ", imgfoldername.get(position) + "/" + foldername + ".pdf" + "    : "+v.getContext().getPackageName() + ".provider");
                    File outputFile = new File(imgfoldername.get(position) + "/" + foldername + ".pdf");
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    shareIntent.setType("application/pdf");
                    Uri fileUri = FileProvider.getUriForFile(activity.getApplicationContext(), v.getContext().getPackageName() + ".provider", outputFile);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        activity.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    activity.startActivity(Intent.createChooser(shareIntent, "Share it"));
                } else {

                    try {

                        Document document = new Document();

                        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + SHARE_DIRECTORY + "/" + foldername;

                        File file = new File(directoryPath);
                        if (!file.exists()) {
                            file.mkdirs();
                        }

                        PdfWriter.getInstance(document, new FileOutputStream(directoryPath + "/" + foldername + ".pdf")); //  Change pdf's name.

                        document.open();

                        for (int i = 0; i < getImgAllfiles(foldername).size(); i++) {

                            Log.e("LLLLLL_File: ", getImgAllfiles(foldername).get(0) + "       ");
                            com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(android.os.Environment.getExternalStorageDirectory().toString() + "/" + SCAN_IMG_DIRECTORY + "/" + foldername + "/" + getImgAllfiles(foldername).get(i));  // Change image's name and extension.

                            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                                    - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                            image.scalePercent(scaler);
                            image.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER | com.itextpdf.text.Image.ALIGN_TOP);

                            document.add(image);
                        }

                        document.close();

                        System.gc();

                        File outputFile = new File(directoryPath + "/" + foldername + ".pdf");
                        String foldername = imgfoldername.get(position).substring(imgfoldername.get(position).lastIndexOf("/") + 1);
                        Log.e("LLLLL_URI: ", imgfoldername.get(position) + "/" + foldername + ".pdf" + "    : "+v.getContext().getPackageName() + ".provider");
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        shareIntent.setType("application/pdf");
                        Uri fileUri = FileProvider.getUriForFile(activity.getApplicationContext(), v.getContext().getPackageName() + ".provider", outputFile);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            activity.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                        activity.startActivity(Intent.createChooser(shareIntent, "Share it"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foldername = imgfoldername.get(position).substring(imgfoldername.get(position).lastIndexOf("/") + 1);

                if (imgfoldername.get(position).contains("Documents")) {
                    Log.e("LLLLL_URI: ", imgfoldername.get(position) + "/" + foldername + ".pdf" + "    : "+v.getContext().getPackageName() + ".provider");
                    File outputFile = new File(imgfoldername.get(position) + "/" + foldername + ".pdf");
                    Pref.openDocument(activity,outputFile.getAbsolutePath());
                } else {
                    Intent intent = new Intent(activity, ImageShownActivity.class);
                    intent.putExtra("list", new File(imgfoldername.get(position)).getAbsolutePath());
                    activity.startActivity(intent);
                }
            }
        });

        holder.tv_name.setText(foldername);

        File file = new File(imgfoldername.get(position));

        Date lastModDate = new Date(file.lastModified());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
        String formattedDate = df.format(lastModDate);

        holder.tv_date.setText(formattedDate);

        holder.img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, position);
            }
        });
    }

    private String getFolderName(String foldername) {

        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + SHARE_DIRECTORY;
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains(foldername)) {
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

    private boolean fileDocExists(String fileName) {

        boolean isExist = false;
        String directoryPath = android.os.Environment.getExternalStorageDirectory().toString() + SHARE_DIRECTORY;
        File file = new File(directoryPath + "/" + fileName + ".pdf");
        if (file.exists()) {
            isExist = true;
        }

        return isExist;
    }

    @Override
    public int getItemCount() {
        return imgfoldername.size();
    }

    private ArrayList<String> getAllfiles(String foldername) {
        ArrayList<String> filename = new ArrayList<>();
        Log.d("Files", "Path: " + directoryPath);
        File directory = new File(directoryPath + "/" + DOC_DIRECTORY + "/" + foldername + "/" + ".img");

        Uri uri = Uri.parse("file://" + directory.getAbsolutePath());

//        Log.e("LLLLLL_DIR: ", uri.getPath());
        File[] files = new File(uri.getPath()).listFiles();
        if (files != null && files.length > 0) {
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                filename.add(files[i].getName());
                Log.d("Files", "FileName:" + files[i].getName());
            }
        }
        return filename;
    }

    private ArrayList<String> getImgAllfiles(String foldername) {
        ArrayList<String> filename = new ArrayList<>();
        Log.d("Files", "Path: " + directoryPath);
        File directory = new File(directoryPath + "/" + SCAN_IMG_DIRECTORY + "/" + foldername);

        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                filename.add(files[i].getName());
                Log.d("Files", "FileName:" + files[i].getName());
            }
        }
        return filename;
    }

    private ArrayList<String> getShareAllfiles(String foldername) {
        ArrayList<String> filename = new ArrayList<>();
        Log.d("Files", "Path: " + directoryPath);
        File directory = new File(directoryPath + "/" + SHARE_DIRECTORY + "/" + foldername);

        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                filename.add(files[i].getName());
                Log.d("Files", "FileName:" + files[i].getName());
            }
        }
        return filename;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    public class MyClassView extends RecyclerView.ViewHolder {
        ImageView img_more, img_doc, img_share;
        TextView tv_name, tv_date;

        public MyClassView(@NonNull View itemView) {
            super(itemView);
            img_more = itemView.findViewById(R.id.img_more);

            img_doc = itemView.findViewById(R.id.img_doc);
            img_share = itemView.findViewById(R.id.img_share);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
