package com.example.emotionaldiary.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Locale;

public class EmotionPieChart extends View {

    private final Paint slicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF chartRect = new RectF();

    private final String[] emotionNames = {
            "Happy",
            "Excited",
            "Calm",
            "Neutral",
            "Sad",
            "Angry"
    };

    // Professional dashboard colors
    private final int[] emotionColors = {
            0xFFF4B942, // Happy - Warm Gold
            0xFFE85D75, // Excited - Rose
            0xFF6FCF7A, // Calm - Soft Green
            0xFF5B8DEF, // Neutral - Blue
            0xFF8B6FE8, // Sad - Purple
            0xFFF26B4A  // Angry - Coral Red
    };

    private int[] emotionCounts = {
            0, 0, 0, 0, 0, 0
    };

    private int total = 0;

    public EmotionPieChart(Context context) {
        super(context);
        init();
    }

    public EmotionPieChart(
            Context context,
            @Nullable AttributeSet attrs) {

        super(context, attrs);
        init();
    }

    public EmotionPieChart(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        slicePaint.setStyle(Paint.Style.FILL);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(
                android.graphics.Typeface.create(
                        "sans",
                        android.graphics.Typeface.BOLD
                )
        );

        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setColor(0xFFFFFFFF);

        setBackgroundColor(0x00000000);
    }

    // =====================================================
    // SET EMOTION DATA
    // =====================================================

    public void setEmotionCounts(
            int happy,
            int excited,
            int calm,
            int neutral,
            int sad,
            int angry) {

        emotionCounts = new int[]{
                happy,
                excited,
                calm,
                neutral,
                sad,
                angry
        };

        total =
                happy +
                        excited +
                        calm +
                        neutral +
                        sad +
                        angry;

        invalidate();
    }

    // =====================================================
    // DRAW
    // =====================================================

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float centerX = width / 2f;
        float centerY = height / 2f;

        float radius =
                Math.min(width, height) * 0.42f;

        chartRect.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        // =========================
        // EMPTY STATE
        // =========================

        if (total == 0) {

            drawEmptyChart(
                    canvas,
                    centerX,
                    centerY,
                    radius
            );

            return;
        }

        // =========================
        // PIE CHART
        // =========================

        float startAngle = -90f;

        for (int i = 0; i < emotionCounts.length; i++) {

            int count =
                    emotionCounts[i];

            if (count <= 0) {
                continue;
            }

            float sweepAngle =
                    (count / (float) total) * 360f;

            slicePaint.setColor(
                    emotionColors[i]
            );

            canvas.drawArc(
                    chartRect,
                    startAngle,
                    sweepAngle,
                    true,
                    slicePaint
            );

            // Draw information inside slice
            drawSliceInformation(
                    canvas,
                    centerX,
                    centerY,
                    radius,
                    startAngle,
                    sweepAngle,
                    emotionNames[i],
                    count
            );

            startAngle += sweepAngle;
        }

        // =========================
        // CENTER CIRCLE
        // =========================

        float centerRadius =
                radius * 0.27f;

        centerPaint.setShadowLayer(
                8f,
                0f,
                3f,
                0x30000000
        );

        canvas.drawCircle(
                centerX,
                centerY,
                centerRadius,
                centerPaint
        );

        centerPaint.clearShadowLayer();

        // Center total
        textPaint.setColor(
                0xFF20264A
        );

        textPaint.setTextSize(
                radius * 0.14f
        );

        canvas.drawText(
                String.valueOf(total),
                centerX,
                centerY - 2,
                textPaint
        );

        textPaint.setTypeface(
                android.graphics.Typeface.create(
                        "sans",
                        android.graphics.Typeface.NORMAL
                )
        );

        textPaint.setTextSize(
                radius * 0.065f
        );

        canvas.drawText(
                "Diaries",
                centerX,
                centerY + radius * 0.13f,
                textPaint
        );

        textPaint.setTypeface(
                android.graphics.Typeface.create(
                        "sans",
                        android.graphics.Typeface.BOLD
                )
        );
    }

    // =====================================================
    // SLICE INFORMATION
    // =====================================================

    private void drawSliceInformation(
            Canvas canvas,
            float centerX,
            float centerY,
            float radius,
            float startAngle,
            float sweepAngle,
            String emotion,
            int count) {

        // Don't put text in extremely tiny slices
        if (sweepAngle < 18f) {
            return;
        }

        float middleAngle =
                startAngle + (sweepAngle / 2f);

        double angle =
                Math.toRadians(middleAngle);

        float textRadius =
                radius * 0.64f;

        float x =
                centerX +
                        (float) Math.cos(angle)
                                * textRadius;

        float y =
                centerY +
                        (float) Math.sin(angle)
                                * textRadius;

        float percentage =
                (count / (float) total) * 100f;

        // White text
        textPaint.setColor(
                0xFFFFFFFF
        );

        // Add subtle shadow for readability
        textPaint.setShadowLayer(
                4f,
                0f,
                2f,
                0x55000000
        );

        // Emotion name
        textPaint.setTextSize(
                radius * 0.095f
        );

        canvas.drawText(
                emotion,
                x,
                y - radius * 0.055f,
                textPaint
        );

        // Count
        textPaint.setTextSize(
                radius * 0.12f
        );

        canvas.drawText(
                String.valueOf(count),
                x,
                y + radius * 0.065f,
                textPaint
        );

        // Percentage
        textPaint.setTextSize(
                radius * 0.065f
        );

        canvas.drawText(
                String.format(
                        Locale.getDefault(),
                        "%.1f%%",
                        percentage
                ),
                x,
                y + radius * 0.135f,
                textPaint
        );

        textPaint.clearShadowLayer();
    }

    // =====================================================
    // EMPTY CHART
    // =====================================================

    private void drawEmptyChart(
            Canvas canvas,
            float centerX,
            float centerY,
            float radius) {

        slicePaint.setColor(
                0xFFE8EAF3
        );

        canvas.drawCircle(
                centerX,
                centerY,
                radius,
                slicePaint
        );

        centerPaint.setColor(
                0xFFFFFFFF
        );

        canvas.drawCircle(
                centerX,
                centerY,
                radius * 0.28f,
                centerPaint
        );

        textPaint.setColor(
                0xFF68708A
        );

        textPaint.setTextSize(
                radius * 0.095f
        );

        textPaint.setTypeface(
                android.graphics.Typeface.create(
                        "sans",
                        android.graphics.Typeface.NORMAL
                )
        );

        canvas.drawText(
                "No diaries yet",
                centerX,
                centerY,
                textPaint
        );

        textPaint.setTypeface(
                android.graphics.Typeface.create(
                        "sans",
                        android.graphics.Typeface.BOLD
                )
        );
    }
}