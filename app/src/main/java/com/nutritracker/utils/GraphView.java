package com.nutritracker.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphView extends View {

    private Paint linePaint;
    private Paint pointPaint;
    private Paint gridPaint;
    private Path path;
    private List<Float> dataPoints = new ArrayList<>();
    private float maxData = 100f;

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#FF5722"));
        linePaint.setStrokeWidth(6f);
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.parseColor("#FF5722"));
        pointPaint.setStyle(Paint.Style.FILL);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(2f);
        gridPaint.setStyle(Paint.Style.STROKE);

        path = new Path();
    }

    public void setData(List<Float> points, String colorHex) {
        this.dataPoints = points;
        if (points != null && !points.isEmpty()) {
            maxData = Collections.max(points);
            if (maxData == 0) maxData = 10f; // Scale bump
        }
        
        linePaint.setColor(Color.parseColor(colorHex));
        pointPaint.setColor(Color.parseColor(colorHex));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataPoints.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float padding = 40f;
        
        float usableWidth = width - (padding * 2);
        float usableHeight = height - (padding * 2);

        canvas.drawLine(padding, height - padding, width - padding, height - padding, gridPaint);

        path.reset();
        
        float xStep = usableWidth / Math.max(1, (dataPoints.size() - 1));

        for (int i = 0; i < dataPoints.size(); i++) {
            float x = padding + (i * xStep);
            float yRatio = dataPoints.get(i) / maxData;
            float y = (height - padding) - (yRatio * usableHeight);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            canvas.drawCircle(x, y, 8f, pointPaint);
        }

        canvas.drawPath(path, linePaint);
    }
}
