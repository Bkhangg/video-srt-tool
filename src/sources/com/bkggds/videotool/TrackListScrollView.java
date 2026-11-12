package com.bkggds.videotool;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ScrollView;

/* loaded from: classes.dex */
public final class TrackListScrollView extends ScrollView {
    private final int slop;
    private float x;
    private float y;

    public TrackListScrollView(Context context) {
        super(context);
        this.slop = ViewConfiguration.get(context).getScaledTouchSlop();
        setVerticalScrollBarEnabled(true);
        setScrollbarFadingEnabled(false);
        setOverScrollMode(1);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        ViewParent parent = getParent();
        if (parent != null) {
            if (motionEvent.getActionMasked() == 0) {
                this.x = motionEvent.getX();
                this.y = motionEvent.getY();
                parent.requestDisallowInterceptTouchEvent(true);
            } else if (motionEvent.getActionMasked() == 2) {
                parent.requestDisallowInterceptTouchEvent(EditorScrollPolicy.keepGesture(motionEvent.getX() - this.x, motionEvent.getY() - this.y, this.slop, canScrollVertically(-1), canScrollVertically(1)));
            } else if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3) {
                parent.requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (getChildCount() != 0) {
            if (!(getChildAt(0) instanceof ViewGroup)) {
                return;
            }
            ViewGroup viewGroup = (ViewGroup) getChildAt(0);
            if (viewGroup.getChildCount() <= 4) {
                return;
            }
            int paddingTop = getPaddingTop() + getPaddingBottom();
            for (int i3 = 0; i3 < 4; i3++) {
                View childAt = viewGroup.getChildAt(i3);
                paddingTop += childAt.getMeasuredHeight();
                ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    paddingTop += marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
                }
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.max(1, paddingTop), 1073741824));
        }
    }
}
