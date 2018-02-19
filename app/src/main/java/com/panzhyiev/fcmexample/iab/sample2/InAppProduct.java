package com.panzhyiev.fcmexample.iab.sample2;

public class InAppProduct {

    public String productId;
    public String storeName;
    public String storeDescription;
    public String price;
    public boolean isSubscription;
    public int priceAmountMicros;
    public String currencyIsoCode;

    public String getSku() {
        return productId;
    }

    public String getType() {
        return isSubscription ? "subs" : "inapp";
    }

    public InAppProduct() {
    }

    public InAppProduct(String productId, boolean isSubscription) {
        this.productId = productId;
        this.isSubscription = isSubscription;
    }

    @Override
    public String toString() {
        return "InAppProduct{" +
                "productId='" + productId + '\'' +
                ", storeName='" + storeName + '\'' +
                ", storeDescription='" + storeDescription + '\'' +
                ", price='" + price + '\'' +
                ", isSubscription=" + isSubscription +
                ", priceAmountMicros=" + priceAmountMicros +
                ", currencyIsoCode='" + currencyIsoCode + '\'' +
                '}';
    }
}
