package com.example.emotionaldiary.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class EmotionPieChartView extends View {

    private final Paint piePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF pieRect = new RectF();

    private final String[] emotionNames = {
            "Happy",
            "Excited",
            "Calm",
            "Neutral",
            "Sad",
            "Angry"
    };

    private final int[] emotionColors = {
            Color.rgb(76, 175, 80),
            Color.rgb(255, 152, 0),
            Color.rgb(33, 150, 243),
            Color.rgb(158, 158, 158),
            Color.rgb(66, 66, 66),
            Color.rgb(244, 67, 54)
    };

    private int[] emotionCounts = {
            0, 0, 0, 0, 0, 0
    };

    public EmotionPieChartView(Context context) {
        super(context);
        initialize();
    }

    public EmotionPieChartView(
            Context context,
            @Nullable AttributeSet attrs
    ) {
        super(context, attrs);
        initialize();
    }

    public EmotionPieChartView(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {

        piePaint.setStyle(Paint.Style.FILL);

        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(28f);
        textPaint.setTypeface(
                android.graphics.Typeface.DEFAULT_BOLD
        );

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setEmotionCounts(
            int happy,
            int excited,
            int calm,
            int neutral,
            int sad,
            int angry
    ) {

        emotionCounts = new int[]{
                happy,
                excited,
                calm,
                neutral,
                sad,
                angry
        };

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        int total = 0;

        for (int count : emotionCounts) {
            total += count;
        }

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        float radius =
                Math.min(getWidth(), getHeight()) * 0.30f;

        pieRect.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        if (total == 0) {

            piePaint.setColor(
                    Color.rgb(224, 224, 224)
            );

            canvas.drawCircle(
                    centerX,
                    centerY,
                    radius,
                    piePaint
            );

            textPaint.setColor(
                    Color.rgb(100, 100, 100)
            );

            textPaint.setTextSize(18f);

            String text = "No diary data";

            float textWidth =
                    textPaint.measureText(text);

            canvas.drawText(
                    text,
                    centerX - textWidth / 2f,
                    centerY + 6f,
                    textPaint
            );

            drawLegend(canvas, total);

            return;
        }

        float startAngle = -90f;

        for (int i = 0; i < emotionCounts.length; i++) {

            int count = emotionCounts[i];

            if (count <= 0) {
                continue;
            }

            float sweepAngle =
                    ((float) count / total) * 360f;

            piePaint.setColor(
                    emotionColors[i]
            );

            canvas.drawArc(
                    pieRect,
                    startAngle,
                    sweepAngle,
                    true,
                    piePaint
            );

            startAngle += sweepAngle;
        }

        drawCenterText(
                canvas,
                centerX,
                centerY,
                total
        );

        drawLegend(
                canvas,
                total
        );
    }

    private void drawCenterText(
            Canvas canvas,
            float centerX,
            float centerY,
            int total
    ) {

        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(28f);

        String totalText =
                String.valueOf(total);

        float totalWidth =
                textPaint.measureText(totalText);

        canvas.drawText(
                totalText,
                centerX - totalWidth / 2f,
                centerY + 8f,
                textPaint
        );
    }

    private void drawLegend(
            Canvas canvas,
            int total
    ) {

        float legendX = 18f;
        float legendStartY = 22f;

        float rowHeight = 34f;

        Paint legendPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        legendPaint.setStyle(
                Paint.Style.FILL
        );

        Paint legendTextPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        legendTextPaint.setColor(
                Color.DKGRAY
        );

        legendTextPaint.setTextSize(
                14f
        );

        for (int i = 0; i < emotionNames.length; i++) {

            float y =
                    legendStartY + (i * rowHeight);

            legendPaint.setColor(
                    emotionColors[i]
            );

            canvas.drawCircle(
                    legendX,
                    y - 5f,
                    7f,
                    legendPaint
            );

            String percentage = "0%";

            if (total > 0) {

                int percent =
                        Math.round(
                                ((float) emotionCounts[i]
                                        / total) * 100f
                        );

                percentage =
                        percent + "%";
            }

            String legendText =
                    emotionNames[i]
                            + "  "
                            + percentage;

            canvas.drawText(
                    legendText,
                    legendX + 15f,
                    y,
                    legendTextPaint
            );
        }
    }
}