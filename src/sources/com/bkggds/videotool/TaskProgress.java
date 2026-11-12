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
public final class TaskProgress {
    private final ProgressBar bar;
    private volatile boolean cancelled;
    private final AlertDialog dialog;
    private final Activity owner;
    private final TextView status;
    private final Handler ui = new Handler();

    public TaskProgress(Activity activity, String str, int i) {
        this.owner = activity;
        Context context = AppTheme.context(activity);
        int round = Math.round(context.getResources().getDisplayMetrics().density * 20.0f);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        int i2 = round / 2;
        linearLayout.setPadding(round, i2, round, i2);
        this.status = new TextView(context);
        this.status.setTextColor(-13418155);
        this.status.setTextSize(13.0f);
        this.status.setText("0 / " + i);
        linearLayout.addView(this.status);
        this.bar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        this.bar.setMax(Math.max(1, i));
        linearLayout.addView(this.bar, new LinearLayout.LayoutParams(-1, round * 2));
        AppTheme.apply(linearLayout);
        this.dialog = new AlertDialog.Builder(context).setTitle(Localization.tr(str)).setView(linearLayout).setNegativeButton(Localization.tr("Cancel"), new DialogInterface.OnClickListener() { // from class: com.bkggds.videotool.TaskProgress.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i3) {
                TaskProgress.this.cancelled = true;
            }
        }).create();
        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.bkggds.videotool.TaskProgress.2
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                TaskProgress.this.cancelled = true;
            }
        });
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.show();
        AppFonts.decorate(this.dialog);
    }

    public boolean cancelled() {
        return this.cancelled || this.owner.isDestroyed() || this.owner.isFinishing();
    }

    public void cancel() {
        this.cancelled = true;
        dismiss();
    }

    public void dismiss() {
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }

    public void update(final int i, final int i2, final String str) {
        this.ui.post(new Runnable() { // from class: com.bkggds.videotool.TaskProgress.3
            @Override // java.lang.Runnable
            public void run() {
                if (TaskProgress.this.cancelled()) {
                    return;
                }
                TaskProgress.this.bar.setProgress(i);
                TaskProgress.this.status.setText(String.valueOf(i) + " / " + i2 + " · " + ((i * 100) / Math.max(1, i2)) + "%\n" + str);
            }
        });
    }
}
