package com.panzhyiev.fcmexample.ui.activity.purchaseActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.panzhyiev.fcmexample.R;
import com.panzhyiev.fcmexample.db.room.entity.CoinBuyPojo;
import com.panzhyiev.fcmexample.iab.sample2.InAppProduct;
import com.panzhyiev.fcmexample.iab.util.IabBroadcastReceiver;
import com.panzhyiev.fcmexample.iab.util.IabHelper;
import com.panzhyiev.fcmexample.iab.util.IabResult;
import com.panzhyiev.fcmexample.iab.util.Inventory;
import com.panzhyiev.fcmexample.iab.util.Purchase;
import com.panzhyiev.fcmexample.ui.ILoadingView;
import com.panzhyiev.fcmexample.ui.activity.MainActivity;
import com.panzhyiev.fcmexample.ui.activity.purchaseActivity.adapter.InAppProductAdapter;
import com.panzhyiev.fcmexample.ui.activity.purchaseActivity.adapter.PurchaseAdapter;
import com.panzhyiev.fcmexample.utils.GridSpacingItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.panzhyiev.fcmexample.iab.util.IabHelper.REQUEST_CODE_BUY;

public class PurchaseActivity extends AppCompatActivity implements ILoadingView, IabBroadcastReceiver.IabBroadcastListener, InAppProductAdapter.OnBuyClickListener,
        DialogInterface.OnClickListener {

    // Debug tag, for logging
    private final String TAG = getClass().getSimpleName();

    // SKUs for our products: coins (consumable)
    static final String SKU_BTC = "btc";
    static final String SKU_ETH = "eth";
    static final String SKU_XRP = "xrp";
    static final String SKU_BCH = "bch";
    static final String SKU_ADA = "ada";
    static final String SKU_LTC = "ltc";
    static final String SKU_XLM = "xlm";
    static final String SKU_NEO = "neo";
    static final String SKU_EOS = "eos";
    static final String SKU_XEM = "xem";
    static final String SKU_MIOTA = "miota";
    static final String SKU_DASH = "dash";

    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final int BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE = 2;
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;

    public static final int PURCHASE_STATUS_PURCHASED = 0;
    public static final int PURCHASE_STATUS_CANCELLED = 1;
    public static final int PURCHASE_STATUS_REFUNDED = 2;

    //item types
    public static final String ITEM_TYPE_INAPP = "inapp";
    public static final String ITEM_TYPE_SUBS = "subs";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    @BindView(R.id.rv_coins_for_sale)
    public RecyclerView mRvCoinsForSale;

    @BindView(R.id.swipe_refresh_purchases)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private PurchaseAdapter mPurchaseAdapter;
    private InAppProductAdapter mInAppProductAdapter;

    IInAppBillingService inAppBillingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar_coins_sale);
        setSupportActionBar(toolbar);
        setUpRecyclerView();

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

