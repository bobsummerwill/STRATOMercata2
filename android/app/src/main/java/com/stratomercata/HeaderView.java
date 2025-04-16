package com.stratomercata;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class HeaderView extends View {
    // Constants
    private static final int PADDING = 40;
    private static final int BORDER_WIDTH = 2;
    private static final int LOGO_SIZE = 80;
    private static final int LOGO_BORDER_WIDTH = 4;
    
    // Paint objects
    private final Paint backgroundPaint;
    private final Paint stratoPaint;
    private final Paint mercataPaint;
    private final Paint subtitlePaint;
    private final Paint borderPaint;
    private final Paint logoBgPaint;
    private final Paint logoBorderPaint;
    private final Paint logoTextPaint;
    
    public HeaderView(Context context) {
        this(context, null);
    }
    
    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // Initialize paints
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        
        stratoPaint = new Paint();
        stratoPaint.setColor(Color.parseColor("#0066FF"));
        stratoPaint.setTextSize(50);
        stratoPaint.setTextAlign(Paint.Align.CENTER);
        stratoPaint.setFakeBoldText(true);
        
        mercataPaint = new Paint();
        mercataPaint.setColor(Color.BLACK);
        mercataPaint.setTextSize(55);
        mercataPaint.setTextAlign(Paint.Align.CENTER);
        mercataPaint.setFakeBoldText(true);
        
        subtitlePaint = new Paint();
        subtitlePaint.setColor(Color.parseColor("#555555"));
        subtitlePaint.setTextSize(30);
        subtitlePaint.setTextAlign(Paint.Align.CENTER);
        
        borderPaint = new Paint();
        borderPaint.setColor(Color.parseColor("#0066FF"));
        borderPaint.setStrokeWidth(BORDER_WIDTH);
        borderPaint.setStyle(Paint.Style.STROKE);
        
        logoBgPaint = new Paint();
        logoBgPaint.setColor(Color.parseColor("#0066FF"));
        logoBgPaint.setStyle(Paint.Style.FILL);
        
        logoBorderPaint = new Paint();
        logoBorderPaint.setColor(Color.parseColor("#FF3333"));
        logoBorderPaint.setStrokeWidth(LOGO_BORDER_WIDTH);
        logoBorderPaint.setStyle(Paint.Style.STROKE);
        
        logoTextPaint = new Paint();
        logoTextPaint.setColor(Color.WHITE);
        logoTextPaint.setTextSize(20);
        logoTextPaint.setTextAlign(Paint.Align.CENTER);
        logoTextPaint.setFakeBoldText(true);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw background
        Rect backgroundRect = new Rect(0, 0, width, height);
        canvas.drawRect(backgroundRect, backgroundPaint);
        
        // Draw STRATO
        canvas.drawText("STRATO", 
                width / 2f, height / 2f - 20, stratoPaint);
        
        // Draw MERCATA
        canvas.drawText("MERCATA", 
                width / 2f, height / 2f + 30, mercataPaint);
        
        // Draw subtitle
        canvas.drawText(getContext().getString(R.string.app_subtitle), 
                width / 2f, height / 2f + 70, subtitlePaint);
        
        // Draw bottom border
        canvas.drawLine(0, height - BORDER_WIDTH / 2f, width, height - BORDER_WIDTH / 2f, borderPaint);
        
        // Draw logo
        float logoX = width - PADDING - LOGO_SIZE / 2f;
        float logoY = PADDING + LOGO_SIZE / 2f;
        
        RectF logoRect = new RectF(
                logoX - LOGO_SIZE / 2f,
                logoY - LOGO_SIZE / 2f,
                logoX + LOGO_SIZE / 2f,
                logoY + LOGO_SIZE / 2f
        );
        
        canvas.drawCircle(logoX, logoY, LOGO_SIZE / 2f, logoBgPaint);
        canvas.drawCircle(logoX, logoY, LOGO_SIZE / 2f - LOGO_BORDER_WIDTH / 2f, logoBorderPaint);
        canvas.drawText("GOLDST", logoX, logoY + 8, logoTextPaint);
    }
}
