package com.bkggds.videotool;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.EditText;

/* loaded from: classes.dex */
public final class SubtitleEditor extends EditText {
    private final int slop;
    private float startX;
    private float startY;

    public SubtitleEditor(Context context) {
        super(context);
        this.slop = ViewConfiguration.get(context).getScaledTouchSlop();
        setSingleLine(false);
        setHorizontallyScrolling(false);
        setVerticalScrollBarEnabled(true);
        setScrollBarStyle(16777216);
        setOverScrollMode(1);
        setImeOptions(268435456);
        setSelectAllOnFocus(false);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        ViewParent parent = getParent();
        int actionMasked = motionEvent.getActionMasked();
        if (parent != null) {
            if (actionMasked == 0) {
                this.startX = motionEvent.getX();
                this.startY = motionEvent.getY();
                parent.requestDisallowInterceptTouchEvent(true);
            } else if (actionMasked != 2) {
                if (actionMasked == 1 || actionMasked == 3) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            } else {
                parent.requestDisallowInterceptTouchEvent(EditorScrollPolicy.keepGesture(motionEvent.getX() - this.startX, motionEvent.getY() - this.startY, this.slop, canScrollVertically(-1), canScrollVertically(1)));
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        int max;
        if ((getTag() == null || !getTag().equals("fullscreen")) && View.MeasureSpec.getSize(i2) > (max = Math.max(120, Math.round(getResources().getDisplayMetrics().heightPixels * 0.48f)))) {
            i2 = View.MeasureSpec.makeMeasureSpec(max, 1073741824);
        }
        super.onMeasure(i, i2);
    }
}
