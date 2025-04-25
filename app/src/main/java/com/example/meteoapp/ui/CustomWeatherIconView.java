package com.example.meteoapp.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class CustomWeatherIconView extends View {

    private Paint sunPaint;
    private Paint cloudPaint;
    private Paint rainPaint;
    private String weatherCondition = "sunny";

    public CustomWeatherIconView(Context context) {
        super(context);
        init();
    }

    public CustomWeatherIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomWeatherIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        sunPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sunPaint.setColor(Color.YELLOW);
        sunPaint.setStyle(Paint.Style.FILL);

        cloudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cloudPaint.setColor(Color.LTGRAY);
        cloudPaint.setStyle(Paint.Style.FILL);

        rainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rainPaint.setColor(Color.BLUE);
        rainPaint.setStyle(Paint.Style.STROKE);
        rainPaint.setStrokeWidth(4f);
    }

    public void setWeatherCondition(String condition) {
        this.weatherCondition = (condition != null) ? condition.toLowerCase() : "unknown";
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 3;

        switch (weatherCondition) {
            case "sunny":
            case "clear":
                canvas.drawCircle(centerX, centerY, radius, sunPaint);
                break;
            case "clouds":
                canvas.drawCircle(centerX - radius / 2, centerY, radius * 0.8f, cloudPaint);
                canvas.drawCircle(centerX + radius / 2, centerY, radius * 0.7f, cloudPaint);
                canvas.drawRect(centerX - radius / 2, centerY, centerX + radius / 2, centerY + radius*0.7f, cloudPaint);
                break;
            case "rain":
                canvas.drawCircle(centerX - radius / 2, centerY - radius/3, radius * 0.7f, cloudPaint);
                canvas.drawCircle(centerX + radius / 2, centerY - radius/3, radius * 0.6f, cloudPaint);
                canvas.drawLine(centerX - radius / 3, centerY + radius/2, centerX - radius / 3 + 5, centerY + radius, rainPaint);
                canvas.drawLine(centerX + radius / 3, centerY + radius/2, centerX + radius / 3 + 5, centerY + radius, rainPaint);
                break;
            default:
                sunPaint.setColor(Color.GRAY);
                canvas.drawCircle(centerX, centerY, radius, sunPaint);
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }
}