//        ArrayList<CoinBuyPojo> list = new ArrayList<>();
//
//        list.add(new CoinBuyPojo("bitcoin", "Bitcoin", "BTC", "0.00001"));
//        list.add(new CoinBuyPojo("ethereum", "Ethereum", "ETH", "0.0001"));
//        list.add(new CoinBuyPojo("ripple", "Ripple", "XRP", "0.1"));
//        list.add(new CoinBuyPojo("bitcoin-cash", "Bitcoin Cash", "BCH", "0.0005"));
//        list.add(new CoinBuyPojo("cardano", "Cardano", "ADA", "0.1"));
//        list.add(new CoinBuyPojo("litecoin", "Litecoin", "LTC", "0.001"));
//        list.add(new CoinBuyPojo("stellar", "Stellar", "XLM", "0.1"));
//        list.add(new CoinBuyPojo("neo", "NEO", "NEO", "0.001"));
//        list.add(new CoinBuyPojo("eos", "EOS", "EOS", "0.01"));
//        list.add(new CoinBuyPojo("nem", "NEM", "XEM", "0.1"));
//        list.add(new CoinBuyPojo("iota", "IOTA", "MIOTA", "0.01"));
//        list.add(new CoinBuyPojo("dash", "Dash", "DASH", "0.001"));
//
//        setList(list);

        // preparing billing
        String base64EncodedPublicKey = getString(R.string.key);

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(PurchaseActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                List<InAppProduct> inAppProductList;
                try {
                    inAppProductList = mHelper.getInAppPurchases(ITEM_TYPE_INAPP, SKU_BTC, SKU_ETH);
                } catch (Exception e) {
                    e.printStackTrace();
                    inAppProductList = new ArrayList<>();
                }

                Log.d(TAG, inAppProductList.toString());

                setUpRecyclerView();
                setList((ArrayList) inAppProductList);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
//                Log.d(TAG, "Setup successful. Querying inventory.");
//                try {
//                    mHelper.queryInventoryAsync(mGotInventoryListener);
//                } catch (IabHelper.IabAsyncInProgressException e) {
//                    complain("Error querying inventory. Another async operation in progress.");
//                }
            }
        });

        setListeners();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BUY) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", -1);
            if (responseCode == BILLING_RESPONSE_RESULT_OK) {
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                // можете проверить цифровую подпись
                readPurchase(purchaseData);
            } else {
                alert("responseCode = " + responseCode);
                // обрабатываем ответ
            }
        }
    }

    private void readPurchase(String purchaseData) {
        try {
            JSONObject jsonObject = new JSONObject(purchaseData);
            // ид покупки, для тестовой покупки будет null
            String orderId = jsonObject.optString("orderId");
            // "com.example.myapp"
            String packageName = jsonObject.getString("packageName");
            // "com.example.myapp_testing_inapp1"
            String productId = jsonObject.getString("productId");
            // unix-timestamp времени покупки
            long purchaseTime = jsonObject.getLong("purchaseTime");

            // PURCHASE_STATUS_PURCHASED
            // PURCHASE_STATUS_CANCELLED
            // PURCHASE_STATUS_REFUNDED
            int purchaseState = jsonObject.getInt("purchaseState");

            if (purchaseState == PURCHASE_STATUS_PURCHASED) {
                alert("You just buy " + productId + " successfully!");
            } else {
                alert("Canceled");
            }

            // "12345"
            String developerPayload = jsonObject.optString("developerPayload");
            // токен покупки, с его помощью можно получить
            // данные о покупке на сервере
            String purchaseToken = jsonObject.getString("purchaseToken");
            // далее вы обрабатываете покупку

            Log.d(TAG,
                    "orderId = " + orderId + "\n" +
                            "packageName = " + packageName + "\n" +
                            "productId = " + productId + "\n" +
                            "purchaseToken = " + purchaseToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Check for BTC delivery
            Purchase btcPurchase = inventory.getPurchase(SKU_BTC);
            if (btcPurchase != null && verifyDeveloperPayload(btcPurchase)) {
                Log.d(TAG, "We have BTC. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_BTC), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming BTC. Another async operation in progress.");
                }
                return;
            }

            // Check for ETH delivery
            Purchase ethPurchase = inventory.getPurchase(SKU_ETH);
            if (ethPurchase != null && verifyDeveloperPayload(ethPurchase)) {
                Log.d(TAG, "We have ETH. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_ETH), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming ETH. Another async operation in progress.");
                }
                return;
            }

//            updateUi();
            hideProgressIndicator();
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");

                alert("You just buy criptocurrency");
            } else {
                complain("Error while consuming: " + result);
            }
//            updateUi();
            hideProgressIndicator();
            Log.d(TAG, "End consumption flow.");
        }
    };

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }


    private void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> mSwipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setUpRecyclerView() {
        mRvCoinsForSale.setLayoutManager(new GridLayoutManager(this, 2));
        mRvCoinsForSale.setHasFixedSize(true);
        mRvCoinsForSale.addItemDecoration(new GridSpacingItemDecoration(2, GridSpacingItemDecoration.dpToPx(this, 10), true));
        mRvCoinsForSale.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onBuyClicked(InAppProduct inAppProduct) {
        Toast.makeText(this, "buy clicked", Toast.LENGTH_SHORT).show();
        try {
            mHelper.purchaseProduct(this, inAppProduct);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showProgressIndicator() {

    }

    @Override
    public void hideProgressIndicator() {

    }

    @Override
    public void setList(ArrayList list) {
//        Log.d(TAG, "setList started");
//        if (mPurchaseAdapter == null) {
//            Log.d(TAG, "mCoinsAdapter == null");
//            mPurchaseAdapter = new PurchaseAdapter(list, this);
//            mRvCoinsForSale.setAdapter(mPurchaseAdapter);
//        } else {
//            Log.d(TAG, "mCoinsAdapter != null");
//            mPurchaseAdapter.reloadList(list);
//        }

        Log.d(TAG, "setList started");
        if (mInAppProductAdapter == null) {
            Log.d(TAG, "mCoinsAdapter == null");
            mInAppProductAdapter = new InAppProductAdapter(list, this);
            mRvCoinsForSale.setAdapter(mInAppProductAdapter);
        } else {
            Log.d(TAG, "mCoinsAdapter != null");
            mInAppProductAdapter.reloadList(list);
        }
    }

    @Override
    public void reloadList(ArrayList list) {

    }

    @Override
    public void showError() {

    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }
}
