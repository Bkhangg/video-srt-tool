package com.bkggds.videotool;

import android.widget.Toast;

/* loaded from: classes.dex */
public final class ImportGuard {
    public static boolean require(MainActivity mainActivity) {
        if (mainActivity.videoUi() != null) {
            return true;
        }
        Toast.makeText(mainActivity, Localization.tr("Please import a video first."), 0).show();
        return false;
    }
}
