package com.bytesbee.provpnapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.fragments.SpeedTestHistoryFragment;
import com.bytesbee.provpnapp.managers.AdsManager;
import com.bytesbee.provpnapp.managers.DialogManager;
import com.bytesbee.provpnapp.managers.HistoryManager;
import com.bytesbee.provpnapp.utils.Utils;

import java.util.Objects;


public class SpeedTestHistoryActivity extends AppCompatActivity {
    private Fragment fragment;
    public Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);
        mActivity = this;

        try {
            Toolbar mToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.lblHistory);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (savedInstanceState != null) {

            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

        } else {

            fragment = new SpeedTestHistoryFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();

        ImageView imgHistory = findViewById(R.id.imgHistory);
        imgHistory.setImageResource(R.drawable.ic_delete_usage);
        imgHistory.setVisibility(View.VISIBLE);
        imgHistory.setOnClickListener(view -> DialogManager.showYesNo(mActivity, R.string.lblDeleteTitle, R.string.msgDelete, () -> {
            HistoryManager.get().clearAll();
            try {
                if (fragment != null) {
                    ((SpeedTestHistoryFragment) fragment).refreshData();
                }
            } catch (Exception e) {
                Utils.getErrors(e);
            }
        }));

        AdsManager.showBannerAd(mActivity);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
