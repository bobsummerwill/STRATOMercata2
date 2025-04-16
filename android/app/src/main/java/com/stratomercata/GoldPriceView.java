package com.stratomercata;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
import java.util.Random;

public class GoldPriceView extends View {
    // Constants
    private static final int UPDATE_INTERVAL = 5000; // 5 seconds
    private static final float MAX_PRICE_CHANGE = 2.5f;
    private static final int PADDING = 40;
    private static final int COIN_SIZE = 100;
    
    // State
    private float currentPrice = 1923.45f;
    private float priceChange = 12.30f;
    private boolean isPositiveChange = true;
    private final Handler handler;
    private final Runnable updateRunnable;
    private final Random random;
    private final DecimalFormat priceFormat;
    private final DecimalFormat changeFormat;
    private final DecimalFormat percentFormat;
    
    // Paint objects
    private final Paint backgroundPaint;
    private final Paint cardPaint;
    private final Paint labelPaint;
    private final Paint pricePaint;
    private final Paint positiveChangePaint;
    private final Paint negativeChangePaint;
    private final Paint goldIndicatorBgPaint;
    private final Paint goldIndicatorTextPaint;
    private final Paint coinPaint;
    private final Paint coinShadowPaint;
    
    public GoldPriceView(Context context) {
        this(context, null);
    }
    
    public GoldPriceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // Initialize formatters
        priceFormat = new DecimalFormat("$#,##0.00");
        changeFormat = new DecimalFormat("+#,##0.00;-#,##0.00");
        percentFormat = new DecimalFormat("+#,##0.00%;-#,##0.00%");
        
        // Initialize paints
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#F5F5F5"));
        
        cardPaint = new Paint();
        cardPaint.setColor(Color.WHITE);
        cardPaint.setShadowLayer(5, 0, 2, Color.parseColor("#20000000"));
        cardPaint.setAntiAlias(true);
        
        labelPaint = new Paint();
        labelPaint.setColor(Color.parseColor("#555555"));
        labelPaint.setTextSize(35);
        labelPaint.setFakeBoldText(true);
        
        pricePaint = new Paint();
        pricePaint.setColor(Color.BLACK);
        pricePaint.setTextSize(80);
        pricePaint.setTextAlign(Paint.Align.CENTER);
        pricePaint.setFakeBoldText(true);
        
        positiveChangePaint = new Paint();
        positiveChangePaint.setColor(Color.parseColor("#4caf50"));
        positiveChangePaint.setTextSize(40);
        positiveChangePaint.setTextAlign(Paint.Align.CENTER);
        positiveChangePaint.setFakeBoldText(true);
        
        negativeChangePaint = new Paint();
        negativeChangePaint.setColor(Color.parseColor("#f44336"));
        negativeChangePaint.setTextSize(40);
        negativeChangePaint.setTextAlign(Paint.Align.CENTER);
        negativeChangePaint.setFakeBoldText(true);
        
        goldIndicatorBgPaint = new Paint();
        goldIndicatorBgPaint.setColor(Color.parseColor("#0066FF"));
        goldIndicatorBgPaint.setStyle(Paint.Style.FILL);
        goldIndicatorBgPaint.setAntiAlias(true);
        
        goldIndicatorTextPaint = new Paint();
        goldIndicatorTextPaint.setColor(Color.WHITE);
        goldIndicatorTextPaint.setTextSize(30);
        goldIndicatorTextPaint.setTextAlign(Paint.Align.CENTER);
        goldIndicatorTextPaint.setFakeBoldText(true);
        
        coinPaint = new Paint();
        coinPaint.setColor(Color.parseColor("#FFD700"));
        coinPaint.setStyle(Paint.Style.FILL);
        coinPaint.setAntiAlias(true);
        
        coinShadowPaint = new Paint();
        coinShadowPaint.setColor(Color.parseColor("#50000000"));
        coinShadowPaint.setStyle(Paint.Style.FILL);
        coinShadowPaint.setAntiAlias(true);
        
        // Initialize random number generator
        random = new Random();
        
        // Set up price update handler
        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updatePrice();
                invalidate();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
    }
    
    private void updatePrice() {
        // Generate random price fluctuation between -MAX_PRICE_CHANGE and +MAX_PRICE_CHANGE
        float fluctuation = (random.nextFloat() * (MAX_PRICE_CHANGE * 2)) - MAX_PRICE_CHANGE;
        
        // Update price and change
        currentPrice += fluctuation;
        priceChange = fluctuation;
        isPositiveChange = fluctuation >= 0;
    }
    
    public void startUpdates() {
        handler.post(updateRunnable);
    }
    
    public void stopUpdates() {
        handler.removeCallbacks(updateRunnable);
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
        
        // Draw header row with label and gold indicator
        String label = getContext().getString(R.string.gold_price_label);
        canvas.drawText(label, PADDING * 2, PADDING * 2, labelPaint);
        
        // Draw GOLD indicator
        RectF goldIndicatorRect = new RectF(
                width - PADDING * 4,
                PADDING * 1.5f,
                width - PADDING * 1.5f,
                PADDING * 2.5f
        );
        canvas.drawRoundRect(goldIndicatorRect, 10, 10, goldIndicatorBgPaint);
        canvas.drawText("GOLD", width - PADDING * 2.75f, PADDING * 2.1f, goldIndicatorTextPaint);
        
        // Draw price
        String priceText = priceFormat.format(currentPrice);
        canvas.drawText(priceText, width / 2f, height * 0.45f, pricePaint);
        
        // Draw change
        String changeText = changeFormat.format(priceChange);
        float percentChange = priceChange / (currentPrice - priceChange);
        String percentText = percentFormat.format(percentChange);
        String fullChangeText = changeText + " (" + percentText + ")";
        
        Paint changePaint = isPositiveChange ? positiveChangePaint : negativeChangePaint;
        canvas.drawText(fullChangeText, width / 2f, height * 0.55f, changePaint);
        
        // Draw gold coin
        float coinX = width / 2f;
        float coinY = height * 0.75f;
        
        // Draw coin shadow
        canvas.drawCircle(coinX + 5, coinY + 5, COIN_SIZE / 2f, coinShadowPaint);
        
        // Draw coin
        canvas.drawCircle(coinX, coinY, COIN_SIZE / 2f, coinPaint);
    }
}
