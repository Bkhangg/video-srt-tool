package com.bkggds.videotool;

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.window.OnBackInvokedCallback;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class Navigation {
    private static final WeakHashMap<MainActivity, State> states = new WeakHashMap<>();

    private static final class State {
        Object callback;
        final NavigationHistory history;

        private State() {
            this.history = new NavigationHistory();
        }

        /* synthetic */ State(State state) {
            this();
        }
    }

    private static State state(MainActivity mainActivity) {
        State state = states.get(mainActivity);
        if (state != null) {
            return state;
        }
        State state2 = new State(null);
        states.put(mainActivity, state2);
        return state2;
    }

    public static void record(MainActivity mainActivity, int i) {
        state(mainActivity).history.visit(i);
    }

    public static void install(MainActivity mainActivity) {
        State state = state(mainActivity);
        if (Build.VERSION.SDK_INT < 33 || state.callback != null) {
            return;
        }
        state.callback = Api33.install(mainActivity);
    }

    public static void release(MainActivity mainActivity) {
        State remove = states.remove(mainActivity);
        if (remove == null || remove.callback == null || Build.VERSION.SDK_INT < 33) {
            return;
        }
        Api33.remove(mainActivity, remove.callback);
    }

    public static void back(MainActivity mainActivity) {
        View decorView = mainActivity.getWindow().getDecorView();
        boolean z = true;
        if (Build.VERSION.SDK_INT >= 30) {
            WindowInsets rootWindowInsets = decorView.getRootWindowInsets();
            if (rootWindowInsets == null || !rootWindowInsets.isVisible(WindowInsets.Type.ime())) {
                z = false;
            }
        } else {
            Rect rect = new Rect();
            decorView.getWindowVisibleDisplayFrame(rect);
            if (decorView.getRootView().getHeight() - rect.bottom <= decorView.getResources().getDisplayMetrics().heightPixels / 4) {
                z = false;
            }
        }
        if (!z) {
            State state = state(mainActivity);
            if (state.history.size() == 0) {
                Toast.makeText(mainActivity, Localization.tr("You are on the first screen."), 0).show();
                return;
            } else {
                mainActivity.navigateUi(state.history.back());
                return;
            }
        }
        InputMethodManager inputMethodManager = (InputMethodManager) mainActivity.getSystemService("input_method");
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
        }
        View currentFocus = mainActivity.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
    }

    private static final class Api33 {
        private Api33() {
        }

        static Object install(final MainActivity mainActivity) {
            OnBackInvokedCallback onBackInvokedCallback = new OnBackInvokedCallback() { // from class: com.bkggds.videotool.Navigation.Api33.1
                @Override // android.window.OnBackInvokedCallback
                public void onBackInvoked() {
                    mainActivity.onBackPressed();
                }
            };
            mainActivity.getOnBackInvokedDispatcher().registerOnBackInvokedCallback(0, onBackInvokedCallback);
            return onBackInvokedCallback;
        }

        static void remove(MainActivity mainActivity, Object obj) {
            mainActivity.getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback((OnBackInvokedCallback) obj);
        }
    }
}
