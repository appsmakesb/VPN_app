package com.bytesbee.provpnapp.models;

import java.io.Serializable;

public class SubscriptionInfo extends APIKey implements Serializable {
    public String orderId;
    public String productId;
    public String purchaseToken;
    public long purchaseTime;
    public String priceCurrencyCode;//INR
    public String formattedPrice;//₹1
    public long priceAmountMicros;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public void setPriceCurrencyCode(String priceCurrencyCode) {
        this.priceCurrencyCode = priceCurrencyCode;
    }

    public void setFormattedPrice(String formattedPrice) {
        this.formattedPrice = formattedPrice;
    }

    public void setPriceAmountMicros(long priceAmountMicros) {
        this.priceAmountMicros = priceAmountMicros;
    }
}
