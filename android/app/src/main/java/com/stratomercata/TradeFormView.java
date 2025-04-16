package com.stratomercata;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.text.DecimalFormat;

public class TradeFormView extends View {
    // Constants
    private static final int PADDING = 40;
    private static final int BUTTON_PADDING = 20;
    private static final int BUTTON_RADIUS = 10;
    private static final int INPUT_HEIGHT = 100;
    private static final int INPUT_RADIUS = 8;
    private static final int BUTTON_HEIGHT = 120;
    
    // State
    private boolean isBuySelected = true;
    private float amount = 1.0f;
    private float currentPrice = 1923.45f;
    private boolean isAmountInputActive = false;
    
    // UI elements
    private RectF buyButton;
    private RectF sellButton;
    private RectF amountInput;
    private RectF priceDisplay;
    private RectF executeButton;
    private RectF formSection;
    private RectF disclaimerBox;
    
    // Paint objects
    private final Paint backgroundPaint;
    private final Paint cardPaint;
    private final Paint titlePaint;
    private final Paint buyButtonPaint;
    private final Paint sellButtonPaint;
    private final Paint inactiveButtonPaint;
    private final Paint buttonTextPaint;
    private final Paint activeButtonTextPaint;
    private final Paint formSectionPaint;
    private final Paint inputBackgroundPaint;
    private final Paint inputTextPaint;
    private final Paint labelPaint;
    private final Paint buyExecuteButtonPaint;
    private final Paint sellExecuteButtonPaint;
    private final Paint executeButtonTextPaint;
    private final Paint disclaimerBoxPaint;
    private final Paint disclaimerBorderPaint;
    private final Paint disclaimerTextPaint;
    private final Paint tradeIndicatorBgPaint;
    private final Paint tradeIndicatorTextPaint;
    
    // Formatters
    private final DecimalFormat amountFormat;
    private final DecimalFormat priceFormat;
    
    public TradeFormView(Context context) {
        this(context, null);
    }
    
    public TradeFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // Initialize formatters
        amountFormat = new DecimalFormat("#,##0.00");
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
        titlePaint.setTextSize(45);
        titlePaint.setFakeBoldText(true);
        
        buyButtonPaint = new Paint();
        buyButtonPaint.setColor(Color.parseColor("#4CAF50"));
        buyButtonPaint.setStyle(Paint.Style.FILL);
        buyButtonPaint.setAntiAlias(true);
        
        sellButtonPaint = new Paint();
        sellButtonPaint.setColor(Color.parseColor("#F44336"));
        sellButtonPaint.setStyle(Paint.Style.FILL);
        sellButtonPaint.setAntiAlias(true);
        
        inactiveButtonPaint = new Paint();
        inactiveButtonPaint.setColor(Color.TRANSPARENT);
        inactiveButtonPaint.setStyle(Paint.Style.FILL);
        inactiveButtonPaint.setAntiAlias(true);
        
        buttonTextPaint = new Paint();
        buttonTextPaint.setColor(Color.parseColor("#555555"));
        buttonTextPaint.setTextSize(35);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
        buttonTextPaint.setFakeBoldText(true);
        
        activeButtonTextPaint = new Paint();
        activeButtonTextPaint.setColor(Color.WHITE);
        activeButtonTextPaint.setTextSize(35);
        activeButtonTextPaint.setTextAlign(Paint.Align.CENTER);
        activeButtonTextPaint.setFakeBoldText(true);
        
        formSectionPaint = new Paint();
        formSectionPaint.setColor(Color.parseColor("#F9F9F9"));
        formSectionPaint.setStyle(Paint.Style.FILL);
        formSectionPaint.setAntiAlias(true);
        
        inputBackgroundPaint = new Paint();
        inputBackgroundPaint.setColor(Color.WHITE);
        inputBackgroundPaint.setStyle(Paint.Style.FILL);
        inputBackgroundPaint.setAntiAlias(true);
        
        inputTextPaint = new TextPaint();
        inputTextPaint.setColor(Color.BLACK);
        inputTextPaint.setTextSize(40);
        inputTextPaint.setTextAlign(Paint.Align.RIGHT);
        inputTextPaint.setFakeBoldText(true);
        
        labelPaint = new Paint();
        labelPaint.setColor(Color.parseColor("#555555"));
        labelPaint.setTextSize(30);
        labelPaint.setFakeBoldText(true);
        
        buyExecuteButtonPaint = new Paint();
        buyExecuteButtonPaint.setColor(Color.parseColor("#4CAF50"));
        buyExecuteButtonPaint.setStyle(Paint.Style.FILL);
        buyExecuteButtonPaint.setAntiAlias(true);
        
