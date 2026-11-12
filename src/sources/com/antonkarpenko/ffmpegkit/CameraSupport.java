package com.antonkarpenko.ffmpegkit;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
class CameraSupport {
    CameraSupport() {
    }

    static List<String> extractSupportedCameraIds(Context context) {
        List<String> detectedCameraIdList = new ArrayList<>();
        try {
            CameraManager manager = (CameraManager) context.getSystemService("camera");
            if (manager != null) {
                String[] cameraIdList = manager.getCameraIdList();
                for (String cameraId : cameraIdList) {
                    CameraCharacteristics chars = manager.getCameraCharacteristics(cameraId);
                    Integer cameraSupport = (Integer) chars.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    if (cameraSupport != null && cameraSupport.intValue() == 2) {
                        android.util.Log.d("ffmpeg-kit", "Detected camera with id " + cameraId + " has LEGACY hardware level which is not supported by Android Camera2 NDK API.");
                    } else if (cameraSupport != null) {
                        detectedCameraIdList.add(cameraId);
                    }
                }
            }
        } catch (CameraAccessException e) {
            android.util.Log.w("ffmpeg-kit", "Detecting camera ids failed.", e);
        }
        return detectedCameraIdList;
    }
}
