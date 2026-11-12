package com.bkggds.videotool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/* loaded from: classes.dex */
public final class MuxProgress {
    private final ProgressBar bar;
    private volatile boolean closed;
    private final AlertDialog dialog;
    private long last;
    private final Runnable onCancel;
    private final TextView status;
    private final Handler ui = new Handler();

    public MuxProgress(Activity activity, String str, Runnable runnable) {
        this.onCancel = runnable;
        Context context = AppTheme.context(activity);
        int round = Math.round(context.getResources().getDisplayMetrics().density * 20.0f);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        int i = round / 2;
        linearLayout.setPadding(round, i, round, i);
        this.status = new TextView(context);
        this.status.setTextSize(13.0f);
        this.status.setTextColor(-13418155);
        linearLayout.addView(this.status);
        this.bar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        this.bar.setMax(100);
        linearLayout.addView(this.bar, new LinearLayout.LayoutParams(-1, round * 2));
        AppTheme.apply(linearLayout);
        this.dialog = new AlertDialog.Builder(context).setTitle(Localization.tr(str)).setView(linearLayout).setNegativeButton(Localization.tr("Cancel"), new DialogInterface.OnClickListener() { // from class: com.bkggds.videotool.MuxProgress.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                MuxProgress.this.onCancel.run();
            }
        }).create();
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.bkggds.videotool.MuxProgress.2
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                MuxProgress.this.onCancel.run();
            }
        });
        this.dialog.show();
        this.dialog.getWindow().addFlags(128);
        AppFonts.decorate(this.dialog);
    }

    public synchronized void update(final int i, final String str, boolean z) {
        if (!this.closed) {
            long nanoTime = System.nanoTime();
            if (z || nanoTime - this.last >= 150000000) {
                this.last = nanoTime;
                this.ui.post(new Runnable() { // from class: com.bkggds.videotool.MuxProgress.3
                    @Override // java.lang.Runnable
                    public void run() {
                        if (MuxProgress.this.closed) {
                            return;
                        }
                        MuxProgress.this.bar.setIndeterminate(i < 0);
                        if (i >= 0) {
                            MuxProgress.this.bar.setProgress(Math.min(100, i));
                        }
                        MuxProgress.this.status.setText(String.valueOf(Localization.tr(str)) + (i >= 0 ? " · " + Math.min(100, i) + "%" : ""));
                    }
                });
            }
        }
    }

    public void dismiss() {
        this.closed = true;
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }
}
