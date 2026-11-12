package com.bkggds.videotool;

import java.util.ArrayDeque;

/* loaded from: classes.dex */
public final class NavigationHistory {
    private final ArrayDeque<Integer> stack = new ArrayDeque<>();
    private int current = -1;

    public void visit(int i) {
        if (i == 4) {
            i = 2;
        }
        if (this.current == i) {
            return;
        }
        if (this.current >= 0) {
            if (this.stack.size() == 64) {
                this.stack.removeFirst();
            }
            this.stack.addLast(Integer.valueOf(this.current));
        }
        this.current = i;
    }

    public int back() {
        if (!this.stack.isEmpty()) {
            this.current = this.stack.removeLast().intValue();
        }
        if (this.current < 0) {
            return 0;
        }
        return this.current;
    }

    public int size() {
        return this.stack.size();
    }
}