        sellExecuteButtonPaint = new Paint();
        sellExecuteButtonPaint.setColor(Color.parseColor("#F44336"));
        sellExecuteButtonPaint.setStyle(Paint.Style.FILL);
        sellExecuteButtonPaint.setAntiAlias(true);
        
        executeButtonTextPaint = new Paint();
        executeButtonTextPaint.setColor(Color.WHITE);
        executeButtonTextPaint.setTextSize(40);
        executeButtonTextPaint.setTextAlign(Paint.Align.CENTER);
        executeButtonTextPaint.setFakeBoldText(true);
        
        disclaimerBoxPaint = new Paint();
        disclaimerBoxPaint.setColor(Color.parseColor("#F9F9F9"));
        disclaimerBoxPaint.setStyle(Paint.Style.FILL);
        disclaimerBoxPaint.setAntiAlias(true);
        
        disclaimerBorderPaint = new Paint();
        disclaimerBorderPaint.setColor(Color.parseColor("#0066FF"));
        disclaimerBorderPaint.setStyle(Paint.Style.STROKE);
        disclaimerBorderPaint.setStrokeWidth(6);
        disclaimerBorderPaint.setAntiAlias(true);
        
        disclaimerTextPaint = new Paint();
        disclaimerTextPaint.setColor(Color.parseColor("#666666"));
        disclaimerTextPaint.setTextSize(25);
        disclaimerTextPaint.setTextAlign(Paint.Align.LEFT);
        disclaimerTextPaint.setFakeBoldText(false);
        
        tradeIndicatorBgPaint = new Paint();
        tradeIndicatorBgPaint.setColor(Color.parseColor("#0066FF"));
        tradeIndicatorBgPaint.setStyle(Paint.Style.FILL);
        tradeIndicatorBgPaint.setAntiAlias(true);
        
        tradeIndicatorTextPaint = new Paint();
        tradeIndicatorTextPaint.setColor(Color.WHITE);
        tradeIndicatorTextPaint.setTextSize(30);
        tradeIndicatorTextPaint.setTextAlign(Paint.Align.CENTER);
        tradeIndicatorTextPaint.setFakeBoldText(true);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Card background
        RectF cardRect = new RectF(PADDING, PADDING, w - PADDING, h - PADDING);
        
        int buttonWidth = (w - PADDING * 4) / 2;
        
        // Buy/Sell buttons container
        RectF buttonContainer = new RectF(
                PADDING * 2,
                PADDING * 4,
                w - PADDING * 2,
                PADDING * 4 + BUTTON_HEIGHT
        );
        
        // Buy/Sell buttons
        buyButton = new RectF(
                buttonContainer.left + BUTTON_PADDING,
                buttonContainer.top + BUTTON_PADDING,
                buttonContainer.left + buttonWidth,
                buttonContainer.bottom - BUTTON_PADDING
        );
        
        sellButton = new RectF(
                buttonContainer.right - buttonWidth,
                buttonContainer.top + BUTTON_PADDING,
                buttonContainer.right - BUTTON_PADDING,
                buttonContainer.bottom - BUTTON_PADDING
        );
        
        // Form section
        formSection = new RectF(
                PADDING * 2,
                buttonContainer.bottom + PADDING,
                w - PADDING * 2,
                buttonContainer.bottom + PADDING * 6 + INPUT_HEIGHT * 2
        );
        
        // Amount input
        amountInput = new RectF(
                formSection.left + PADDING,
                formSection.top + PADDING * 2,
                formSection.right - PADDING,
                formSection.top + PADDING * 2 + INPUT_HEIGHT
        );
        
        // Price display
        priceDisplay = new RectF(
                formSection.left + PADDING,
                amountInput.bottom + PADDING * 2,
                formSection.right - PADDING,
                amountInput.bottom + PADDING * 2 + INPUT_HEIGHT
        );
        
        // Execute button
        executeButton = new RectF(
                PADDING * 2,
                formSection.bottom + PADDING,
                w - PADDING * 2,
                formSection.bottom + PADDING + BUTTON_HEIGHT
        );
        
        // Disclaimer box
        disclaimerBox = new RectF(
                PADDING * 2,
                executeButton.bottom + PADDING,
                w - PADDING * 2,
                executeButton.bottom + PADDING * 3
        );
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
        
        // Draw header with title and trade indicator
        String title = isBuySelected ? getContext().getString(R.string.buy) : getContext().getString(R.string.sell);
        canvas.drawText(title + " Gold", PADDING * 2, PADDING * 2.5f, titlePaint);
        
