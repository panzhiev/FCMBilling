package com.panzhyiev.fcmexample.ui.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.panzhyiev.fcmexample.R;
import com.panzhyiev.fcmexample.crypto.MnemonicCodeCustom;
import com.panzhyiev.fcmexample.crypto.ethereum.Numeric;
import com.panzhyiev.fcmexample.db.SharedPreferencesHelper;

import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicException;
import org.ethereum.crypto.ECKey;
import org.spongycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.panzhyiev.fcmexample.utils.Constants.SEED;
import static org.bitcoinj.core.Utils.HEX;

public class MnemonicActivity extends AppCompatActivity {

    public static final String TAG = MnemonicActivity.class.getSimpleName();
    private byte[] seed;
    private String strSeed;
    private DeterministicKey child0; // key path m/0
    private DeterministicKey child00; // key path m/0/0

    ECKey ecKey;

    @BindView(R.id.btn_generate_seed)
    public Button mBtnSeed;
    @BindView(R.id.btn_generate_private)
    public Button mBtnPrivate;
    @BindView(R.id.btn_generate_public)
    public Button mBtnPublic;
    @BindView(R.id.btn_generate_address)
    public Button mBtnAddress;
    @BindView(R.id.tv_list_words)
    public TextView mTvList;
    @BindView(R.id.tv_seed)
    public TextView mTvSeed;
    @BindView(R.id.tv_priv)
    public TextView mTvPriv;
    @BindView(R.id.tv_pub)
    public TextView mTvPub;
    @BindView(R.id.tv_address)
    public TextView mTvAddress;

    private DeterministicKey mDeterministicKey;
    private Handler mHandler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnemonic);
        ButterKnife.bind(this);
        setListeners();
        mTvList.setText(generateMnemonic().toString());

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                mTvSeed.setText(strSeed);
            }
        };
    }

    private void setListeners() {

        mBtnSeed.setOnClickListener(view -> {
            new Thread(() -> {
                getAndStoreSeed();
                mHandler.sendEmptyMessage(0);
            }).start();
        });

        mBtnPrivate.setOnClickListener(view -> {
            mDeterministicKey = HDKeyDerivation.createMasterPrivateKey(seed);
            child0 = HDKeyDerivation.deriveChildKey(mDeterministicKey, 0);
            child00 = HDKeyDerivation.deriveChildKey(child0, 0);

            String senderPrivKey = child00.getPrivateKeyAsHex();

            BigInteger bigInteger = new BigInteger(senderPrivKey, 16);
//            BigInteger bigInteger = new BigInteger(senderPrivKey, 16);

            ecKey = ECKey.fromPrivate(bigInteger);

//            mTvPriv.setText(child00.getPrivateKeyAsHex());
            mTvPriv.setText(senderPrivKey);

            Log.d(TAG, "PATH " + child00.getPathAsString());
            Log.d(TAG, "PRIVATE HEX " + senderPrivKey);
        });
        mBtnPublic.setOnClickListener(view -> {
            mTvPub.setText(Hex.toHexString(ecKey.getPubKey()));
            Log.d(TAG, "PUBLIC HEX " + Hex.toHexString(ecKey.getPubKey()));
        });
        mBtnAddress.setOnClickListener(view -> {

            String addresUnchecksumed = Hex.toHexString(ecKey.getAddress());

            String addressChecksumed = Numeric.toChecksumAddress("0x" + addresUnchecksumed);

            mTvAddress.setText("0x" + addressChecksumed);
            Log.d(TAG, "ADDRESS " + "0x" + addressChecksumed);
        });
    }

    private ArrayList<String> generateMnemonic() {
        ArrayList<String> mWordList = new ArrayList<>();
//        for (int i = 0; i < 12; i++) {
//            mWordList.add(i, "a");
//        }

        mWordList.add("dress");
        mWordList.add("stable");
        mWordList.add("electric");
        mWordList.add("tissue");
        mWordList.add("stand");
        mWordList.add("level");
        mWordList.add("female");
        mWordList.add("orange");
        mWordList.add("surge");
        mWordList.add("lizard");
        mWordList.add("radio");
        mWordList.add("flame");

//        byte bytes[] = new byte[16];
//        new SecureRandom().nextBytes(bytes);
//
//        try {
//            mWordList = (ArrayList<String>) MnemonicCodeCustom.INSTANCE.toMnemonic(bytes);
//        } catch (MnemonicException.MnemonicLengthException e) {
//            e.printStackTrace();
//        }

        return mWordList;
    }

    public void getAndStoreSeed() {
        Log.d(TAG, "getAndStoreSeed()");
        seed = MnemonicCodeCustom.toSeed(generateMnemonic(), "");
        strSeed = HEX.encode(seed);
//        SharedPreferencesHelper.getInstance().putStringValue(SEED, strSeed);
    }
}
