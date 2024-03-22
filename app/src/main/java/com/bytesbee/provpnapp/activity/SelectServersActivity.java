package com.bytesbee.provpnapp.activity;

import static com.bytesbee.provpnapp.constants.IConstants.ONE;
import static com.bytesbee.provpnapp.constants.IConstants.TRUE;
import static com.bytesbee.provpnapp.constants.IConstants.TYPE_FREE;
import static com.bytesbee.provpnapp.constants.IConstants.TYPE_PAID;
import static com.bytesbee.provpnapp.constants.IConstants.ZERO;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.adapter.ServersPageAdapter;
import com.bytesbee.provpnapp.fragments.FreeServersFragment;
import com.bytesbee.provpnapp.managers.AdsManager;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SelectServersActivity extends AppCompatActivity implements View.OnClickListener, FreeServersFragment.ServerClickListener {
    private Activity mActivity;
    private ViewPager2 viewPager;
    private TextView txtFreeServers, txtPremiumServers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_servers);
        mActivity = this;
        init();
        setupToolbar();
        initData();
        initListeners();
        AdsManager.showBannerAd(mActivity);
    }

    public void init() {
        txtPremiumServers = findViewById(R.id.txtPremiumServers);
        txtFreeServers = findViewById(R.id.txtFreeServers);
        viewPager = findViewById(R.id.viewPager);
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

    public void initData() {
        final List<Fragment> fragments = new ArrayList<>();
        final FreeServersFragment freeServersFragment = new FreeServersFragment().newInstance(TYPE_FREE);
        final FreeServersFragment paidServersFragment = new FreeServersFragment().newInstance(TYPE_PAID);
        freeServersFragment.setServerClickListener(this);
        paidServersFragment.setServerClickListener(this);
        fragments.add(freeServersFragment);
        fragments.add(paidServersFragment);
        final FragmentStateAdapter pagerAdapter = new ServersPageAdapter(this, fragments);
        viewPager.setAdapter(pagerAdapter);
        try {
            if (SessionManager.get().getServer() != null && SessionManager.get().getServer().getIsPaid() == TYPE_PAID) {
                setPremiumServersSelected();
                viewPager.setCurrentItem(ONE);
            } else {
                setFreeServersSelected();
                viewPager.setCurrentItem(ZERO);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
        viewPager.setUserInputEnabled(false);
    }

    public void initListeners() {
        txtFreeServers.setOnClickListener(this);
        txtPremiumServers.setOnClickListener(this);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                try {
                    if (position == ZERO) {
                        setFreeServersSelected();
                    } else {
                        setPremiumServersSelected();
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    public void onClick(final View view) {
        final int id = view.getId();
        if (id == R.id.txtPremiumServers) {
            setPremiumServersSelected();
            viewPager.setCurrentItem(ONE);
        } else if (id == R.id.txtFreeServers) {
            setFreeServersSelected();
            viewPager.setCurrentItem(ZERO);
        }
    }

    public void setPremiumServersSelected() {
        try {
            txtPremiumServers.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_border_text));
            txtPremiumServers.setTextColor(getResources().getColor(R.color.colorText));
            txtPremiumServers.setTypeface(Utils.getBoldFont(mActivity), Typeface.BOLD);
            txtFreeServers.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_border));
            txtFreeServers.setTextColor(getResources().getColor(R.color.colorTitleText));
            txtFreeServers.setTypeface(Utils.getRegularFont(mActivity), Typeface.NORMAL);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public void setFreeServersSelected() {
        try {
            txtFreeServers.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_border_text));
            txtFreeServers.setTextColor(getResources().getColor(R.color.colorText));
            txtFreeServers.setTypeface(Utils.getBoldFont(mActivity), Typeface.BOLD);
            txtPremiumServers.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_border));
            txtPremiumServers.setTextColor(getResources().getColor(R.color.colorTitleText));
            txtPremiumServers.setTypeface(Utils.getRegularFont(mActivity), Typeface.NORMAL);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    @Override
    public void onServerClick() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}