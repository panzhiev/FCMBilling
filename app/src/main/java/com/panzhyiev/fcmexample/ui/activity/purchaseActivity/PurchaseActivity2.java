package com.panzhyiev.fcmexample.ui.activity.purchaseActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.panzhyiev.fcmexample.App;
import com.panzhyiev.fcmexample.R;
import com.panzhyiev.fcmexample.iab.sample2.InAppProduct;

import org.json.JSONObject;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Logger;
import org.solovyev.android.checkout.Purchase;

import com.panzhyiev.fcmexample.ui.ILoadingView;
import com.panzhyiev.fcmexample.ui.activity.purchaseActivity.adapter.InAppProductAdapter;
import com.panzhyiev.fcmexample.utils.GridSpacingItemDecoration;
import com.panzhyiev.fcmexample.utils.Utils;

import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.checkout.Sku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PurchaseActivity2 extends AppCompatActivity implements ILoadingView, InAppProductAdapter.OnBuyClickListener,
        DialogInterface.OnClickListener {

    // Debug tag, for logging
    private static final String TAG = "PurchaseActivity2";

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

    @BindView(R.id.rv_coins_for_sale)
    public RecyclerView mRvCoinsForSale;

    @BindView(R.id.swipe_refresh_purchases)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private ActivityCheckout mCheckout;
    private InventoryCallback mInventoryCallback;

    private List<String> getInAppSkus() {
        final List<String> skus = new ArrayList<>();
        skus.addAll(Arrays.asList(getResources().getStringArray(R.array.array_skus)));
//        for (int i = 0; i < 20; i++) {
//            final int id = i + 1;
//            final String sku = id < 10 ? "item_0" + id : "item_" + id;
//            skus.add(sku);
//        }
        return skus;
    }

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

        final Adapter adapter = new Adapter();
        mInventoryCallback = new InventoryCallback(adapter);
        mRvCoinsForSale.setAdapter(adapter);

        final Billing billing = App.get(this).getBilling();
        Billing.setLogger(new Logger() {
            @Override
            public void v(@Nonnull String tag, @Nonnull String msg) {

            }

            @Override
            public void v(@Nonnull String tag, @Nonnull String msg, @Nonnull Throwable e) {

            }

            @Override
            public void d(@Nonnull String tag, @Nonnull String msg) {
                Log.d(TAG, msg);
            }

            @Override
            public void d(@Nonnull String tag, @Nonnull String msg, @Nonnull Throwable e) {
                Log.d(TAG, msg);
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void i(@Nonnull String tag, @Nonnull String msg) {

            }

            @Override
            public void i(@Nonnull String tag, @Nonnull String msg, @Nonnull Throwable e) {

            }

            @Override
            public void w(@Nonnull String tag, @Nonnull String msg) {

            }

            @Override
            public void w(@Nonnull String tag, @Nonnull String msg, @Nonnull Throwable e) {

            }

            @Override
            public void e(@Nonnull String tag, @Nonnull String msg) {

            }

            @Override
            public void e(@Nonnull String tag, @Nonnull String msg, @Nonnull Throwable e) {

            }
        });
        mCheckout = Checkout.forActivity(this, billing);
        mCheckout.start();
        reloadInventory();

        setListeners();
    }

    private void reloadInventory() {
        final Inventory.Request request = Inventory.Request.create();
        // load purchase info
        request.loadAllPurchases();
        // load SKU details
        request.loadSkus(ProductTypes.IN_APP, getInAppSkus());
        mCheckout.loadInventory(request, mInventoryCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCheckout.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
//            reloadInventory();
        });
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
    protected void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
    }

    private void purchase(Sku sku) {
        final RequestListener<Purchase> listener = makeRequestListener();
        mCheckout.startPurchaseFlow(sku, null, listener);
    }

    /**
     * @return {@link RequestListener} that reloads inventory when the action is finished
     */
    private <T> RequestListener<T> makeRequestListener() {
        return new RequestListener<T>() {
            @Override
            public void onSuccess(@Nonnull T result) {

                Log.d(TAG, "RequestListener + onSuccess");

                if (result instanceof Purchase) {

                    Purchase purchase = (Purchase) result;
                    String s = purchase.toJson();
                    Log.d(TAG, "JSON : " + s);

                    String token = purchase.token;
                    String packageName = purchase.packageName;
                    String orderId = purchase.orderId;

                    Log.d("PURCHASE_INFO", "token = " + token + "\n" +
                            "packageName = " + packageName + "\n" +
                            "orderId = " + orderId + "\n");

                    // here send request to the server with purchase token
                    // mPresenter.sendConfirmationPurchaseToken(token, packageName, orderId);

                }

                reloadInventory();

//                for (String sku : getInAppSkus()) {
//                    Inventory.Product mProduct = Inventory.Products.empty().get(ProductTypes.IN_APP);
//                    final Purchase purchase = mProduct.getPurchaseInState(sku, Purchase.State.PURCHASED);
//                    if (purchase != null) {
//                        Log.d(TAG, "Purchase != null");
//                        consume(purchase);
//                    } else {
//                        Log.d(TAG, "Purchase == null");
//                    }
//                }
            }

            @Override
            public void onError(int response, @Nonnull Exception e) {
                Log.d(TAG, "RequestListener + onError");

                reloadInventory();
            }
        };
    }

    private void consume(final Purchase purchase) {
        mCheckout.whenReady(new Checkout.EmptyListener() {
            @Override
            public void onReady(@Nonnull BillingRequests requests) {
                requests.consume(purchase.token, makeRequestListener());
            }
        });
    }


    /**
     * Updates {@link Adapter} when {@link Inventory.Products} are loaded.
     */
    private class InventoryCallback implements Inventory.Callback {
        private final Adapter mAdapter;

        public InventoryCallback(Adapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onLoaded(@Nonnull Inventory.Products products) {
            final Inventory.Product product = products.get(ProductTypes.IN_APP);
            if (!product.supported) {
                // billing is not supported, user can't purchase anything
                return;
            }

            for (String sku : getInAppSkus()) {
                final Purchase purchase = product.getPurchaseInState(sku, Purchase.State.PURCHASED);
                if (purchase != null) {
                    Log.d(TAG, "Purchase != null");
                    consume(purchase);
                } else {
                    Log.d(TAG, "Purchase == null");
                }
            }

            mAdapter.update(product);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private final LayoutInflater mInflater = LayoutInflater.from(PurchaseActivity2.this);
        private Inventory.Product mProduct = Inventory.Products.empty().get(ProductTypes.IN_APP);

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = mInflater.inflate(R.layout.item_coin_buy, parent, false);
            return new ViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Sku sku = mProduct.getSkus().get(position);
            holder.onBind(sku, mProduct.isPurchased(sku));
        }

        @Override
        public int getItemCount() {
            return mProduct.getSkus().size();
        }

        public void update(Inventory.Product product) {
            mProduct = product;
            notifyDataSetChanged();
        }

        public void onClick(Sku sku) {
            final Purchase purchase = mProduct.getPurchaseInState(sku, Purchase.State.PURCHASED);
            if (purchase != null) {
                consume(purchase);
            } else {
                purchase(sku);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Adapter mAdapter;
        @BindView(R.id.tv_symbol_currency)
        TextView mTitle;
        @BindView(R.id.tv_name_currency)
        TextView mDescription;
        @BindView(R.id.tv_amount_for_sale)
        TextView mPrice;
        @BindView(R.id.iv_crypto_icon)
        ImageView mIcon;
        @BindView(R.id.btn_buy_coins_item)
        Button mBtnBuyCoins;

        @Nullable
        private Sku mSku;

        Context mContext;

        ViewHolder(View view, Adapter adapter) {
            super(view);
            mAdapter = adapter;
            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
            mBtnBuyCoins.setOnClickListener(this);
            mContext = view.getContext();
        }

        private static void strikeThrough(TextView view, boolean strikeThrough) {
            int flags = view.getPaintFlags();
            if (strikeThrough) {
                flags |= Paint.STRIKE_THRU_TEXT_FLAG;
            } else {
                flags &= ~Paint.STRIKE_THRU_TEXT_FLAG;
            }
            view.setPaintFlags(flags);
        }

        void onBind(Sku sku, boolean purchased) {
            mSku = sku;
            mTitle.setText(sku.id.code.toUpperCase());
            mDescription.setText(sku.description);
            mBtnBuyCoins.setEnabled(!purchased);
//            strikeThrough(mTitle, purchased);
//            strikeThrough(mDescription, purchased);
            mPrice.setText(sku.price);

            Bitmap bitmap = Utils.getBitmapFromCryptoIconsAssets(mContext, sku.id.code.toLowerCase());
            if (bitmap != null) {
                mIcon.setImageBitmap(bitmap);
            } else
                mIcon.setImageDrawable(new ColorDrawable(sku.title.hashCode() + sku.description.hashCode()));
        }

        /**
         * @return SKU title without application name that is automatically added by Play Services
         */
        private String getTitle(Sku sku) {
            final int i = sku.title.indexOf("(");
            if (i > 0) {
                return sku.title.substring(0, i);
            }
            return sku.title;
        }

        @Override
        public void onClick(View v) {
            if (mSku == null) {
                return;
            }

            switch (v.getId()) {
                case R.id.btn_buy_coins_item:
                    mAdapter.onClick(mSku);
                    break;
            }
        }
    }
}
