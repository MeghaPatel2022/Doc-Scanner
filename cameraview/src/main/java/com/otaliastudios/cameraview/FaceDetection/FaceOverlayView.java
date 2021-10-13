package com.otaliastudios.cameraview.FaceDetection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.camera2.params.Face;
import android.os.Build;
import android.util.Log;
import android.view.View;

import static android.content.ContentValues.TAG;
import static android.util.Config.LOGV;

public class FaceOverlayView extends View {

    private Paint mPaint;
    private Paint mTextPaint;
    private int mDisplayOrientation;
    private int mOrientation;
    private Camera.Face[] mFaces;

    public FaceOverlayView(Context context, Face[] faces) {
        super(context);
//        setFaces(faces);
        initialize();

    }

    public FaceOverlayView(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        // We want a green box around the face:
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(128);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(Color.GREEN);
        mTextPaint.setStyle(Paint.Style.FILL);
    }

//    public void setFaces(Face[] faces) {
//        mFaces = faces;
//        invalidate();
//    }

    public void setFaces(Camera.Face[] faces) {
        mFaces = faces;
    }


    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setDisplayOrientation(int displayOrientation) {
        mDisplayOrientation = displayOrientation;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.e("LLLL_mn13helloworld", "canvas");
        canvas.save();
//        canvas.drawText("Score", 100, 100, mTextPaint);
        if (mFaces != null && mFaces.length > 0) {
//            canvas.drawText("Score", 50, 50, mTextPaint);
            Log.e("LLLL_mn13helloworld", "canvas");
            Matrix matrix = new Matrix();
            FaceUtils.prepareMatrix(matrix, false, mDisplayOrientation, getWidth(), getHeight());
            canvas.save();
            matrix.postRotate(mOrientation);
            canvas.rotate(-mOrientation);
            RectF rectF = new RectF();
            for (Camera.Face face : mFaces) {

                Log.e("LLLL_faces_data: ", face.mouth + "      " + face.leftEye);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    rectF.set(face.);
                    matrix.mapRect(rectF);
                    canvas.drawRect(rectF, mPaint);
//                    Log.e("LLLLL_Face point;", "" + face.getScore() + "   " + face.getMouthPosition().x + "   " + 50 + mTextPaint);
//                    canvas.drawText("Score " + face.getScore(), face.getMouthPosition().x, 50, mTextPaint);
                    Camera.Face detectedFace = face;
                    if (detectedFace != null) {

//                        int canvasWidth = canvas.getWidth();
//                        int canvasHeight = canvas.getHeight();
//                        int faceWidthOffset = detectedFace.getBounds().width() / 8;
//                        int faceHeightOffset = detectedFace.getBounds().height() / 8;
//
//                        canvas.save();
//                        /*canvas.rotate(360 - mOrientation, canvasWidth / 2,
//                                canvasHeight / 2);*/
//
//                        int l = detectedFace.getBounds().right;
//                        int t = detectedFace.getBounds().bottom;
//                        int r = detectedFace.getBounds().left;
//                        int b = detectedFace.getBounds().top;
//                        int left = (canvasWidth - (canvasWidth * l) / canvasWidth) - (faceWidthOffset);
//                        int top = (canvasHeight * t) / canvasHeight - (faceHeightOffset);
//                        int right = (canvasWidth - (canvasWidth * r) / canvasWidth) + (faceWidthOffset);
//                        int bottom = (canvasHeight * b) / canvasHeight + (faceHeightOffset);
//
//                        canvas.drawRect(left, top, right, bottom, mPaint);
//                        canvas.restore();
                    }

                }

            }
            canvas.restore();
        }
    }


}
