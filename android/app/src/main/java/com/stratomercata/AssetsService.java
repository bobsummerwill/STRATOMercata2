package com.stratomercata;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Service class that handles API calls and data processing for assets
 */
public class AssetsService {
    private static final String TAG = "AssetsService";
    
    // Credentials
    private String clientUrl;
    private String userCommonName;
    private String clientId;
    private String clientSecret;
    private String accessToken;
    private long tokenExpiresAt;
    private static final long TOKEN_LIFETIME_RESERVE_SECONDS = 120; // Reserve 2 minutes for token expiration check
    
    // API client
    private ApiService apiService;
    
    // Data
    private List<Asset> assets = new ArrayList<>();
    private List<AssetGroup> sortedAssets = new ArrayList<>();
    private Map<String, String> latestPrices = new HashMap<>();
    private int fungibleTokensCount = 0;
    private double fungibleTokensValue = 0;
    private int nonFungibleTokensCount = 0;
    private int cataTokensCount = 0;
    private double totalCataTokens = 0;
    
    // Listener for data loading events
    private OnDataLoadedListener dataLoadedListener;
    
    // Model classes
    public static class Asset {
        @SerializedName("id")
        public String id;
        
        @SerializedName("name")
        public String name;
        
        @SerializedName("quantity")
        public String quantity;
        
        @SerializedName("decimals")
        public Integer decimals;
    }
    
    public static class Oracle {
        @SerializedName("name")
        public String name;
        
        @SerializedName("consensusPrice")
        public String consensusPrice;
    }
    
    public static class AssetGroup {
        public String name;
        public long totalQuantity;
        public int tokenCount;
        public int decimals;
        public double calculatedQuantity;
        public double calculatedValue;
        public List<Asset> tokens = new ArrayList<>();
    }
    
    // API interface
    public interface ApiService {
        @GET("BlockApps-Mercata-Asset")
        Call<List<Asset>> getAssets(@Query("ownerCommonName") String ownerCommonName);
        
        @GET("BlockApps-Mercata-OracleService")
        Call<List<Oracle>> getOracleValues();
    }
    
    // Interface for data loading events
    public interface OnDataLoadedListener {
        void onDataLoaded();
        void onError(String errorMessage);
    }
    
    public AssetsService(Context context) {
        // Load credentials
        loadCredentials(context);
        
        // Initialize API client
        initApiClient();
    }
    
    public void setOnDataLoadedListener(OnDataLoadedListener listener) {
        this.dataLoadedListener = listener;
    }
    
