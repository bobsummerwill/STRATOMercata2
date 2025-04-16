package com.stratomercata;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PriceChartView extends View {
    // Constants
    private static final int PADDING = 40;
    private static final int CHART_PADDING = 50;
    private static final int AXIS_LABEL_PADDING = 10;
    private static final int POINT_RADIUS = 6;
    private static final int LINE_WIDTH = 3;
    private static final int PERIOD_BUTTON_PADDING = 10;
    private static final int PERIOD_BUTTON_RADIUS = 8;
    private static final int HEX_SIZE = 15;
    
    // Mock data for the chart
    private final float[] priceData = {
        1910.25f, 1915.50f, 1920.75f, 1918.30f, 1922.45f, 
        1925.10f, 1923.80f, 1928.65f, 1930.20f, 1927.90f,
        1932.40f, 1935.75f, 1933.25f, 1936.80f, 1940.15f
    };
    
    private final String[] periodOptions = {"15D", "1M", "3M", "6M", "1Y", "ALL"};
    private int selectedPeriodIndex = 0;
    
    // Paint objects
    private final Paint backgroundPaint;
    private final Paint cardPaint;
    private final Paint titlePaint;
    private final Paint axisPaint;
    private final Paint axisLabelPaint;
    private final Paint linePaint;
    private final Paint pointPaint;
    private final Paint periodButtonPaint;
    private final Paint selectedPeriodButtonPaint;
    private final Paint periodTextPaint;
    private final Paint selectedPeriodTextPaint;
    private final Paint chartBgPaint;
    private final Paint hexagonPaint;
    private final Paint chartIndicatorBgPaint;
    private final Paint chartIndicatorTextPaint;
    
    // Formatters
    private final DecimalFormat priceFormat;
    
    public PriceChartView(Context context) {
        this(context, null);
    }
    
    public PriceChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // Initialize formatters
        priceFormat = new DecimalFormat("$#,##0.00");
        
        // Initialize paints
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#F5F5F5"));
        
        cardPaint = new Paint();
        cardPaint.setColor(Color.WHITE);
        cardPaint.setShadowLayer(5, 0, 2, Color.parseColor("#20000000"));
        cardPaint.setAntiAlias(true);
        
        titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(40);
        titlePaint.setFakeBoldText(true);
        
        axisPaint = new Paint();
        axisPaint.setColor(Color.parseColor("#DDDDDD"));
        axisPaint.setStrokeWidth(2);
        axisPaint.setStyle(Paint.Style.STROKE);
        
        axisLabelPaint = new Paint();
        axisLabelPaint.setColor(Color.parseColor("#555555"));
        axisLabelPaint.setTextSize(30);
        
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#0066FF"));
        linePaint.setStrokeWidth(LINE_WIDTH);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        
        pointPaint = new Paint();
        pointPaint.setColor(Color.parseColor("#0066FF"));
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);
        
        periodButtonPaint = new Paint();
        periodButtonPaint.setColor(Color.parseColor("#F5F5F5"));
        periodButtonPaint.setStyle(Paint.Style.FILL);
        periodButtonPaint.setAntiAlias(true);
        
        selectedPeriodButtonPaint = new Paint();
        selectedPeriodButtonPaint.setColor(Color.parseColor("#0066FF"));
        selectedPeriodButtonPaint.setStyle(Paint.Style.FILL);
        selectedPeriodButtonPaint.setAntiAlias(true);
        
        periodTextPaint = new Paint();
        periodTextPaint.setColor(Color.parseColor("#555555"));
        periodTextPaint.setTextSize(30);
        periodTextPaint.setTextAlign(Paint.Align.CENTER);
        
        selectedPeriodTextPaint = new Paint();
        selectedPeriodTextPaint.setColor(Color.WHITE);
        selectedPeriodTextPaint.setTextSize(30);
        selectedPeriodTextPaint.setFakeBoldText(true);
        selectedPeriodTextPaint.setTextAlign(Paint.Align.CENTER);
        
        chartBgPaint = new Paint();
        chartBgPaint.setColor(Color.parseColor("#F9F9F9"));
        chartBgPaint.setStyle(Paint.Style.FILL);
        
        hexagonPaint = new Paint();
        hexagonPaint.setColor(Color.parseColor("#EEEEEE"));
        hexagonPaint.setStyle(Paint.Style.FILL);
        hexagonPaint.setAntiAlias(true);
        
        chartIndicatorBgPaint = new Paint();
        chartIndicatorBgPaint.setColor(Color.parseColor("#0066FF"));
        chartIndicatorBgPaint.setStyle(Paint.Style.FILL);
        chartIndicatorBgPaint.setAntiAlias(true);
        
        chartIndicatorTextPaint = new Paint();
        chartIndicatorTextPaint.setColor(Color.WHITE);
        chartIndicatorTextPaint.setTextSize(30);
        chartIndicatorTextPaint.setTextAlign(Paint.Align.CENTER);
        chartIndicatorTextPaint.setFakeBoldText(true);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw background
        Rect backgroundRect = new Rect(0, 0, width, height);
        canvas.drawRect(backgroundRect, backgroundPaint);
        
        // Draw card background
        RectF cardRect = new RectF(PADDING, PADDING, width - PADDING, height - PADDING);
        canvas.drawRoundRect(cardRect, 20, 20, cardPaint);
        
        // Draw header row with title and period indicator
        String title = getContext().getString(R.string.chart_title);
        canvas.drawText(title, PADDING * 2, PADDING * 2, titlePaint);
        
        // Draw period indicator
        RectF periodIndicatorRect = new RectF(
                width - PADDING * 4,
                PADDING * 1.5f,
                width - PADDING * 1.5f,
                PADDING * 2.5f
        );
        canvas.drawRoundRect(periodIndicatorRect, 10, 10, chartIndicatorBgPaint);
        canvas.drawText("15 DAYS", width - PADDING * 2.75f, PADDING * 2.1f, chartIndicatorTextPaint);
        
        // Calculate chart area
        int chartTop = PADDING * 3;
        int chartBottom = height - PADDING * 4; // Extra space for period buttons
        int chartLeft = PADDING * 3; // Extra space for Y-axis labels
        int chartRight = width - (int)(PADDING * 1.5f);
        int chartWidth = chartRight - chartLeft;
        int chartHeight = chartBottom - chartTop;
        
        // Draw chart background with hexagon pattern
        RectF chartBgRect = new RectF(chartLeft, chartTop, chartRight, chartBottom);
        canvas.drawRoundRect(chartBgRect, 10, 10, chartBgPaint);
        
        // Draw hexagon pattern
        int rows = (int) Math.ceil(chartHeight / (HEX_SIZE * 1.5f));
        int cols = (int) Math.ceil(chartWidth / (HEX_SIZE * 1.732f));
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float offsetX = chartLeft + c * HEX_SIZE * 1.732f;
                float offsetY = chartTop + r * HEX_SIZE * 1.5f;
                // Offset every other row
                float adjustedX = r % 2 == 0 ? offsetX : offsetX + HEX_SIZE * 0.866f;
                
                // Draw hexagon
                Path hexPath = new Path();
                for (int i = 0; i < 6; i++) {
                    float angle = (float) (Math.PI / 3 * i);
                    float x = adjustedX + HEX_SIZE * (float) Math.cos(angle);
                    float y = offsetY + HEX_SIZE * (float) Math.sin(angle);
                    
                    if (i == 0) {
                        hexPath.moveTo(x, y);
                    } else {
                        hexPath.lineTo(x, y);
                    }
                }
                hexPath.close();
                canvas.drawPath(hexPath, hexagonPaint);
            }
        }
        
        // Find min and max for scaling
        float minPrice = Float.MAX_VALUE;
        float maxPrice = Float.MIN_VALUE;
        for (float price : priceData) {
            minPrice = Math.min(minPrice, price);
            maxPrice = Math.max(maxPrice, price);
        }
        float priceRange = maxPrice - minPrice;
        
        // Add some padding to the min/max
        minPrice -= priceRange * 0.05f;
        maxPrice += priceRange * 0.05f;
        priceRange = maxPrice - minPrice;
        
        // Draw Y-axis labels
        canvas.drawText(priceFormat.format(maxPrice), PADDING * 1.5f, chartTop + AXIS_LABEL_PADDING, axisLabelPaint);
        canvas.drawText(priceFormat.format((maxPrice + minPrice) / 2), PADDING * 1.5f, chartTop + chartHeight / 2 + AXIS_LABEL_PADDING, axisLabelPaint);
        canvas.drawText(priceFormat.format(minPrice), PADDING * 1.5f, chartBottom + AXIS_LABEL_PADDING, axisLabelPaint);
        
        // Draw axes
        canvas.drawLine(chartLeft, chartTop, chartLeft, chartBottom, axisPaint);
        canvas.drawLine(chartLeft, chartBottom, chartRight, chartBottom, axisPaint);
        
        // Calculate points for the chart
        List<Float> xPoints = new ArrayList<>();
        List<Float> yPoints = new ArrayList<>();
        
        for (int i = 0; i < priceData.length; i++) {
            float x = chartLeft + ((float) i / (priceData.length - 1)) * chartWidth;
            float y = chartBottom - ((priceData[i] - minPrice) / priceRange) * chartHeight;
            xPoints.add(x);
            yPoints.add(y);
        }
        
        // Draw the line chart
        Path linePath = new Path();
        linePath.moveTo(xPoints.get(0), yPoints.get(0));
        for (int i = 1; i < xPoints.size(); i++) {
            linePath.lineTo(xPoints.get(i), yPoints.get(i));
        }
        canvas.drawPath(linePath, linePaint);
        
        // Draw points
        for (int i = 0; i < xPoints.size(); i++) {
            canvas.drawCircle(xPoints.get(i), yPoints.get(i), POINT_RADIUS, pointPaint);
        }
        
        // Draw period selector
        int periodButtonY = height - PADDING * 2;
        int buttonWidth = (width - PADDING * 2) / periodOptions.length;
        
        for (int i = 0; i < periodOptions.length; i++) {
            int buttonLeft = PADDING + i * buttonWidth;
            int buttonRight = PADDING + (i + 1) * buttonWidth;
            int buttonCenterX = (buttonLeft + buttonRight) / 2;
            
            RectF buttonRect = new RectF(
                    buttonLeft + PERIOD_BUTTON_PADDING,
                    periodButtonY - 40,
                    buttonRight - PERIOD_BUTTON_PADDING,
                    periodButtonY
            );
            
            // Draw button background
            Paint buttonPaint = i == selectedPeriodIndex ? selectedPeriodButtonPaint : periodButtonPaint;
            Paint textPaint = i == selectedPeriodIndex ? selectedPeriodTextPaint : periodTextPaint;
            
            canvas.drawRoundRect(buttonRect, PERIOD_BUTTON_RADIUS, PERIOD_BUTTON_RADIUS, buttonPaint);
            canvas.drawText(periodOptions[i], buttonCenterX, periodButtonY - 15, textPaint);
        }
    }
}
