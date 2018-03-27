package com.panzhyiev.fcmexample;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import com.panzhyiev.fcmexample.db.SharedPreferencesHelper;

import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.PlayStoreListener;

import javax.annotation.Nonnull;


public class App extends Application {

    private static Application INSTANCE;

    @Nonnull
    private final Billing mBilling = new Billing(this, new Billing.DefaultConfiguration() {
        @Nonnull
        @Override
        public String getPublicKey() {
            // encrypted public key of the app. Plain version can be found in Google Play's Developer
            // Console in Service & APIs section under "YOUR LICENSE KEY FOR THIS APPLICATION" title.
            // A naive encryption algorithm is used to "protect" the key. See more about key protection
            // here: https://developer.android.com/google/play/billing/billing_best_practices.html#key
            //            return Encryption.decrypt(s, "panzhiev.timur@gmail.com");
            return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm9sqzl2ZCezjBgxi28XGH" +
                    "uEmyYdlzkFxUK1kI2mPq+oQLnpOQCzaONsS2/eZiw0LbW7n53zEmBk9eZH54JYP0kzZ4Tlw0Me+eKoMvzXW" +
                    "NhAb5HKOZfJPDKnR2Tu9ZUsOFjlR02rxux/3mj+ii7S7PmVdhxhkoBX8lez/yAOw6o/IHV703zlBWV8BBvvH" +
                    "QwWh1qb67iHHpNbaWFksetPhBC0AkARdWoVLGcjyffGBE5TYEk6G23YJfi7myxyLb9CtAiXi2pNfPHd2IDU6H4" +
                    "0bG79v61m+HlzMM59+oJceWGaFrt1Fic1pk3xMV7D1gKxPScSLM4ldEcJJDPO6331yrQIDAQAB";
        }
    });

    /**
     * Returns an instance of {@link App} attached to the passed activity.
     */
    public static App get(Activity activity) {
        return (App) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesHelper.getInstance().initialize(this);
        INSTANCE = this;
        mBilling.addPlayStoreListener(() ->
                Toast
                        .makeText(App.this, R.string.purchases_changed, Toast.LENGTH_LONG)
                        .show());
    }

    @Nonnull
    public Billing getBilling() {
        return mBilling;
    }

    public static Application getInstance() {
        return INSTANCE;
    }
}
