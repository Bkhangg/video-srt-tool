package com.antonkarpenko.ffmpegkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class Packages {
    private static final List<String> supportedExternalLibraries = new ArrayList();

    static {
        supportedExternalLibraries.add("dav1d");
        supportedExternalLibraries.add("fontconfig");
        supportedExternalLibraries.add("freetype");
        supportedExternalLibraries.add("fribidi");
        supportedExternalLibraries.add("gmp");
        supportedExternalLibraries.add("gnutls");
        supportedExternalLibraries.add("kvazaar");
        supportedExternalLibraries.add("mp3lame");
        supportedExternalLibraries.add("libass");
        supportedExternalLibraries.add("iconv");
        supportedExternalLibraries.add("libilbc");
        supportedExternalLibraries.add("libtheora");
        supportedExternalLibraries.add("libvidstab");
        supportedExternalLibraries.add("libvorbis");
        supportedExternalLibraries.add("libvpx");
        supportedExternalLibraries.add("libwebp");
        supportedExternalLibraries.add("libxml2");
        supportedExternalLibraries.add("opencore-amr");
        supportedExternalLibraries.add("openh264");
        supportedExternalLibraries.add("openssl");
        supportedExternalLibraries.add("opus");
        supportedExternalLibraries.add("rubberband");
        supportedExternalLibraries.add("sdl2");
        supportedExternalLibraries.add("shine");
        supportedExternalLibraries.add("snappy");
        supportedExternalLibraries.add("soxr");
        supportedExternalLibraries.add("speex");
        supportedExternalLibraries.add("srt");
        supportedExternalLibraries.add("tesseract");
        supportedExternalLibraries.add("twolame");
        supportedExternalLibraries.add("x264");
        supportedExternalLibraries.add("x265");
        supportedExternalLibraries.add("xvid");
        supportedExternalLibraries.add("zimg");
    }

    public static String getPackageName() {
        List<String> externalLibraryList = getExternalLibraries();
        boolean speex = externalLibraryList.contains("speex");
        boolean fribidi = externalLibraryList.contains("fribidi");
        boolean gnutls = externalLibraryList.contains("gnutls");
        boolean xvid = externalLibraryList.contains("xvid");
        boolean minGpl = false;
        boolean https = false;
        boolean httpsGpl = false;
        boolean audio = false;
        boolean video = false;
        boolean full = false;
        boolean fullGpl = false;
        if (speex && fribidi) {
            if (xvid) {
                fullGpl = true;
            } else {
                full = true;
            }
        } else if (speex) {
            audio = true;
        } else if (fribidi) {
            video = true;
        } else if (xvid) {
            if (gnutls) {
                httpsGpl = true;
            } else {
                minGpl = true;
            }
        } else if (gnutls) {
            https = true;
        }
        return fullGpl ? (externalLibraryList.contains("dav1d") && externalLibraryList.contains("fontconfig") && externalLibraryList.contains("freetype") && externalLibraryList.contains("fribidi") && externalLibraryList.contains("gmp") && externalLibraryList.contains("gnutls") && externalLibraryList.contains("kvazaar") && externalLibraryList.contains("mp3lame") && externalLibraryList.contains("libass") && externalLibraryList.contains("iconv") && externalLibraryList.contains("libilbc") && externalLibraryList.contains("libtheora") && externalLibraryList.contains("libvidstab") && externalLibraryList.contains("libvorbis") && externalLibraryList.contains("libvpx") && externalLibraryList.contains("libwebp") && externalLibraryList.contains("libxml2") && externalLibraryList.contains("opencore-amr") && externalLibraryList.contains("opus") && externalLibraryList.contains("shine") && externalLibraryList.contains("snappy") && externalLibraryList.contains("soxr") && externalLibraryList.contains("speex") && externalLibraryList.contains("twolame") && externalLibraryList.contains("x264") && externalLibraryList.contains("x265") && externalLibraryList.contains("xvid") && externalLibraryList.contains("zimg")) ? "full-gpl" : "custom" : full ? (externalLibraryList.contains("dav1d") && externalLibraryList.contains("fontconfig") && externalLibraryList.contains("freetype") && externalLibraryList.contains("fribidi") && externalLibraryList.contains("gmp") && externalLibraryList.contains("gnutls") && externalLibraryList.contains("kvazaar") && externalLibraryList.contains("mp3lame") && externalLibraryList.contains("libass") && externalLibraryList.contains("iconv") && externalLibraryList.contains("libilbc") && externalLibraryList.contains("libtheora") && externalLibraryList.contains("libvorbis") && externalLibraryList.contains("libvpx") && externalLibraryList.contains("libwebp") && externalLibraryList.contains("libxml2") && externalLibraryList.contains("opencore-amr") && externalLibraryList.contains("opus") && externalLibraryList.contains("shine") && externalLibraryList.contains("snappy") && externalLibraryList.contains("soxr") && externalLibraryList.contains("speex") && externalLibraryList.contains("twolame") && externalLibraryList.contains("zimg")) ? "full" : "custom" : video ? (externalLibraryList.contains("dav1d") && externalLibraryList.contains("fontconfig") && externalLibraryList.contains("freetype") && externalLibraryList.contains("fribidi") && externalLibraryList.contains("kvazaar") && externalLibraryList.contains("libass") && externalLibraryList.contains("iconv") && externalLibraryList.contains("libtheora") && externalLibraryList.contains("libvpx") && externalLibraryList.contains("libwebp") && externalLibraryList.contains("snappy") && externalLibraryList.contains("zimg")) ? "video" : "custom" : audio ? (externalLibraryList.contains("mp3lame") && externalLibraryList.contains("libilbc") && externalLibraryList.contains("libvorbis") && externalLibraryList.contains("opencore-amr") && externalLibraryList.contains("opus") && externalLibraryList.contains("shine") && externalLibraryList.contains("soxr") && externalLibraryList.contains("speex") && externalLibraryList.contains("twolame")) ? "audio" : "custom" : httpsGpl ? (externalLibraryList.contains("gmp") && externalLibraryList.contains("gnutls") && externalLibraryList.contains("libvidstab") && externalLibraryList.contains("x264") && externalLibraryList.contains("x265") && externalLibraryList.contains("xvid")) ? "https-gpl" : "custom" : https ? (externalLibraryList.contains("gmp") && externalLibraryList.contains("gnutls")) ? "https" : "custom" : minGpl ? (externalLibraryList.contains("libvidstab") && externalLibraryList.contains("x264") && externalLibraryList.contains("x265") && externalLibraryList.contains("xvid")) ? "min-gpl" : "custom" : externalLibraryList.size() == 0 ? "min" : "custom";
    }

    public static List<String> getExternalLibraries() {
        String buildConfiguration = AbiDetect.getNativeBuildConf();
        List<String> enabledLibraryList = new ArrayList<>();
        for (String supportedExternalLibrary : supportedExternalLibraries) {
            if (buildConfiguration.contains("enable-" + supportedExternalLibrary) || buildConfiguration.contains("enable-lib" + supportedExternalLibrary)) {
                enabledLibraryList.add(supportedExternalLibrary);
            }
        }
        Collections.sort(enabledLibraryList);
        return enabledLibraryList;
    }
}
