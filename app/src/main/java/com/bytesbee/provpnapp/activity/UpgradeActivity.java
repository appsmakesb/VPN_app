package com.bytesbee.provpnapp.activity;

import static com.bytesbee.provpnapp.constants.IConstants.BILL_DONE;
import static com.bytesbee.provpnapp.constants.IConstants.MINUS;
import static com.bytesbee.provpnapp.constants.IConstants.P1M;
import static com.bytesbee.provpnapp.constants.IConstants.P1W;
import static com.bytesbee.provpnapp.constants.IConstants.P1Y;
import static com.bytesbee.provpnapp.constants.IConstants.P3M;
import static com.bytesbee.provpnapp.constants.IConstants.P6M;
import static com.bytesbee.provpnapp.constants.IConstants.TRUE;
import static com.bytesbee.provpnapp.constants.IConstants.ZERO;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.ProductDetails;
import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.adapter.SubListAdapter;
import com.bytesbee.provpnapp.managers.BillingManager;
import com.bytesbee.provpnapp.managers.DialogManager;
import com.bytesbee.provpnapp.models.ProductModel;
import com.bytesbee.provpnapp.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UpgradeActivity extends AppCompatActivity implements View.OnClickListener, BillingManager.BillingResultHandler, BillingManager.ProductDetailsListener {
    private TextView btnUpgrade;
    private BillingManager billingManager;
    private RecyclerView recyclerView;
    private List<String> mList = new ArrayList<>();
    private List<ProductDetails> productDetailsList;
    private String strSubItem = "None";
    private SubListAdapter subListAdapter;
    private List<ProductModel> proList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        init();
        initData();
        setupToolbar();
        initBilling();
    }

    private void setupToolbar() {
        try {
            final Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(TRUE);
                getSupportActionBar().setHomeButtonEnabled(TRUE);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public void init() {
        recyclerView = findViewById(R.id.recyclerViewSub);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        btnUpgrade = findViewById(R.id.btnUpgrade);
        proList = new ArrayList<>();
        subListAdapter = new SubListAdapter(UpgradeActivity.this, proList);
        recyclerView.setAdapter(subListAdapter);
    }

    public void initData() {
        addList(P1W, P1M, P3M, P6M, P1Y);
    }

    private void addList(String... list) {
        mList = new ArrayList<>();
        Collections.addAll(mList, list);
    }

    public void initBilling() {
        billingManager = new BillingManager(this);
        billingManager.setCallback(this, this);
        billingManager.startConnection();
    }

    private void initListener() {
        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> btnUpgrade.setOnClickListener(UpgradeActivity.this), 100);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnUpgrade) {
            clickUpgradeButton();
        }
    }

    public void clickUpgradeButton() {
        if (!strSubItem.equalsIgnoreCase("None")) {
            int index = MINUS;
            if (productDetailsList != null) {
                for (int i = 0; i < productDetailsList.size(); i++) {
                    if (productDetailsList.get(i).getProductId().equalsIgnoreCase(strSubItem)) {
                        index = i;
                        break;
                    }
                }
            }
            if (index == MINUS) {
                Utils.showToast(UpgradeActivity.this, getString(R.string.msgSubscriptionPost));
            } else {
                billingManager.launchPurchaseFlow(productDetailsList.get(index));
            }
        } else {
            Utils.showToast(UpgradeActivity.this, getString(R.string.msgChooseSubscription));
        }
    }

    @Override
    public void showResultMsg(String message) {
//        Utils.sout("Message: " + message);
        if (message.equalsIgnoreCase(BILL_DONE)) {
            initListener();
        }
    }

    /**
     * For example,
     * P1W equates to one week,
     * P1M equates to one month,
     * P3M equates to three months,
     * P6M equates to six months, and
     * P1Y equates to one year.
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void productDetailsList(List<ProductDetails> proDetailsList) {
        this.productDetailsList = new ArrayList<>();
        Utils.sout("ProductList:: " + proDetailsList);
        final Handler handler = new Handler(Looper.getMainLooper());
        if (!proDetailsList.isEmpty()) {
            proList.clear();
            int count = 0;
            for (int i = 0; i < mList.size(); i++) {
                for (int j = 0; j < proDetailsList.size(); j++) {
                    ProductDetails pd = proDetailsList.get(j);
                    if (pd.getSubscriptionOfferDetails() != null) {
                        final ProductDetails.PricingPhase pricingPhase = pd.getSubscriptionOfferDetails().get(ZERO).getPricingPhases().getPricingPhaseList().get(ZERO);
                        if (pricingPhase.getBillingPeriod().equalsIgnoreCase(mList.get(i))) {
                            productDetailsList.add(pd);
                            ProductModel pm = new ProductModel();
                            pm.setId(count);
                            pm.setProductId(pd.getProductId());//onemonth
                            pm.setName(pd.getName());//VIP Server for One Month
                            pm.setBillingPeriod(pricingPhase.getBillingPeriod());//P1M
                            pm.setFormattedPrice(pricingPhase.getFormattedPrice());//₹820.00
                            pm.setPriceCurrencyCode(pricingPhase.getPriceCurrencyCode());//INR
                            proList.add(pm);
                            count++;
                            break;
                        }
                    }
                }
            }

            subListAdapter.setOnItemClickListener(obj -> {
                strSubItem = obj.getProductId();
                Utils.sout("strSubItem selected: " + strSubItem);
            });
            handler.postDelayed(subListAdapter::notifyDataSetChanged, 10);
        } else {
            handler.postDelayed(() -> DialogManager.showOK(UpgradeActivity.this, R.string.lblSubscription, R.string.msgSubscriptionPost, this::finish), 10);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    protected void onDestroy() {
        if (billingManager != null) {
            billingManager.closeConnection();
        }
        super.onDestroy();
    }
}