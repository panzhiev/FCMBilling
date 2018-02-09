package com.panzhyiev.fcmexample.ui.activity.purchaseActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.panzhyiev.fcmexample.R;
import com.panzhyiev.fcmexample.db.room.entity.CoinBuyPojo;
import com.panzhyiev.fcmexample.ui.ILoadingView;
import com.panzhyiev.fcmexample.ui.activity.purchaseActivity.adapter.PurchaseAdapter;
import com.panzhyiev.fcmexample.utils.GridSpacingItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PurchaseActivity extends AppCompatActivity implements PurchaseAdapter.OnBuyClickListener, ILoadingView {

    private final String TAG = getClass().getSimpleName();

    @BindView(R.id.rv_coins_for_sale)
    public RecyclerView mRvCoinsForSale;

    private PurchaseAdapter mPurchaseAdapter;

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

        ArrayList<CoinBuyPojo> list = new ArrayList<>();

        list.add(new CoinBuyPojo("bitcoin", "Bitcoin", "BTC", "0.00001"));
        list.add(new CoinBuyPojo("ethereum", "Ethereum", "ETH", "0.0001"));
        list.add(new CoinBuyPojo("ripple", "Ripple", "XRP", "0.1"));
        list.add(new CoinBuyPojo("bitcoin-cash", "Bitcoin Cash", "BCH", "0.0005"));
        list.add(new CoinBuyPojo("cardano", "Cardano", "ADA", "0.1"));
        list.add(new CoinBuyPojo("litecoin", "Litecoin", "LTC", "0.001"));
        list.add(new CoinBuyPojo("stellar", "Stellar", "XLM", "0.1"));
        list.add(new CoinBuyPojo("neo", "NEO", "NEO", "0.001"));
        list.add(new CoinBuyPojo("eos", "EOS", "EOS", "0.01"));
        list.add(new CoinBuyPojo("nem", "NEM", "XEM", "0.1"));
        list.add(new CoinBuyPojo("iota", "IOTA", "MIOTA", "0.01"));
        list.add(new CoinBuyPojo("dash", "Dash", "DASH", "0.001"));

        setList(list);

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
    public void onBuyClicked() {
        Toast.makeText(this, "buy clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressIndicator() {

    }

    @Override
    public void hideProgressIndicator() {

    }

    @Override
    public void setList(ArrayList list) {
        Log.d(TAG, "setList started");
        if (mPurchaseAdapter == null) {
            Log.d(TAG, "mCoinsAdapter == null");
            mPurchaseAdapter = new PurchaseAdapter(list, this);
            mRvCoinsForSale.setAdapter(mPurchaseAdapter);
        } else {
            Log.d(TAG, "mCoinsAdapter != null");
            mPurchaseAdapter.reloadList(list);
        }
    }

    @Override
    public void reloadList(ArrayList list) {

    }

    @Override
    public void showError() {

    }
}
