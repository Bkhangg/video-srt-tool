package com.bkggds.videotool;

import android.content.Context;
import android.widget.ScrollView;

/* loaded from: classes.dex */
public final class PageScrollView extends ScrollView {
    private int generation;
    private int page;
    private final int[] positions;

    public PageScrollView(Context context) {
        super(context);
        this.positions = new int[6];
        this.page = -1;
        setFillViewport(true);
        setClipToPadding(false);
        setOverScrollMode(1);
        setDescendantFocusability(131072);
        setFocusableInTouchMode(true);
    }

    public void showPage(final int i) {
        if (i < 0 || i >= this.positions.length || this.page == i) {
            return;
        }
        if (this.page >= 0) {
            this.positions[this.page] = getScrollY();
        }
        this.page = i;
        final int i2 = this.generation + 1;
        this.generation = i2;
        requestFocus();
        post(new Runnable() { // from class: com.bkggds.videotool.PageScrollView.1
            @Override // java.lang.Runnable
            public void run() {
                if (i2 == PageScrollView.this.generation) {
                    PageScrollView.this.scrollTo(0, PageScrollView.this.positions[i]);
                }
            }
        });
    }
}
