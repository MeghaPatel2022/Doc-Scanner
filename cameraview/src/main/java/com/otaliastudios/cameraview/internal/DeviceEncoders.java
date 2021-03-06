package com.otaliastudios.cameraview.internal;

import android.annotation.SuppressLint;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.size.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Checks the capabilities of device encoders and adjust parameters to ensure
 * that they'll be supported by the final encoder.
 * This can choose the encoder in two ways, based on the mode flag:
 *
 * 1. {@link #MODE_TAKE_FIRST}
 *
 * Chooses the encoder as the first one that matches the given mime type.
 * This is what {@link android.media.MediaCodec#createEncoderByType(String)} does,
 * and what {@link android.media.MediaRecorder} also does when recording.
 *
 * The list is ordered based on the encoder definitions in system/etc/media_codecs.xml,
 * as explained here: https://source.android.com/devices/media , for example.
 * So taking the first means respecting the vendor priorities and should generally be
 * a good idea.
 *
 * About {@link android.media.MediaRecorder}, we know it uses this option from here:
 * https://stackoverflow.com/q/57479564/4288782 where all links to source code are shown.
 * - StagefrightRecorder (https://android.googlesource.com/platform/frameworks/av/+/master/media/libmediaplayerservice/StagefrightRecorder.cpp#1782)
 * - MediaCodecSource (https://android.googlesource.com/platform/frameworks/av/+/master/media/libstagefright/MediaCodecSource.cpp#515)
 * - MediaCodecList (https://android.googlesource.com/platform/frameworks/av/+/master/media/libstagefright/MediaCodecList.cpp#322)
 *
 * To be fair, what {@link android.media.MediaRecorder} does is actually choose the first one
 * that configures itself without errors. We currently do not offer this option here. TODO
 *
 * 2. {@link #MODE_PREFER_HARDWARE}
 *
 * This takes the list - as ordered by the vendor - and just sorts it such that hardware encoders
 * are preferred over software ones. It's questionable whether this is good or not. Some vendors
 * might forget to put hardware encoders first in the list, some others might put poor hardware
 * encoders on the bottom of the list on purpose.
 */
public class DeviceEncoders {

    private final static String TAG = DeviceEncoders.class.getSimpleName();
    private final static CameraLogger LOG = CameraLogger.create(TAG);

    @VisibleForTesting static boolean ENABLED = Build.VERSION.SDK_INT >= 21;

    public final static int MODE_TAKE_FIRST = 0;
    public final static int MODE_PREFER_HARDWARE = 1;

    @SuppressWarnings("FieldCanBeLocal")
    private final MediaCodecInfo mVideoEncoder;
    @SuppressWarnings("FieldCanBeLocal")
    private final MediaCodecInfo mAudioEncoder;
    private final MediaCodecInfo.VideoCapabilities mVideoCapabilities;
    private final MediaCodecInfo.AudioCapabilities mAudioCapabilities;

    @SuppressLint("NewApi")
    public DeviceEncoders(@NonNull String videoType, @NonNull String audioType, int mode) {
        // We could still get a list of MediaCodecInfo for API >= 16, but it seems that the APIs
        // for querying the availability of a specified MediaFormat were only added in 21 anyway.
        if (ENABLED) {
            List<MediaCodecInfo> encoders = getDeviceEncoders();
            mVideoEncoder = findDeviceEncoder(encoders, videoType, mode);
            LOG.i("Enabled. Found video encoder:", mVideoEncoder.getName());
            mAudioEncoder = findDeviceEncoder(encoders, audioType, mode);
            LOG.i("Enabled. Found audio encoder:", mAudioEncoder.getName());
            mVideoCapabilities = mVideoEncoder.getCapabilitiesForType(videoType).getVideoCapabilities();
            mAudioCapabilities = mAudioEncoder.getCapabilitiesForType(audioType).getAudioCapabilities();
        } else {
            mVideoEncoder = null;
            mAudioEncoder = null;
            mVideoCapabilities = null;
            mAudioCapabilities = null;
            LOG.i("Disabled.");
        }
    }

    /**
     * Collects all the device encoders, which means excluding decoders.
     * @return encoders
     */
    @NonNull
    @SuppressLint("NewApi")
    @VisibleForTesting
    List<MediaCodecInfo> getDeviceEncoders() {
        ArrayList<MediaCodecInfo> results = new ArrayList<>();
        MediaCodecInfo[] array = new MediaCodecList(MediaCodecList.REGULAR_CODECS).getCodecInfos();
        for (MediaCodecInfo info : array) {
            if (info.isEncoder()) results.add(info);
        }
        return results;
    }

    /**
     * Whether an encoder is a hardware encoder or not. We don't have an API to check this,
     * but we can follow what libstagefright does:
     * https://android.googlesource.com/platform/frameworks/av/+/master/media/libstagefright/MediaCodecList.cpp#293
     *
     * @param encoder encoder
     * @return true if hardware
     */
    @SuppressLint("NewApi")
    @VisibleForTesting
    boolean isHardwareEncoder(@NonNull String encoder) {
        encoder = encoder.toLowerCase();
        boolean isSoftwareEncoder = encoder.startsWith("omx.google.")
                || encoder.startsWith("c2.android.")
                || (!encoder.startsWith("omx.") && !encoder.startsWith("c2."));
        return !isSoftwareEncoder;
    }

    /**
     * Finds the encoder we'll be using, depending on the given mode flag:
     * - {@link #MODE_TAKE_FIRST} will just take the first of the list
     * - {@link #MODE_PREFER_HARDWARE} will prefer hardware encoders
     * Throws if we find no encoder for this type.
     *
     * @param encoders encoders
     * @param mimeType mime type
     * @param mode mode
     * @return encoder
     */
    @SuppressLint("NewApi")
    @NonNull
    @VisibleForTesting
    MediaCodecInfo findDeviceEncoder(@NonNull List<MediaCodecInfo> encoders, @NonNull String mimeType, int mode) {
        ArrayList<MediaCodecInfo> results = new ArrayList<>();
        for (MediaCodecInfo encoder : encoders) {
            String[] types = encoder.getSupportedTypes();
            for (String type : types) {
                if (type.equalsIgnoreCase(mimeType)) {
                    results.add(encoder);
                    break;
                }
            }
        }
        LOG.i("findDeviceEncoder -", "type:", mimeType, "encoders:", results.size());
        if (mode == MODE_PREFER_HARDWARE) {
            Collections.sort(results, new Comparator<MediaCodecInfo>() {
                @Override
                public int compare(MediaCodecInfo o1, MediaCodecInfo o2) {
                    boolean hw1 = isHardwareEncoder(o1.getName());
                    boolean hw2 = isHardwareEncoder(o2.getName());
                    if (hw1 && hw2) return 0;
                    if (hw1) return -1;
                    if (hw2) return 1;
                    return 0;
                }
            });
        }
        if (results.isEmpty()) {
            throw new RuntimeException("No encoders for type:" + mimeType);
        }
        return results.get(0);
    }

    /**
     * Returns a video size supported by the device encoders.
     * Throws if input width or height are out of the supported boundaries.
     *
     * @param size input size
     * @return adjusted size
     */
    @SuppressLint("NewApi")
    @NonNull
    public Size getSupportedVideoSize(@NonNull Size size) {
        if (!ENABLED) return size;
        int width = size.getWidth();
        int height = size.getHeight();
        double aspect = (double) width / height;

        // If width is too large, scale down, but keep aspect ratio.
        if (mVideoCapabilities.getSupportedWidths().getUpper() < width) {
            width = mVideoCapabilities.getSupportedWidths().getUpper();
            height = (int) Math.round(width / aspect);
        }

        // If height is too large, scale down, but keep aspect ratio.
        if (mVideoCapabilities.getSupportedHeights().getUpper() < height) {
            height = mVideoCapabilities.getSupportedHeights().getUpper();
            width = (int) Math.round(aspect * height);
        }

        // Adjust the alignment.
        while (width % mVideoCapabilities.getWidthAlignment() != 0) width--;
        while (height % mVideoCapabilities.getHeightAlignment() != 0) height--;

        // It's still possible that we're BELOW the lower.
        if (!mVideoCapabilities.getSupportedWidths().contains(width)) {
            throw new RuntimeException("Width not supported after adjustment." +
                    " Desired:" + width +
                    " Range:" + mVideoCapabilities.getSupportedWidths());
        }
        if (!mVideoCapabilities.getSupportedHeights().contains(height)) {
            throw new RuntimeException("Height not supported after adjustment." +
                    " Desired:" + height +
                    " Range:" + mVideoCapabilities.getSupportedHeights());
        }

        // It's still possible that we're unsupported for other reasons.
        if (!mVideoCapabilities.isSizeSupported(width, height)) {
            throw new RuntimeException("Size not supported for unknown reason." +
                    " Might be an aspect ratio issue." +
                    " Desired size:" + new Size(width, height));
        }
        Size adjusted = new Size(width, height);
        LOG.i("getSupportedVideoSize -", "inputSize:", size, "adjustedSize:", adjusted);
        return adjusted;
    }

    /**
     * Returns a video bit rate supported by the device encoders.
     * This means adjusting the input bit rate if needed, to match encoder constraints.
     *
     * @param bitRate input rate
     * @return adjusted rate
     */
    @SuppressLint("NewApi")
    public int getSupportedVideoBitRate(int bitRate) {
        if (!ENABLED) return bitRate;
        int newBitRate = mVideoCapabilities.getBitrateRange().clamp(bitRate);
        LOG.i("getSupportedVideoBitRate -", "inputRate:", bitRate, "adjustedRate:", newBitRate);
        return newBitRate;
    }

    /**
     * Returns a video frame rate supported by the device encoders.
     * This means adjusting the input frame rate if needed, to match encoder constraints.
     *
     * @param frameRate input rate
     * @return adjusted rate
     */
    @SuppressLint("NewApi")
    public int getSupportedVideoFrameRate(@NonNull Size size, int frameRate) {
        if (!ENABLED) return frameRate;
        int newFrameRate = (int) (double) mVideoCapabilities
                .getSupportedFrameRatesFor(size.getWidth(), size.getHeight())
                .clamp((double) frameRate);
        LOG.i("getSupportedVideoFrameRate -", "inputRate:", frameRate, "adjustedRate:", newFrameRate);
        return newFrameRate;
    }

    /**
     * Returns an audio bit rate supported by the device encoders.
     * This means adjusting the input bit rate if needed, to match encoder constraints.
     *
     * @param bitRate input rate
     * @return adjusted rate
     */
    @SuppressLint("NewApi")
    public int getSupportedAudioBitRate(int bitRate) {
        if (!ENABLED) return bitRate;
        int newBitRate = mAudioCapabilities.getBitrateRange().clamp(bitRate);
        LOG.i("getSupportedAudioBitRate -", "inputRate:", bitRate, "adjustedRate:", newBitRate);
        return newBitRate;
    }


    // Won't do this for audio sample rate. As far as I remember, the value we're using,
    // 44.1kHz, is guaranteed to be available, and it's not configurable.

    /**
     * Returns the name of the video encoder if we were able to determine one.
     * @return encoder name
     */
    @SuppressLint("NewApi")
    @Nullable
    public String getVideoEncoder() {
        if (mVideoEncoder != null) {
            return mVideoEncoder.getName();
        } else {
            return null;
        }
    }

    /**
     * Returns the name of the audio encoder if we were able to determine one.
     * @return encoder name
     */
    @SuppressLint("NewApi")
    @Nullable
    public String getAudioEncoder() {
        if (mAudioEncoder != null) {
            return mAudioEncoder.getName();
        } else {
            return null;
        }
    }

}
