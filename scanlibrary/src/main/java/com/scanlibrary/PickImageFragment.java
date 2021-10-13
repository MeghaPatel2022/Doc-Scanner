                                                                                                                                                                                                                                                                                    package com.scanlibrary;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FaceDetection.FaceOverlayView;
import com.otaliastudios.cameraview.FaceDetection.FaceUtils;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Engine;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.controls.Preview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.os.Build.VERSION.SDK_INT;
import static com.scanlibrary.Const.finalList;
import static com.scanlibrary.ScanActivity.isGallery;

                                                                                                                                                                                                                                                                                    /**
 * Created by jhansi on 04/04/15.
 */
public class PickImageFragment extends Fragment implements SurfaceHolder.Callback, View.OnClickListener {

    private final static CameraLogger LOG = CameraLogger.create("Doc ScannedApp");

    CameraView camera;
    ImageView img_camera, img_gallery;
    ImageView img_flash;

    ImageView img_last_scanned;
    TextView tv_scanned_img_size;

    private Camera mCamera;
    private long mCaptureTime;
    boolean flag = false, flash_flag = false;
    private static final String IMAGE_DIRECTORY = "/Doc Scan/Capture";

    FaceOverlayView mFaceView;
    private int mDisplayRotation;
    private int mDisplayOrientation;
    private IScanner scanner;
    private Camera.FaceDetectionListener faceDetectionListener = (faces, camera) -> {
        Log.e("LLLL_onFaceDetection", "Number of Faces:" + faces.length);
        // Update the view now!
        mFaceView.setFaces(faces);
    };

