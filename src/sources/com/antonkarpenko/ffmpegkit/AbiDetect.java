package com.antonkarpenko.ffmpegkit;

/* loaded from: classes.dex */
public class AbiDetect {
    static final String ARM_V7A = "arm-v7a";
    static final String ARM_V7A_NEON = "arm-v7a-neon";
    private static boolean armV7aNeonLoaded = false;

    static native String getNativeAbi();

    static native String getNativeBuildConf();

    static native String getNativeCpuAbi();

    static native boolean isNativeLTSBuild();

    static {
        NativeLoader.loadFFmpegKitAbiDetect();
        FFmpegKit.class.getName();
        FFmpegKitConfig.class.getName();
        FFprobeKit.class.getName();
    }

    private AbiDetect() {
    }

    static void setArmV7aNeonLoaded() {
        armV7aNeonLoaded = true;
    }

    public static String getAbi() {
        if (armV7aNeonLoaded) {
            return ARM_V7A_NEON;
        }
        return getNativeAbi();
    }

    public static String getCpuAbi() {
        return getNativeCpuAbi();
    }
}
