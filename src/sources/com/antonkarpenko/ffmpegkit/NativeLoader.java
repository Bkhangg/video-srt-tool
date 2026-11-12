package com.antonkarpenko.ffmpegkit;

import android.os.Build;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class NativeLoader {
    static final String[] FFMPEG_LIBRARIES = {"avutil", "swscale", "swresample", "avcodec", "avformat", "avfilter", "avdevice"};
    static final String[] LIBRARIES_LINKED_WITH_CXX = {"chromaprint", "openh264", "rubberband", "snappy", "srt", "tesseract", "x265", "zimg", "libilbc"};

    static boolean isTestModeDisabled() {
        return System.getProperty("enable.ffmpeg.kit.test.mode") == null;
    }

    private static void loadLibrary(String libraryName) {
        if (isTestModeDisabled()) {
            try {
                System.loadLibrary(libraryName);
            } catch (UnsatisfiedLinkError e) {
                throw new Error(String.format("FFmpegKit failed to start on %s.", getDeviceDebugInformation()), e);
            }
        }
    }

    private static List<String> loadExternalLibraries() {
        if (isTestModeDisabled()) {
            return Packages.getExternalLibraries();
        }
        return Collections.emptyList();
    }

    private static String loadNativeAbi() {
        if (isTestModeDisabled()) {
            return AbiDetect.getNativeAbi();
        }
        return Abi.ABI_X86_64.getName();
    }

    static String loadAbi() {
        if (isTestModeDisabled()) {
            return AbiDetect.getAbi();
        }
        return Abi.ABI_X86_64.getName();
    }

    static String loadPackageName() {
        if (isTestModeDisabled()) {
            return Packages.getPackageName();
        }
        return "test";
    }

    static String loadVersion() {
        if (isTestModeDisabled()) {
            return FFmpegKitConfig.getVersion();
        }
        return loadIsLTSBuild() ? String.format("%s-lts", "6.0") : "6.0";
    }

    static boolean loadIsLTSBuild() {
        if (isTestModeDisabled()) {
            return AbiDetect.isNativeLTSBuild();
        }
        return true;
    }

    static int loadLogLevel() {
        if (isTestModeDisabled()) {
            return FFmpegKitConfig.getNativeLogLevel();
        }
        return Level.AV_LOG_DEBUG.getValue();
    }

    static String loadBuildDate() {
        if (isTestModeDisabled()) {
            return FFmpegKitConfig.getBuildDate();
        }
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
    }

    static void enableRedirection() {
        if (isTestModeDisabled()) {
            FFmpegKitConfig.enableRedirection();
        }
    }

    static void loadFFmpegKitAbiDetect() {
        loadLibrary("ffmpegkit_abidetect");
    }

    static boolean loadFFmpeg() {
        return false;
    }

    static void loadFFmpegKit(boolean nativeFFmpegTriedAndFailed) {
        boolean nativeFFmpegKitLoaded = false;
        if (!nativeFFmpegTriedAndFailed && "arm-v7a".equals(loadNativeAbi())) {
            try {
                loadLibrary("ffmpegkit_armv7a_neon");
                nativeFFmpegKitLoaded = true;
                AbiDetect.setArmV7aNeonLoaded();
            } catch (Error e) {
            }
        }
        if (!nativeFFmpegKitLoaded) {
            loadLibrary("ffmpegkit");
        }
    }

    static String getDeviceDebugInformation() {
        return "brand: " + Build.BRAND + ", model: " + Build.MODEL + ", device: " + Build.DEVICE + ", api level: " + Build.VERSION.SDK_INT + ", abis: " + FFmpegKitConfig.argumentsToString(Build.SUPPORTED_ABIS) + ", 32bit abis: " + FFmpegKitConfig.argumentsToString(Build.SUPPORTED_32_BIT_ABIS) + ", 64bit abis: " + FFmpegKitConfig.argumentsToString(Build.SUPPORTED_64_BIT_ABIS);
    }
}