    private View view;
    private Uri fileUri;

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
        boolean isGallery = getArguments().getBoolean(ScanConstants.ISGALLERY);
        if (isGallery) {
            ImagePicker.create(getActivity())
                    .language("in") // Set image picker language
                    .theme(R.style.ImagePickerTheme)
                    .includeVideo(false) // include video (false by default)
                    .onlyVideo(false) // include video (false by default)
                    .folderMode(false)
                    .multi()
                    .limit(30 - finalList.size())
                    .showCamera(false)
                    .toolbarArrowColor(Color.BLACK) // set toolbar arrow up color
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Select Pictures") // image selection title
                    .toolbarDoneButtonText("DONE")
                    .start(); // done button text
        } else {
            view = inflater.inflate(R.layout.pick_image_fragment, null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                init();
            }
        }
        return view;
    }

    private void init() {

        camera = view.findViewById(R.id.camera);
        img_camera = view.findViewById(R.id.img_camera);
        img_gallery = view.findViewById(R.id.img_gallery);
        img_flash = view.findViewById(R.id.img_flash);

        img_last_scanned = view.findViewById(R.id.img_last_scanned);
        tv_scanned_img_size = view.findViewById(R.id.tv_scanned_img_size);

        if (SDK_INT >= 23) {
            camera.setEngine(Engine.CAMERA2);
        } else
            camera.setEngine(Engine.CAMERA1);

        camera.setLifecycleOwner((LifecycleOwner) getActivity());
        camera.addCameraListener(new Listener());
        camera.setFacing(Facing.BACK);

        if (sharedpreference.getFlash(getActivity()).equals("OFF")) {
            img_flash.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.flash_off));
            camera.setFlash(Flash.OFF);
        } else if (sharedpreference.getFlash(getActivity()).equals("ON")) {
            camera.setFlash(Flash.ON);
            img_flash.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.flash_on));
        }

        // OnClick
        img_camera.setOnClickListener(this);
        img_flash.setOnClickListener(this);
        img_gallery.setOnClickListener(this);
        img_last_scanned.setOnClickListener(this);

        if (finalList.size() > 0) {
            try {
                img_last_scanned.setImageBitmap(getBitmap(finalList.get(finalList.size() - 1)));
                tv_scanned_img_size.setText(Integer.toString(finalList.size()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            tv_scanned_img_size.setVisibility(View.GONE);
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!isGallery) {
                        Intent intent = null;
                        try {
                            intent = new Intent(getActivity(), Class.forName("com.codedevs.camscanner.Activity.MainActivity"));
                            startActivity(intent);
                            getActivity().finish();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    isGallery = false;
                    return true;
                }
                return false;
            }
        });
    }

    private void clearTempImages() {
        try {
            File tempFolder = new File(ScanConstants.IMAGE_PATH);
            for (File f : tempFolder.listFiles())
                f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getIntentPreference() {
        int preference = getArguments().getInt(ScanConstants.OPEN_INTENT_PREFERENCE, 0);
        return preference;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_camera) {
            isGallery = false;
            camera.setMode(Mode.PICTURE);
            capturePictureSnapshot();
        } else if (v.getId() == R.id.img_gallery) {
            ImagePicker.create(getActivity())
                    .language("in") // Set image picker language
                    .theme(R.style.ImagePickerTheme)
                    .includeVideo(false) // include video (false by default)
                    .onlyVideo(false) // include video (false by default)
                    .folderMode(false)
                    .multi()
                    .limit(30 - finalList.size())
                    .showCamera(false)
                    .toolbarArrowColor(Color.BLACK) // set toolbar arrow up color
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Select Pictures") // image selection title
                    .toolbarDoneButtonText("DONE")
                    .start(); // done button text
        } else if (v.getId() == R.id.img_last_scanned) {
            if (finalList.size() > 0) {
                Const.position = 0;
                scanner.onBitmapSelect(finalList.get(0));
            } else
                Toast.makeText(getActivity(), "Please select at least 1 image...", Toast.LENGTH_LONG).show();
        } else if (v.getId() == R.id.img_flash) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getActivity().runOnUiThread(() -> {
                    if (camera.getFlash() == Flash.ON) {
                        sharedpreference.setFlash(getContext(), "OFF");
                        img_flash.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.flash_off));
                        camera.setFlash(Flash.OFF);
                    } else if (camera.getFlash() == Flash.OFF) {
                        sharedpreference.setFlash(getContext(), "ON");
                        img_flash.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.flash_on));
                        camera.setFlash(Flash.ON);

                    }
                });

            }
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open(1);
        mCamera.setFaceDetectionListener(faceDetectionListener);
        mCamera.startFaceDetection();
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            Log.e("LLLLLLL", "Could not preview the image.", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // We have no surface, return immediately:
        if (holder.getSurface() == null) {
            return;
        }
        // Try to stop the current preview:
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // Ignore...
        }
        // Get the supported preview sizes:
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = previewSizes.get(0);
        // And set them:
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        mCamera.setParameters(parameters);
        // Now set the display orientation for the camera. Can we do this differently?
        mDisplayRotation = FaceUtils.getDisplayRotation(getActivity());
        mDisplayOrientation = FaceUtils.getDisplayOrientation(mDisplayRotation, 0);
        mCamera.setDisplayOrientation(mDisplayOrientation);

        if (mFaceView != null) {
            mFaceView.setDisplayOrientation(mDisplayOrientation);
        }

        // Finally start the camera preview again:
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.setPreviewCallback(null);
        mCamera.setFaceDetectionListener(null);
        mCamera.setErrorCallback(null);
        mCamera.release();
        mCamera = null;
    }


    public void openMediaContent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE);
    }

    public void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = createImageFile();
        boolean isDirectoryCreated = file.getParentFile().mkdirs();
        Log.d("", "openCamera: isDirectoryCreated: " + isDirectoryCreated);
        Uri tempFileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tempFileUri = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                    "com.codedevs.camscanner.provider", // As defined in Manifest
                    file);
        } else {
            tempFileUri = Uri.fromFile(file);
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
        startActivityForResult(cameraIntent, ScanConstants.START_CAMERA_REQUEST_CODE);
    }

    private File createImageFile() {
        clearTempImages();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
                Date());
        File file = new File(ScanConstants.IMAGE_PATH, "IMG_" + timeStamp +
                ".jpg");
        fileUri = Uri.fromFile(file);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("", "onActivityResult" + resultCode);
        Bitmap bitmap = null;
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);
            // or get a single image only
            for (int i = 0; i < images.size(); i++) {
                finalList.add(images.get(i).getUri());
            }

            scanner.onScanFinish(finalList,false,"",true);
        }
        if (resultCode == Activity.RESULT_OK) {
            try {
                switch (requestCode) {
                    case ScanConstants.START_CAMERA_REQUEST_CODE:
                        bitmap = getBitmap(fileUri);
                        break;

                    case ScanConstants.PICKFILE_REQUEST_CODE:
                        bitmap = getBitmap(data.getData());
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            getActivity().finish();
        }
    }

    private class Listener extends CameraListener {

        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {
            Log.d("mn13option", options.toString());

            AnimatorSet set;
            camera.setCameraDistance(10 * camera.getWidth());
//            set = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), com.otaliastudios.cameraview.R.anim.flipping);
//            set.setTarget(camera);
//            set.setDuration(500);
//            set.start();

            int cameraid = 0;
            if (camera.getFacing() == Facing.BACK) {
                cameraid = 0;
            } else if (camera.getFacing() == Facing.FRONT) {
                cameraid = 1;
            }

            Log.d("mn13camera", cameraid + ":");

            if (!flag) {
                flag = true;
            }
        }

        @Override
        public void onOrientationChanged(int orientation) {
            super.onOrientationChanged(orientation);
            Log.d("mn13option1", orientation + "");

        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
            message("Got CameraException #" + exception.getReason(), true);
        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            long callbackTime = System.currentTimeMillis();
            if (mCaptureTime == 0) mCaptureTime = callbackTime - 300;
            LOG.w("onPictureTaken called! Launching activity. Delay:", callbackTime - mCaptureTime);

            String filePath = "";
            FileOutputStream outStream;
            try {
                File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
                if (!wallpaperDirectory.exists()) {
                    wallpaperDirectory.mkdirs();
                }

                try {
                    File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
                    f.createNewFile();
                    f.getAbsoluteFile();
                    //selectedimage = f;
                    Log.e("selectedfile", f + "");
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(result.getData());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        MediaScannerConnection.scanFile(getContext(), new String[]{f.getPath()}, new String[]{"image/jpeg"}, null);
                    }
                    fo.close();
                    filePath = f.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                    //    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                MediaActionSound sound = new MediaActionSound();
//                sound.play(MediaActionSound.SHUTTER_CLICK);

                //Toast.makeText(OpenCamera.this, "Picture Saved: " + "", Toast.LENGTH_LONG).show();

                Log.e("LLLL_filepath: ", filePath);

                postImagePick(getBitmap(Uri.fromFile(new File(filePath))), filePath);


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("LLLLL_Exc: ", Objects.requireNonNull(e.getLocalizedMessage()));
            }

        }

        protected void postImagePick(Bitmap bitmap, String filepath) {
//            Uri uri = FileProvider.getUriForFile(getActivity(), "com.codedevs.camscanner" + ".provider", new File(filepath));

            Uri uri = Utils.getUri(getActivity(), bitmap);

            bitmap.recycle();
            finalList.add(uri);
            if (finalList.size() > 0) {
                try {
                    img_last_scanned.setImageBitmap(getBitmap(finalList.get(finalList.size() - 1)));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_scanned_img_size.setVisibility(View.VISIBLE);
                            tv_scanned_img_size.setText(Integer.toString(finalList.size()));
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                tv_scanned_img_size.setVisibility(View.GONE);
            }
//            scanner.onBitmapSelect(uri);
        }

        @Override
        public void onVideoTaken(@NonNull VideoResult result) {
            super.onVideoTaken(result);
            LOG.w("onVideoTaken called! Launched activity.");
        }

        @Override
        public void onVideoRecordingStart() {
            super.onVideoRecordingStart();
            Log.e("LLLLLLL_Video: ", "start");
            LOG.w("onVideoRecordingStart!");
        }

        @Override
        public void onVideoRecordingEnd() {
            super.onVideoRecordingEnd();
            Log.e("LLLLLLL_Video: ", "end");
            message("Video taken. Processing...", false);
            LOG.w("onVideoRecordingEnd!");
        }

        @Override
        public void onExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers);
            message("Exposure correction:" + newValue, false);
        }


        @Override
        public void onZoomChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onZoomChanged(newValue, bounds, fingers);
            message("Zoom:" + newValue, false);
        }

    }

    private void capturePictureSnapshot() {
        if (camera.isTakingPicture()) return;
        if (camera.getPreview() != Preview.GL_SURFACE) {
            message("Picture snapshots are only allowed with the GL_SURFACE preview.", true);
            return;
        }
        mCaptureTime = System.currentTimeMillis();
        message("Capturing picture snapshot...", false);
        if (sharedpreference.getFlash(getActivity()).equalsIgnoreCase(String.valueOf(Flash.OFF))) {
            camera.setFlash(Flash.OFF);
        } else if (sharedpreference.getFlash(getActivity()).equalsIgnoreCase(String.valueOf(Flash.AUTO))) {
            camera.setFlash(Flash.AUTO);
        } else
            camera.setFlash(Flash.ON);

        camera.setPictureMetering(true);
        camera.takePictureSnapshot();
    }

    private void message(@NonNull String content, boolean important) {
        if (important) {
            LOG.w(content);
        } else {
            LOG.e(content);
        }
    }

    private Bitmap getBitmap(Uri selectedimg) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        AssetFileDescriptor fileDescriptor = null;
        fileDescriptor =
                getActivity().getContentResolver().openAssetFileDescriptor(selectedimg, "r");
        Bitmap original
                = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
        return original;
    }

}