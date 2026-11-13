package com.bkggds.videotool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

/* Player button icons drawn in code, so every icon has the same weight and size. */
public final class PlayerIcon extends View {
    public static final int BACK = 0;
    public static final int PLAY = 1;
    public static final int PAUSE = 2;
    public static final int AUDIO = 3;
    public static final int SUBTITLE = 4;
    public static final int RATIO = 5;
    public static final int FULLSCREEN = 6;
    public static final int EXIT_FULLSCREEN = 7;
    public static final int HEADPHONES = 8;

    private final Paint paint = new Paint(1);
    private final Paint textPaint = new Paint(1);
    private final Path path = new Path();
    private final RectF rect = new RectF();
    private int kind;

    public PlayerIcon(Context context) {
        super(context);
        this.kind = 0;
        this.paint.setColor(-1);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.textPaint.setColor(-1);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setFakeBoldText(true);
        this.textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        setFocusable(true);
    }

    public PlayerIcon(Context context, int kind) {
        this(context);
        this.kind = kind;
    }

    public void setKind(int kind) {
        if (this.kind != kind) {
            this.kind = kind;
            invalidate();
        }
    }

    public int getKind() {
        return this.kind;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        float s = Math.min(w, h) * 0.62f;
        float cx = w / 2.0f;
        float cy = h / 2.0f;
        float l = cx - s / 2.0f;
        float t = cy - s / 2.0f;
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(s * 0.10f);
        switch (this.kind) {
            case BACK:
                this.path.reset();
                this.path.moveTo(l + s * 0.88f, t + s * 0.5f);
                this.path.lineTo(l + s * 0.12f, t + s * 0.5f);
                this.path.moveTo(l + s * 0.44f, t + s * 0.14f);
                this.path.lineTo(l + s * 0.12f, t + s * 0.5f);
                this.path.lineTo(l + s * 0.44f, t + s * 0.86f);
                canvas.drawPath(this.path, this.paint);
                break;
            case PLAY:
                this.paint.setStyle(Paint.Style.FILL);
                this.path.reset();
                this.path.moveTo(l + s * 0.17f, t + s * 0.1f);
                this.path.lineTo(l + s * 0.91f, t + s * 0.5f);
                this.path.lineTo(l + s * 0.17f, t + s * 0.9f);
                this.path.close();
                canvas.drawPath(this.path, this.paint);
                break;
            case PAUSE:
                this.paint.setStyle(Paint.Style.FILL);
                this.rect.set(l + s * 0.15f, t + s * 0.12f, l + s * 0.38f, t + s * 0.88f);
                canvas.drawRoundRect(this.rect, s * 0.06f, s * 0.06f, this.paint);
                this.rect.set(l + s * 0.62f, t + s * 0.12f, l + s * 0.85f, t + s * 0.88f);
                canvas.drawRoundRect(this.rect, s * 0.06f, s * 0.06f, this.paint);
                break;
            case AUDIO:
                this.paint.setStyle(Paint.Style.FILL);
                this.path.reset();
                this.path.moveTo(l + s * 0.04f, t + s * 0.36f);
                this.path.lineTo(l + s * 0.24f, t + s * 0.36f);
                this.path.lineTo(l + s * 0.46f, t + s * 0.13f);
                this.path.lineTo(l + s * 0.46f, t + s * 0.87f);
                this.path.lineTo(l + s * 0.24f, t + s * 0.64f);
                this.path.lineTo(l + s * 0.04f, t + s * 0.64f);
                this.path.close();
                canvas.drawPath(this.path, this.paint);
                this.paint.setStyle(Paint.Style.STROKE);
                this.paint.setStrokeWidth(s * 0.09f);
                float ax = l + s * 0.5f;
                float ay = t + s * 0.5f;
                this.rect.set(ax - s * 0.19f, ay - s * 0.19f, ax + s * 0.19f, ay + s * 0.19f);
                canvas.drawArc(this.rect, -55.0f, 110.0f, false, this.paint);
                this.rect.set(ax - s * 0.38f, ay - s * 0.38f, ax + s * 0.38f, ay + s * 0.38f);
                canvas.drawArc(this.rect, -55.0f, 110.0f, false, this.paint);
                break;
            case SUBTITLE:
                this.paint.setStyle(Paint.Style.STROKE);
                this.paint.setStrokeWidth(s * 0.09f);
                this.rect.set(l + s * 0.07f, t + s * 0.13f, l + s * 0.93f, t + s * 0.87f);
                canvas.drawRoundRect(this.rect, s * 0.14f, s * 0.14f, this.paint);
                this.textPaint.setTextSize(s * 0.4f);
                canvas.drawText("CC", cx, cy + s * 0.14f, this.textPaint);
                break;
            case RATIO:
                this.paint.setStyle(Paint.Style.STROKE);
                this.paint.setStrokeWidth(s * 0.09f);
                this.rect.set(l + s * 0.07f, t + s * 0.13f, l + s * 0.93f, t + s * 0.87f);
                canvas.drawRoundRect(this.rect, s * 0.12f, s * 0.12f, this.paint);
                this.paint.setStrokeWidth(s * 0.08f);
                this.path.reset();
                this.path.moveTo(l + s * 0.26f, cy);
                this.path.lineTo(l + s * 0.74f, cy);
                this.path.moveTo(l + s * 0.38f, cy - s * 0.12f);
                this.path.lineTo(l + s * 0.24f, cy);
                this.path.lineTo(l + s * 0.38f, cy + s * 0.12f);
                this.path.moveTo(l + s * 0.62f, cy - s * 0.12f);
                this.path.lineTo(l + s * 0.76f, cy);
                this.path.lineTo(l + s * 0.62f, cy + s * 0.12f);
                canvas.drawPath(this.path, this.paint);
                break;
            case FULLSCREEN:
                this.brackets(canvas, l, t, s, true);
                break;
            case EXIT_FULLSCREEN:
                this.brackets(canvas, l, t, s, false);
                break;
            case HEADPHONES:
                /* Headband arc with two filled earcups: the audio track button. */
                this.paint.setStyle(Paint.Style.STROKE);
                this.paint.setStrokeWidth(s * 0.10f);
                this.rect.set(l + s * 0.11f, t + s * 0.10f, l + s * 0.89f, t + s * 0.90f);
                canvas.drawArc(this.rect, 180.0f, 180.0f, false, this.paint);
                this.paint.setStyle(Paint.Style.FILL);
                this.rect.set(l + s * 0.06f, t + s * 0.50f, l + s * 0.29f, t + s * 0.86f);
                canvas.drawRoundRect(this.rect, s * 0.08f, s * 0.08f, this.paint);
                this.rect.set(l + s * 0.71f, t + s * 0.50f, l + s * 0.94f, t + s * 0.86f);
                canvas.drawRoundRect(this.rect, s * 0.08f, s * 0.08f, this.paint);
                break;
            default:
                break;
        }
    }

    /* Four corner brackets, pointing out for fullscreen and in for exit. */
    private void brackets(Canvas canvas, float l, float t, float s, boolean out) {
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(s * 0.10f);
        float a = out ? 0.1f : 0.4f;
        float b = out ? 0.4f : 0.1f;
        this.path.reset();
        this.path.moveTo(l + s * a, t + s * b);
        this.path.lineTo(l + s * a, t + s * a);
        this.path.lineTo(l + s * b, t + s * a);
        this.path.moveTo(l + s * (1.0f - a), t + s * b);
        this.path.lineTo(l + s * (1.0f - a), t + s * a);
        this.path.lineTo(l + s * (1.0f - b), t + s * a);
        this.path.moveTo(l + s * a, t + s * (1.0f - b));
        this.path.lineTo(l + s * a, t + s * (1.0f - a));
        this.path.lineTo(l + s * b, t + s * (1.0f - a));
        this.path.moveTo(l + s * (1.0f - a), t + s * (1.0f - b));
        this.path.lineTo(l + s * (1.0f - a), t + s * (1.0f - a));
        this.path.lineTo(l + s * (1.0f - b), t + s * (1.0f - a));
        canvas.drawPath(this.path, this.paint);
    }
}