        // Draw TRADE indicator
        RectF tradeIndicatorRect = new RectF(
                width - PADDING * 4,
                PADDING * 1.5f,
                width - PADDING * 1.5f,
                PADDING * 2.5f
        );
        canvas.drawRoundRect(tradeIndicatorRect, 10, 10, tradeIndicatorBgPaint);
        canvas.drawText("TRADE", width - PADDING * 2.75f, PADDING * 2.1f, tradeIndicatorTextPaint);
        
        // Draw button container
        RectF buttonContainer = new RectF(
                PADDING * 2,
                PADDING * 4,
                width - PADDING * 2,
                PADDING * 4 + BUTTON_HEIGHT
        );
        canvas.drawRoundRect(buttonContainer, BUTTON_RADIUS, BUTTON_RADIUS, formSectionPaint);
        
        // Draw Buy/Sell buttons
        canvas.drawRoundRect(buyButton, BUTTON_RADIUS, BUTTON_RADIUS, 
                isBuySelected ? buyButtonPaint : inactiveButtonPaint);
        canvas.drawText(getContext().getString(R.string.buy), 
                buyButton.centerX(), buyButton.centerY() + 15, 
                isBuySelected ? activeButtonTextPaint : buttonTextPaint);
        
        canvas.drawRoundRect(sellButton, BUTTON_RADIUS, BUTTON_RADIUS, 
                !isBuySelected ? sellButtonPaint : inactiveButtonPaint);
        canvas.drawText(getContext().getString(R.string.sell), 
                sellButton.centerX(), sellButton.centerY() + 15, 
                !isBuySelected ? activeButtonTextPaint : buttonTextPaint);
        
        // Draw form section
        canvas.drawRoundRect(formSection, BUTTON_RADIUS, BUTTON_RADIUS, formSectionPaint);
        
        // Draw amount input
        canvas.drawText(getContext().getString(R.string.amount), 
                amountInput.left, amountInput.top - PADDING / 2, labelPaint);
        canvas.drawRoundRect(amountInput, INPUT_RADIUS, INPUT_RADIUS, inputBackgroundPaint);
        canvas.drawText(amountFormat.format(amount), 
                amountInput.right - PADDING, amountInput.centerY() + 15, inputTextPaint);
        
        // Draw price display
        canvas.drawText(getContext().getString(R.string.price), 
                priceDisplay.left, priceDisplay.top - PADDING / 2, labelPaint);
        canvas.drawRoundRect(priceDisplay, INPUT_RADIUS, INPUT_RADIUS, inputBackgroundPaint);
        
        float totalPrice = amount * currentPrice;
        canvas.drawText(priceFormat.format(totalPrice), 
                priceDisplay.right - PADDING, priceDisplay.centerY() + 15, inputTextPaint);
        
        // Draw execute button
        Paint executeButtonPaint = isBuySelected ? buyExecuteButtonPaint : sellExecuteButtonPaint;
        canvas.drawRoundRect(executeButton, BUTTON_RADIUS, BUTTON_RADIUS, executeButtonPaint);
        canvas.drawText(getContext().getString(R.string.execute), 
                executeButton.centerX(), executeButton.centerY() + 15, executeButtonTextPaint);
        
        // Draw disclaimer
        canvas.drawRoundRect(disclaimerBox, BUTTON_RADIUS, BUTTON_RADIUS, disclaimerBoxPaint);
        canvas.drawRect(
                disclaimerBox.left, 
                disclaimerBox.top, 
                disclaimerBox.left + 6, 
                disclaimerBox.bottom, 
                disclaimerBorderPaint);
        
        String disclaimer = "Trading involves risk. Please ensure you understand the risks before trading.";
        canvas.drawText(disclaimer, 
                disclaimerBox.left + PADDING, disclaimerBox.centerY() + 10, disclaimerTextPaint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (buyButton.contains(x, y)) {
                    isBuySelected = true;
                    invalidate();
                    return true;
                } else if (sellButton.contains(x, y)) {
                    isBuySelected = false;
                    invalidate();
                    return true;
                } else if (amountInput.contains(x, y)) {
                    isAmountInputActive = true;
                    // In a real app, this would show a number input dialog
                    // For this demo, we'll just increment the amount
                    amount += 0.5f;
                    invalidate();
                    return true;
                } else if (executeButton.contains(x, y)) {
                    executeTrade();
                    return true;
                }
                break;
        }
        
        return super.onTouchEvent(event);
    }
    
    private void executeTrade() {
        String action = isBuySelected ? "Bought" : "Sold";
        float totalPrice = amount * currentPrice;
        
        String message = action + " " + amountFormat.format(amount) + " oz of gold for " + 
                priceFormat.format(totalPrice);
        
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
    
    public void setCurrentPrice(float price) {
        this.currentPrice = price;
        invalidate();
    }
}