    private void loadCredentials(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("credentials.yaml");
            Yaml yaml = new Yaml();
            Map<String, Object> credentials = yaml.load(inputStream);
            
            clientUrl = (String) credentials.get("clientUrl");
            userCommonName = (String) credentials.get("userCommonName");
            clientId = (String) credentials.get("clientId");
            clientSecret = (String) credentials.get("clientSecret");
            
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error loading credentials: " + e.getMessage());
            if (dataLoadedListener != null) {
                dataLoadedListener.onError("Error loading credentials: " + e.getMessage());
            }
        }
    }
    
    private void initApiClient() {
        if (clientUrl == null) {
            Log.e(TAG, "Client URL is null, cannot initialize API client");
            return;
        }
        
        // Create OkHttp client with token interceptor
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request original = chain.request();
                        
                        // Add authorization header if token is available
                        if (accessToken != null && !accessToken.isEmpty()) {
                            Request request = original.newBuilder()
                                    .header("Authorization", "Bearer " + accessToken)
                                    .method(original.method(), original.body())
                                    .build();
                            return chain.proceed(request);
                        }
                        
                        return chain.proceed(original);
                    }
                })
                .build();
        
        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + clientUrl + "/cirrus/search/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        // Create API service
        apiService = retrofit.create(ApiService.class);
    }
    
    public void loadData() {
        if (apiService == null) {
            Log.e(TAG, "API service is null, cannot fetch asset data");
            if (dataLoadedListener != null) {
                dataLoadedListener.onError("API service initialization failed");
            }
            return;
        }
        
        // Get OAuth token first
        getOAuthToken(new TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                // Token received, now fetch assets
                accessToken = token;
                fetchAssets();
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error getting OAuth token: " + error);
                if (dataLoadedListener != null) {
                    dataLoadedListener.onError("Error getting OAuth token: " + error);
                }
            }
        });
    }
    
    private interface TokenCallback {
        void onTokenReceived(String token);
        void onError(String error);
    }
    
    private void getOAuthToken(final TokenCallback callback) {
        // Check if we have a valid cached token
        long currentTime = System.currentTimeMillis() / 1000;
        if (accessToken != null && tokenExpiresAt > currentTime + TOKEN_LIFETIME_RESERVE_SECONDS) {
            Log.d(TAG, "Using cached token");
            callback.onTokenReceived(accessToken);
            return;
        }
        
        // OAuth configuration
        final String tokenEndpoint = "https://keycloak.blockapps.net/auth/realms/mercata/protocol/openid-connect/token";
        
        // Create OkHttp client for token request
        OkHttpClient client = new OkHttpClient();
        
        // Create request body with client credentials
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();
        
        // Create request
        Request request = new Request.Builder()
                .url(tokenEndpoint)
                .post(formBody)
                .build();
        
        // Execute request asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e(TAG, "Failed to get OAuth token: " + e.getMessage());
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to get OAuth token: " + response.code());
                    callback.onError("HTTP error: " + response.code());
                    return;
                }
                
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String token = jsonObject.getString("access_token");
                    long expiresIn = jsonObject.getLong("expires_in");
                    
                    // Calculate expiration time
                    tokenExpiresAt = System.currentTimeMillis() / 1000 + expiresIn;
                    Log.d(TAG, "New OAuth token expires at: " + tokenExpiresAt);
                    
                    // Return token
                    callback.onTokenReceived(token);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing OAuth token response: " + e.getMessage());
                    callback.onError("Error parsing response: " + e.getMessage());
                }
            }
        });
    }
    
    private void fetchAssets() {
        // Fetch assets
        String ownerCommonName = "eq." + userCommonName;
        apiService.getAssets(ownerCommonName).enqueue(new Callback<List<Asset>>() {
            @Override
            public void onResponse(Call<List<Asset>> call, retrofit2.Response<List<Asset>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    assets = response.body();
                    Log.d(TAG, "Fetched " + assets.size() + " assets");
                    
                    // Now fetch oracle values
                    fetchOracleValues();
                } else {
                    Log.e(TAG, "Error fetching assets: " + response.code());
                    if (dataLoadedListener != null) {
                        dataLoadedListener.onError("Error fetching assets: " + response.code());
                    }
                }
            }
            
            @Override
            public void onFailure(Call<List<Asset>> call, Throwable t) {
                Log.e(TAG, "Asset API call failed: " + t.getMessage());
                if (dataLoadedListener != null) {
                    dataLoadedListener.onError("Asset API call failed: " + t.getMessage());
                }
            }
        });
    }
    
    private void fetchOracleValues() {
        apiService.getOracleValues().enqueue(new Callback<List<Oracle>>() {
            @Override
            public void onResponse(Call<List<Oracle>> call, retrofit2.Response<List<Oracle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Oracle> oracles = response.body();
                    processOracleData(oracles);
                    
                    // Process asset data now that we have oracle values
                    processAssetData();
                    
                    // Notify listener that data is loaded
                    if (dataLoadedListener != null) {
                        dataLoadedListener.onDataLoaded();
                    }
                } else {
                    Log.e(TAG, "Error fetching oracle values: " + response.code());
                    if (dataLoadedListener != null) {
                        dataLoadedListener.onError("Error fetching oracle values: " + response.code());
                    }
                }
            }
            
            @Override
            public void onFailure(Call<List<Oracle>> call, Throwable t) {
                Log.e(TAG, "Oracle API call failed: " + t.getMessage());
                if (dataLoadedListener != null) {
                    dataLoadedListener.onError("Oracle API call failed: " + t.getMessage());
                }
            }
        });
    }
    
    private void processOracleData(List<Oracle> oracles) {
        // Process data to keep only the latest price for each unique asset name
        for (Oracle oracle : oracles) {
            if (oracle.name != null && oracle.consensusPrice != null) {
                // Later entries will overwrite earlier ones
                latestPrices.put(oracle.name, oracle.consensusPrice);
            }
        }
        
        // Store reference to certain prices for mapping
        String ethPrice = latestPrices.get("ETH");
        String btcPrice = latestPrices.get("BTC");
        String goldPrice = latestPrices.get("Gold");
        String silverPrice = latestPrices.get("Silver");
        
        // Apply hard-coded price mappings
        if (ethPrice != null) latestPrices.put("ETHST", ethPrice);
        if (goldPrice != null) latestPrices.put("PAXGST", goldPrice);
        if (silverPrice != null) latestPrices.put("Silver - Fractional 100 oz Bars", silverPrice);
        latestPrices.put("STRAT", "1");
        latestPrices.put("USDCST", "1");
        latestPrices.put("USDST", "1");
        latestPrices.put("USDTST", "1");
        if (btcPrice != null) latestPrices.put("WBTCST", btcPrice);
    }
    
    private void processAssetData() {
        // Reset counters
        fungibleTokensCount = 0;
        fungibleTokensValue = 0;
        nonFungibleTokensCount = 0;
        cataTokensCount = 0;
        totalCataTokens = 0;
        
        // Group assets by name
        Map<String, AssetGroup> assetGroups = new HashMap<>();
        
        for (Asset asset : assets) {
            String name = asset.name != null ? asset.name : (asset.id != null ? asset.id : "Unnamed Asset");
            long quantity = asset.quantity != null ? Long.parseLong(asset.quantity) : 0;
            
            if (!assetGroups.containsKey(name)) {
                // Get the correct decimals value (using hardcoded values for certain assets)
                int decimals = getDecimalsForAsset(name, asset.decimals);
                
                AssetGroup group = new AssetGroup();
                group.name = name;
                group.totalQuantity = 0;
                group.tokenCount = 0;
                group.decimals = decimals;
                
                assetGroups.put(name, group);
            }
            
            AssetGroup group = assetGroups.get(name);
            group.totalQuantity += quantity;
            group.tokenCount += 1;
            group.tokens.add(asset);
        }
        
        // Convert to list and sort alphabetically
        sortedAssets = new ArrayList<>(assetGroups.values());
        Collections.sort(sortedAssets, new Comparator<AssetGroup>() {
            @Override
            public int compare(AssetGroup a1, AssetGroup a2) {
                return a1.name.compareTo(a2.name);
            }
        });
        
        // Process each asset group
        for (AssetGroup asset : sortedAssets) {
            String price = latestPrices.get(asset.name);
            
            if ("CATA".equals(asset.name)) {
                // Count CATA tokens separately
                cataTokensCount += asset.tokenCount;
                
                // Calculate total CATA tokens (quantity)
                try {
                    // Calculate actual quantity as a number with full precision
                    if (asset.decimals != 0) {
                        // Use string operations for high precision
                        String quantityStr = String.valueOf(asset.totalQuantity);
                        int decimals = asset.decimals;
                        
                        if (quantityStr.length() <= decimals) {
                            // Need to add leading zeros
                            int missingZeros = decimals - quantityStr.length();
                            StringBuilder sb = new StringBuilder("0.");
                            for (int i = 0; i < missingZeros; i++) {
                                sb.append('0');
                            }
                            sb.append(quantityStr);
                            asset.calculatedQuantity = Double.parseDouble(sb.toString());
                        } else {
                            // Insert decimal point at the right position from the end
                            int insertPosition = quantityStr.length() - decimals;
                            String result = quantityStr.substring(0, insertPosition) + "." + quantityStr.substring(insertPosition);
                            asset.calculatedQuantity = Double.parseDouble(result);
                        }
                    } else {
                        asset.calculatedQuantity = asset.totalQuantity;
                    }
                    
                    totalCataTokens += asset.calculatedQuantity;
                } catch (Exception e) {
                    Log.e(TAG, "Error calculating CATA quantity: " + e.getMessage());
                    asset.calculatedQuantity = 0;
                }
            } else if (price != null) {
                // This is a fungible token with a price oracle
                fungibleTokensCount += asset.tokenCount;
                
                // Calculate value
                try {
                    // Calculate actual quantity as a number with full precision
                    double actualQuantity;
                    if (asset.decimals != 0) {
                        // Use string operations for high precision
                        String quantityStr = String.valueOf(asset.totalQuantity);
                        int decimals = asset.decimals;
                        
                        if (quantityStr.length() <= decimals) {
                            // Need to add leading zeros
                            int missingZeros = decimals - quantityStr.length();
                            StringBuilder sb = new StringBuilder("0.");
                            for (int i = 0; i < missingZeros; i++) {
                                sb.append('0');
                            }
                            sb.append(quantityStr);
                            actualQuantity = Double.parseDouble(sb.toString());
                        } else {
                            // Insert decimal point at the right position from the end
                            int insertPosition = quantityStr.length() - decimals;
                            String result = quantityStr.substring(0, insertPosition) + "." + quantityStr.substring(insertPosition);
                            actualQuantity = Double.parseDouble(result);
                        }
                    } else {
                        actualQuantity = asset.totalQuantity;
                    }
                    
                    // Parse price with full precision
                    double priceValue = Double.parseDouble(price);
                    
                    // Calculate total value with maximum available precision
                    double totalValue = actualQuantity * priceValue;
                    
                    // Add to the running total
                    fungibleTokensValue += totalValue;
                    
                    // Store calculated quantity and value for display
                    asset.calculatedQuantity = actualQuantity;
                    asset.calculatedValue = totalValue;
                } catch (Exception e) {
                    Log.e(TAG, "Error calculating total value: " + e.getMessage());
                }
            } else {
                // This is a non-fungible token without a price oracle
                nonFungibleTokensCount += asset.tokenCount;
            }
        }
    }
    
    private int getDecimalsForAsset(String assetName, Integer originalDecimals) {
        // Hardcoded assumptions for specific asset types
        Map<String, Integer> knownDecimals = new HashMap<>();
        knownDecimals.put("CATA", 18);
        knownDecimals.put("ETHST", 18);
        knownDecimals.put("STRAT", 4);
        
        // If we have a hardcoded value for this asset, use it
        if (knownDecimals.containsKey(assetName)) {
            return knownDecimals.get(assetName);
        }
        
        // Otherwise return the original value or 0 if null
        return originalDecimals != null ? originalDecimals : 0;
    }
    
    public String calculateActualValue(long quantity, int decimals) {
        // For large decimal values (like 18), we need to handle the calculation carefully
        if (decimals > 15) {
            // Convert to string and manipulate
            String quantityStr = String.valueOf(quantity);
            
            if (quantityStr.length() <= decimals) {
                // Need to add leading zeros
                int missingZeros = decimals - quantityStr.length();
                StringBuilder sb = new StringBuilder("0.");
                for (int i = 0; i < missingZeros; i++) {
                    sb.append('0');
                }
                sb.append(quantityStr);
                return String.valueOf(Double.parseDouble(sb.toString()));
            } else {
                // Insert decimal point at the right position from the end
                int insertPosition = quantityStr.length() - decimals;
                String result = quantityStr.substring(0, insertPosition) + "." + quantityStr.substring(insertPosition);
                return String.valueOf(Double.parseDouble(result));
            }
        } else {
            // For smaller decimal values, direct division works fine
            double value = quantity / Math.pow(10, decimals);
            return String.valueOf(value);
        }
    }
    
    // Getters for the processed data
    public List<AssetGroup> getSortedAssets() {
        return sortedAssets;
    }
    
    public int getFungibleTokensCount() {
        return fungibleTokensCount;
    }
    
    public double getFungibleTokensValue() {
        return fungibleTokensValue;
    }
    
    public int getNonFungibleTokensCount() {
        return nonFungibleTokensCount;
    }
    
    public int getCataTokensCount() {
        return cataTokensCount;
    }
    
    public double getTotalCataTokens() {
        return totalCataTokens;
    }
    
    public String getUserCommonName() {
        return userCommonName;
    }
    
    public Map<String, String> getLatestPrices() {
        return latestPrices;
    }
}
