package com.stratomercata;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * View component that displays asset information
 */
public class AssetsView extends View implements AssetsService.OnDataLoadedListener {
    private static final String TAG = "AssetsView";
    private static final int PADDING = 40;
    private static final int TEXT_SIZE_TITLE = 40;
    private static final int TEXT_SIZE_HEADER = 35;
    private static final int TEXT_SIZE_CONTENT = 30;
    private static final int ROW_HEIGHT = 60;
    private static final int TABLE_PADDING = 10;
    
    // Service for data loading and processing
    private AssetsService assetsService;
    
    // Data state
    private boolean dataLoaded = false;
    private String errorMessage = null;
    
    // UI
    private final Paint backgroundPaint;
    private final Paint titlePaint;
    private final Paint headerPaint;
    private final Paint contentPaint;
    private final Paint tableBorderPaint;
    private final Paint tableHeaderBgPaint;
    private final Paint tableRowBgPaint;
    private final Paint tableRowAltBgPaint;
    private final DecimalFormat priceFormat;
    private final DecimalFormat quantityFormat;
    private StaticLayout errorLayout;
    private TextPaint errorPaint;
    
    public AssetsView(Context context) {
        this(context, null);
    }
    
    public AssetsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // Initialize formatters
        priceFormat = new DecimalFormat("$#,##0.00");
        quantityFormat = new DecimalFormat("#,##0.######");
        
        // Initialize paints
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#F5F5F5"));
        
        titlePaint = new Paint();
        titlePaint.setColor(Color.parseColor("#333333"));
        titlePaint.setTextSize(TEXT_SIZE_TITLE);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setAntiAlias(true);
        
        headerPaint = new Paint();
        headerPaint.setColor(Color.parseColor("#555555"));
        headerPaint.setTextSize(TEXT_SIZE_HEADER);
        headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        headerPaint.setAntiAlias(true);
        
        contentPaint = new Paint();
        contentPaint.setColor(Color.parseColor("#333333"));
        contentPaint.setTextSize(TEXT_SIZE_CONTENT);
        contentPaint.setAntiAlias(true);
        
        tableBorderPaint = new Paint();
        tableBorderPaint.setColor(Color.parseColor("#DDDDDD"));
        tableBorderPaint.setStyle(Paint.Style.STROKE);
        tableBorderPaint.setStrokeWidth(2);
        tableBorderPaint.setAntiAlias(true);
        
        tableHeaderBgPaint = new Paint();
        tableHeaderBgPaint.setColor(Color.parseColor("#EEEEEE"));
        tableHeaderBgPaint.setStyle(Paint.Style.FILL);
        
        tableRowBgPaint = new Paint();
        tableRowBgPaint.setColor(Color.WHITE);
        tableRowBgPaint.setStyle(Paint.Style.FILL);
        
        tableRowAltBgPaint = new Paint();
        tableRowAltBgPaint.setColor(Color.parseColor("#F9F9F9"));
        tableRowAltBgPaint.setStyle(Paint.Style.FILL);
        
        errorPaint = new TextPaint();
        errorPaint.setColor(Color.RED);
        errorPaint.setTextSize(TEXT_SIZE_CONTENT);
        errorPaint.setAntiAlias(true);
        
