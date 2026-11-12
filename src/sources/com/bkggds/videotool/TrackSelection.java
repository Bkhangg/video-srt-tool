package com.bkggds.videotool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/* loaded from: classes.dex */
public final class TrackSelection {
    private final LinkedHashSet<Integer> checked = new LinkedHashSet<>();
    private boolean[] eligible = new boolean[0];
    private String signature = "";

    public void sync(String str, boolean[] zArr) {
        if (this.signature.equals(str)) {
            return;
        }
        this.signature = str;
        this.eligible = (boolean[]) zArr.clone();
        this.checked.clear();
        for (int i = 0; i < zArr.length; i++) {
            if (zArr[i]) {
                this.checked.add(Integer.valueOf(i));
                return;
            }
        }
    }

    public void set(int i, boolean z) {
        if (i < 0 || i >= this.eligible.length || !this.eligible[i]) {
            return;
        }
        if (z) {
            this.checked.add(Integer.valueOf(i));
        } else {
            this.checked.remove(Integer.valueOf(i));
        }
    }

    public boolean checked(int i) {
        return this.checked.contains(Integer.valueOf(i));
    }

    public void all() {
        for (int i = 0; i < this.eligible.length; i++) {
            if (this.eligible[i]) {
                this.checked.add(Integer.valueOf(i));
            }
        }
    }

    public void none() {
        this.checked.clear();
    }

    public int size() {
        return this.checked.size();
    }

    public List<Integer> indices() {
        ArrayList arrayList = new ArrayList(this.checked);
        Collections.sort(arrayList);
        return arrayList;
    }
}
