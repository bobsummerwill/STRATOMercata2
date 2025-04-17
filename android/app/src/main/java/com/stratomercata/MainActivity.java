package com.stratomercata;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private HeaderView headerView;
    private GoldPriceView goldPriceView;
    private PriceChartView priceChartView;
    private TradeFormView tradeFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        headerView = findViewById(R.id.header_view);
        goldPriceView = findViewById(R.id.gold_price_view);
        priceChartView = findViewById(R.id.price_chart_view);
        tradeFormView = findViewById(R.id.trade_form_view);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (goldPriceView != null) {
            goldPriceView.startUpdates();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (goldPriceView != null) {
            goldPriceView.stopUpdates();
        }
    }
}
