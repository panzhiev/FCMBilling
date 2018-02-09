package com.panzhyiev.fcmexample.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.panzhyiev.fcmexample.R;
import com.panzhyiev.fcmexample.fcm.RegistrationService;
import com.panzhyiev.fcmexample.ui.activity.purchaseActivity.PurchaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    @BindView(R.id.btn_buy_coins)
    public Button mBtnBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent i = new Intent(this, RegistrationService.class);
        startService(i);

        mBtnBuy.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PurchaseActivity.class)));
    }
}
