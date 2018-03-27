package com.panzhyiev.fcmexample.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.panzhyiev.fcmexample.R;

import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;

public class SpendEthereumActivity extends AppCompatActivity {

    private static final String TAG = SpendEthereumActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend_ethereum);

        String privateKeyString = "4188e2568ef9be3cfff692129946bbea4ac0ca6c650f4de15e3fdf7ccd3bbba1";

        BigInteger bigInteger = new BigInteger(privateKeyString, 16);
        ECKey ecKey = ECKey.fromPrivate(bigInteger);

        Transaction ethTx = new Transaction(
                ByteUtil.bigIntegerToBytes(BigInteger.valueOf(0)),
                ByteUtil.bigIntegerToBytes(BigInteger.valueOf(41000000000L)),
                ByteUtil.bigIntegerToBytes(BigInteger.valueOf(21340)),
                ecKey.getAddress(),
                ByteUtil.bigIntegerToBytes(BigInteger.valueOf(50000000000000000L)),
                "hello".getBytes());

        ethTx.sign(ecKey);

        Log.d(TAG, "onCreate: " + Hex.toHexString(ByteUtil.merge(ethTx.getEncoded(), ethTx.getSignature().toByteArray())));
        Log.d(TAG, "onCreate: " + Hex.toHexString(ethTx.getReceiveAddress()));
        Log.d(TAG, "onCreate: " + ethTx.toString());
    }
}
