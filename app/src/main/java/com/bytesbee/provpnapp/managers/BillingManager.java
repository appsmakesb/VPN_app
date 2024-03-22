package com.bytesbee.provpnapp.managers;

import static com.bytesbee.provpnapp.constants.IConstants.ONE;
import static com.bytesbee.provpnapp.constants.IConstants.SUCCESS;
import static com.bytesbee.provpnapp.constants.IConstants.ZERO;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.activity.HomeActivity;
import com.bytesbee.provpnapp.constants.IConstants;
import com.bytesbee.provpnapp.models.CallbackSubscription;
import com.bytesbee.provpnapp.models.SubscriptionInfo;
import com.bytesbee.provpnapp.rests.ApiInterface;
import com.bytesbee.provpnapp.rests.RestAdapter;
import com.bytesbee.provpnapp.utils.Utils;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillingManager implements PurchasesUpdatedListener, BillingClientStateListener {

    private final BillingClient billingClient;
    private boolean subStatus = false;
    private final Activity mActivity;

    private final ImmutableList<QueryProductDetailsParams.Product> productList;
    private final List<ProductDetails> mProductDetailsList;

    private BillingResultHandler mCallback;
    private ProductDetailsListener mDetailsCallback;

    //step-1 init
    public BillingManager(Activity activity) {
        mActivity = activity;

        billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        mProductDetailsList = new ArrayList<>();

        productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(activity.getString(R.string.onemonth))
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(activity.getString(R.string.quarter))
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(activity.getString(R.string.halfyear))
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Product 4
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(activity.getString(R.string.oneyear))
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                //Test
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("testvpn")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );
    }

    public void setCallback(BillingResultHandler mCallback, ProductDetailsListener skuDetailsListener) {
        if (this.mCallback == null) {
            this.mCallback = mCallback;
        }
        if (this.mDetailsCallback == null) {
            this.mDetailsCallback = skuDetailsListener;
        }
    }

    public void startConnection() {
        billingClient.startConnection(this);
    }

    public void closeConnection() {
        try {
            if (billingClient != null)
                billingClient.endConnection();
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            mCallback.showResultMsg(IConstants.BILL_DONE);

            //v5
            QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                    .setProductList(productList)
                    .build();
            if (!productList.isEmpty()) {
                billingClient.queryProductDetailsAsync(params, (billingResult1, productDetailsList) -> {
                            if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                mProductDetailsList.clear();
                                mProductDetailsList.addAll(productDetailsList);
                                mDetailsCallback.productDetailsList(mProductDetailsList);
                            }
                        }
                );
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE || billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR || billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
            mCallback.showResultMsg("error");
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        //restart, No connection to billing service
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            Utils.sout("purchaseUpdate: " + list);
            for (Purchase purchase : list) {
                verifySubPurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    private ProductDetails myProductDetails = null;

    //v5
    public String launchPurchaseFlow(ProductDetails productDetails) {
        assert productDetails.getSubscriptionOfferDetails() != null;
        this.myProductDetails = productDetails;
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                        .build()
                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(mActivity, billingFlowParams);

        switch (billingResult.getResponseCode()) {

            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                mCallback.showResultMsg("Billing not supported for type of request");
                return "Billing not supported for type of request";

            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                return "";

            case BillingClient.BillingResponseCode.ERROR:
                mCallback.showResultMsg("Error completing request");
                return "Error completing request";

            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                return "Error processing request.";

            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                return "Selected item is already owned";

            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                return "Item not available";

            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                return "Play Store service is not connected now";

            case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                return "Timeout";

            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                mCallback.showResultMsg("Network error.");
                return "Network Connection down";

            case BillingClient.BillingResponseCode.USER_CANCELED:
                mCallback.showResultMsg("Request Canceled");
                return "Request Canceled";

            case BillingClient.BillingResponseCode.OK:
                return "Subscribed Successfully";
        }
        return "";
    }

    void verifySubPurchase(Purchase purchase) {
        try {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    //user prefs to set premium
                    //Toast.makeText(mActivity, "Subscription activated, Enjoy!", Toast.LENGTH_SHORT).show();
                    Utils.sout("Purchase part: " + purchase);
                    SessionManager.get().setPremiumUser(true);
                    try {
                        if (myProductDetails != null) {
                            Utils.sout("MyProduct: " + myProductDetails);

                            final ProductDetails.PricingPhase pricingPhase = myProductDetails.getSubscriptionOfferDetails().get(ZERO).getPricingPhases().getPricingPhaseList().get(ZERO);

                            SubscriptionInfo subInfo = new SubscriptionInfo();
                            subInfo.setApi_key(Utils.getBase64Encoded(Utils.getAPIKey(mActivity)));
                            subInfo.setOrderId(purchase.getOrderId());
                            subInfo.setPurchaseToken(Utils.getBase64Encoded(purchase.getPurchaseToken()));
                            subInfo.setPurchaseTime(purchase.getPurchaseTime());
                            subInfo.setProductId(myProductDetails.getProductId());
                            subInfo.setPriceCurrencyCode(pricingPhase.getPriceCurrencyCode());
                            subInfo.setFormattedPrice(pricingPhase.getFormattedPrice());
                            subInfo.setPriceAmountMicros(pricingPhase.getPriceAmountMicros());

                            Utils.sout("subInfo json:: " + new Gson().toJson(subInfo));

                            ApiInterface apiInterface = RestAdapter.createAPI(mActivity);

                            Call<CallbackSubscription> callbackCall = apiInterface.insertSubscription(subInfo);
                            callbackCall.enqueue(new Callback<CallbackSubscription>() {
                                @Override
                                public void onResponse(@NonNull Call<CallbackSubscription> call, @NonNull Response<CallbackSubscription> response) {
                                    CallbackSubscription resp = response.body();
                                    if (resp != null && resp.status == SUCCESS) {
                                        Utils.sout("Done: " + resp.message);
                                    } else {
                                        Utils.sout("Failed");
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<CallbackSubscription> call, @NonNull Throwable t) {
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> DialogManager.showOK(mActivity, R.string.titleSub, R.string.msgSubDone, () -> ScreenManager.showClearTopScreen(mActivity, HomeActivity.class)), ONE);
                });
                Utils.sout("Purchase OrderID: " + purchase.getOrderId());
                Utils.sout("getOriginalJson : " + purchase.getOriginalJson());

            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    //v5
    public void checkSubscription() {
        try {
            billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult, list) -> {
                boolean isSub = false;
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (list.size() > 0) {
                        isSub = true;
                    }
                }
                subStatus = isSub;
                SessionManager.get().setPremiumUser(subStatus);
            });
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    //interface
    public interface BillingResultHandler {
        void showResultMsg(String message);
    }

    public interface ProductDetailsListener {
        void productDetailsList(List<ProductDetails> productDetailsList);
    }
}