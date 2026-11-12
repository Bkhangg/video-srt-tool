package com.antonkarpenko.ffmpegkit;

/* loaded from: classes.dex */
public enum Abi {
    ABI_ARMV7A_NEON("armeabi-v7a-neon"),
    ABI_ARMV7A("armeabi-v7a"),
    ABI_ARM("armeabi"),
    ABI_X86("x86"),
    ABI_X86_64("x86_64"),
    ABI_ARM64_V8A("arm64-v8a"),
    ABI_UNKNOWN("unknown");

    private final String name;

    public static Abi from(String abiName) {
        if (abiName == null) {
            return ABI_UNKNOWN;
        }
        if (abiName.equals(ABI_ARM.getName())) {
            return ABI_ARM;
        }
        if (abiName.equals(ABI_ARMV7A.getName())) {
            return ABI_ARMV7A;
        }
        if (abiName.equals(ABI_ARMV7A_NEON.getName())) {
            return ABI_ARMV7A_NEON;
        }
        if (abiName.equals(ABI_ARM64_V8A.getName())) {
            return ABI_ARM64_V8A;
        }
        if (abiName.equals(ABI_X86.getName())) {
            return ABI_X86;
        }
        if (abiName.equals(ABI_X86_64.getName())) {
            return ABI_X86_64;
        }
        return ABI_UNKNOWN;
    }

    public String getName() {
        return this.name;
    }

    Abi(String abiName) {
        this.name = abiName;
    }
}