        // Initialize service and load data
        initService(context);
    }
    
    private void initService(Context context) {
        // Create service and set listener
        assetsService = new AssetsService(context);
        assetsService.setOnDataLoadedListener(this);
        
        // Load data
        assetsService.loadData();
    }
    
    @Override
    public void onDataLoaded() {
        // Mark data as loaded and trigger redraw
        dataLoaded = true;
        invalidate();
        
        // Request layout to adjust the view height based on the number of assets
        requestLayout();
    }
    
    @Override
    public void onError(String message) {
        // Store error message and trigger redraw
        errorMessage = message;
        invalidate();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 800; // Default height
        
        if (dataLoaded) {
            // Calculate height based on the number of assets
            List<AssetsService.AssetGroup> sortedAssets = assetsService.getSortedAssets();
            height = PADDING * 4 + TEXT_SIZE_TITLE + TEXT_SIZE_HEADER * 2 + ROW_HEIGHT * 3 + 
                    PADDING * 2 + TEXT_SIZE_HEADER + TEXT_SIZE_CONTENT + PADDING + 
                    ROW_HEIGHT * (sortedAssets.size() + 1) + PADDING * 2;
        } else if (errorMessage != null) {
            // Height for error message
            height = 300;
        }
        
        setMeasuredDimension(width, height);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw background
        canvas.drawRect(0, 0, width, height, backgroundPaint);
        
        if (errorMessage != null) {
            // Draw error message
            if (errorLayout == null) {
                errorLayout = new StaticLayout(
                        errorMessage,
                        errorPaint,
                        width - PADDING * 2,
                        Layout.Alignment.ALIGN_NORMAL,
                        1.0f,
                        0.0f,
                        false
                );
            }
            
            canvas.save();
            canvas.translate(PADDING, PADDING);
            errorLayout.draw(canvas);
            canvas.restore();
            return;
        }
        
        if (!dataLoaded) {
            // Draw loading message
            canvas.drawText("Loading asset data...", PADDING, PADDING + TEXT_SIZE_CONTENT, contentPaint);
            return;
        }
        
        // Get data from service
        List<AssetsService.AssetGroup> sortedAssets = assetsService.getSortedAssets();
        int fungibleTokensCount = assetsService.getFungibleTokensCount();
        double fungibleTokensValue = assetsService.getFungibleTokensValue();
        int nonFungibleTokensCount = assetsService.getNonFungibleTokensCount();
        int cataTokensCount = assetsService.getCataTokensCount();
        double totalCataTokens = assetsService.getTotalCataTokens();
        String userCommonName = assetsService.getUserCommonName();
        Map<String, String> latestPrices = assetsService.getLatestPrices();
        
        // Draw title
        canvas.drawText("User Assets", PADDING, PADDING + TEXT_SIZE_TITLE, titlePaint);
        
        // Draw Total Value section
        int y = PADDING + TEXT_SIZE_TITLE + PADDING;
        canvas.drawText("Total Value:", PADDING, y + TEXT_SIZE_HEADER, headerPaint);
        
        // Draw total value table
        y += TEXT_SIZE_HEADER + PADDING;
        RectF totalValueTableRect = new RectF(PADDING, y, width - PADDING, y + ROW_HEIGHT * 4);
        canvas.drawRoundRect(totalValueTableRect, 5, 5, tableBorderPaint);
        
        // Draw table header
        RectF headerRect = new RectF(PADDING, y, width - PADDING, y + ROW_HEIGHT);
        canvas.drawRect(headerRect, tableHeaderBgPaint);
        
        // Draw header text
        float col1Width = (width - PADDING * 2) * 0.6f;
        canvas.drawText("Asset Type", PADDING + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, headerPaint);
        canvas.drawText("Value", PADDING + col1Width + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, headerPaint);
        
        // Draw vertical divider
        canvas.drawLine(PADDING + col1Width, y, PADDING + col1Width, y + ROW_HEIGHT * 4, tableBorderPaint);
        
        // Draw horizontal dividers
        for (int i = 1; i <= 3; i++) {
            canvas.drawLine(PADDING, y + ROW_HEIGHT * i, width - PADDING, y + ROW_HEIGHT * i, tableBorderPaint);
        }
        
        // Draw row data
        y += ROW_HEIGHT;
        
        // Row 1: Fungible tokens
        canvas.drawRect(PADDING, y, width - PADDING, y + ROW_HEIGHT, tableRowBgPaint);
        canvas.drawText(fungibleTokensCount + " Fungible tokens", PADDING + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, contentPaint);
        canvas.drawText("worth " + priceFormat.format(fungibleTokensValue), PADDING + col1Width + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, contentPaint);
        
        // Row 2: Non-fungible tokens
        y += ROW_HEIGHT;
        canvas.drawRect(PADDING, y, width - PADDING, y + ROW_HEIGHT, tableRowAltBgPaint);
        canvas.drawText(nonFungibleTokensCount + " non-fungible tokens", PADDING + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, contentPaint);
        canvas.drawText("(unknown value)", PADDING + col1Width + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, contentPaint);
        
        // Row 3: CATA tokens
        y += ROW_HEIGHT;
        canvas.drawRect(PADDING, y, width - PADDING, y + ROW_HEIGHT, tableRowBgPaint);
        canvas.drawText(cataTokensCount + " CATA tokens", PADDING + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, contentPaint);
        canvas.drawText(quantityFormat.format(totalCataTokens) + " CATA", PADDING + col1Width + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, contentPaint);
        
        // Draw Asset Breakdown section
        y += ROW_HEIGHT + PADDING * 2;
        canvas.drawText("Asset Breakdown:", PADDING, y + TEXT_SIZE_HEADER, headerPaint);
        
        if (sortedAssets.isEmpty()) {
            y += TEXT_SIZE_HEADER + PADDING;
            canvas.drawText("No assets found", PADDING, y + TEXT_SIZE_CONTENT, contentPaint);
            return;
        }
        
        // Draw asset count
        y += TEXT_SIZE_HEADER + PADDING;
        canvas.drawText("Found " + sortedAssets.size() + " unique asset classes (across " + 
                (fungibleTokensCount + nonFungibleTokensCount + cataTokensCount) + " tokens) for owner: " + 
                userCommonName, PADDING, y + TEXT_SIZE_CONTENT, contentPaint);
        
        // Draw asset breakdown table
        y += TEXT_SIZE_CONTENT + PADDING;
        drawAssetBreakdownTable(canvas, width, y, sortedAssets, latestPrices);
    }
    
    private void drawAssetBreakdownTable(Canvas canvas, int width, int y, List<AssetsService.AssetGroup> sortedAssets, Map<String, String> latestPrices) {
        // Draw table outline
        RectF assetTableRect = new RectF(PADDING, y, width - PADDING, y + ROW_HEIGHT * (sortedAssets.size() + 1));
        canvas.drawRoundRect(assetTableRect, 5, 5, tableBorderPaint);
        
        // Draw table header
        RectF assetHeaderRect = new RectF(PADDING, y, width - PADDING, y + ROW_HEIGHT);
        canvas.drawRect(assetHeaderRect, tableHeaderBgPaint);
        
        // Calculate column widths
        float assetCol1Width = (width - PADDING * 2) * 0.3f; // Asset Name
        float assetCol2Width = (width - PADDING * 2) * 0.25f; // Quantity
        float assetCol3Width = (width - PADDING * 2) * 0.25f; // Token Count
        float assetCol4Width = (width - PADDING * 2) * 0.2f; // Value
        
        // Draw header text
        canvas.drawText("Asset Name", PADDING + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, headerPaint);
        canvas.drawText("Quantity", PADDING + assetCol1Width + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, headerPaint);
        canvas.drawText("Token Count", PADDING + assetCol1Width + assetCol2Width + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, headerPaint);
        canvas.drawText("Value", PADDING + assetCol1Width + assetCol2Width + assetCol3Width + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, headerPaint);
        
        // Draw vertical dividers
        canvas.drawLine(PADDING + assetCol1Width, y, PADDING + assetCol1Width, y + ROW_HEIGHT * (sortedAssets.size() + 1), tableBorderPaint);
        canvas.drawLine(PADDING + assetCol1Width + assetCol2Width, y, PADDING + assetCol1Width + assetCol2Width, y + ROW_HEIGHT * (sortedAssets.size() + 1), tableBorderPaint);
        canvas.drawLine(PADDING + assetCol1Width + assetCol2Width + assetCol3Width, y, PADDING + assetCol1Width + assetCol2Width + assetCol3Width, y + ROW_HEIGHT * (sortedAssets.size() + 1), tableBorderPaint);
        
        // Draw horizontal dividers and row data
        y += ROW_HEIGHT;
        for (int i = 0; i < sortedAssets.size(); i++) {
            // Draw horizontal divider
            if (i > 0) {
                canvas.drawLine(PADDING, y, width - PADDING, y, tableBorderPaint);
            }
            
            // Draw row background (alternating)
            Paint rowBgPaint = (i % 2 == 0) ? tableRowBgPaint : tableRowAltBgPaint;
            canvas.drawRect(PADDING, y, width - PADDING, y + ROW_HEIGHT, rowBgPaint);
            
            // Get asset data
            AssetsService.AssetGroup asset = sortedAssets.get(i);
            
            // Format quantity
            String quantityDisplay = assetsService.calculateActualValue(asset.totalQuantity, asset.decimals);
            
            // Format token count
            String tokenCountDisplay = asset.tokenCount + " token" + (asset.tokenCount != 1 ? "s" : "");
            
            // Format value
            String valueDisplay = "N/A";
            String price = latestPrices.get(asset.name);
            if (price != null && asset.calculatedValue > 0) {
                if (asset.calculatedValue > 0 && asset.calculatedValue < 0.01) {
                    // For very small values, show at least $0.01
                    valueDisplay = "$0.01";
                } else {
                    // Round to the nearest cent
                    double roundedValue = Math.round(asset.calculatedValue * 100) / 100.0;
                    valueDisplay = priceFormat.format(roundedValue);
                }
            }
            
            // Draw asset data
            canvas.drawText(asset.name, PADDING + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, contentPaint);
            canvas.drawText(quantityDisplay, PADDING + assetCol1Width + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, contentPaint);
            canvas.drawText(tokenCountDisplay, PADDING + assetCol1Width + assetCol2Width + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, contentPaint);
            canvas.drawText(valueDisplay, PADDING + assetCol1Width + assetCol2Width + assetCol3Width + TABLE_PADDING, y + ROW_HEIGHT - TABLE_PADDING, contentPaint);
            
            y += ROW_HEIGHT;
        }
    }
}
