package com.panzhyiev.fcmexample.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.matthewmitchell.emercoinj.core.Transaction;
import com.matthewmitchell.emercoinj.params.EmercoinNetParams;
import com.panzhyiev.fcmexample.R;
import com.panzhyiev.fcmexample.fcm.RegistrationService;
import com.panzhyiev.fcmexample.ui.activity.purchaseActivity.PurchaseActivity;
import com.panzhyiev.fcmexample.ui.activity.purchaseActivity.PurchaseActivity2;
import com.panzhyiev.fcmexample.utils.DialogFactory;
import com.panzhyiev.fcmexample.utils.SnackbarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String NOTIFICATION = "notification";
    private final String TAG = getClass().getSimpleName();

    private BroadcastReceiver mBroadcastReceiver;
    public final static String BROADCAST_ACTION = "PUBLIC_MESSAGE";

    @BindView(R.id.btn_buy_coins)
    public Button mBtnBuy;

    @BindView(R.id.btn_generate_eth_addresses)
    public Button mBtnEth;

    @BindView(R.id.ll_main)
    public LinearLayout mLlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent i = new Intent(this, RegistrationService.class);
        startService(i);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String[] strings = intent.getStringArrayExtra(NOTIFICATION);
//                DialogFactory.createSimpleOkErrorDialog(MainActivity.this, strings[0], strings[1]).show();

                SnackbarUtils.with(mLlMain)
                        .setBgColor(Color.WHITE)
                        .setMessage(strings[1])
                        .setDuration(SnackbarUtils.LENGTH_LONG)
                        .setAction("SHOW", view -> {
                            Intent intent1 = new Intent("OPEN_ACTIVITY_RESULT");
                            startActivity(intent1);
                        })
                        .setMessageColor(getResources().getColor(R.color.black))
                        .show();
            }
        };

        // создаем фильтр для BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(mBroadcastReceiver, intentFilter);

        Transaction transaction = new Transaction(EmercoinNetParams.get());

        mBtnBuy.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PurchaseActivity2.class)));

        mBtnEth.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MnemonicActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // дерегистрируем (выключаем) BroadcastReceiver
        unregisterReceiver(mBroadcastReceiver);
    }
}