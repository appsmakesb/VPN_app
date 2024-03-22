package com.bytesbee.provpnapp.activity;


import static com.bytesbee.provpnapp.constants.IConstants.EXTRA_TOURS;
import static com.bytesbee.provpnapp.constants.IConstants.FALSE;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.managers.ScreenManager;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroPageTransformerType;

import org.jetbrains.annotations.Nullable;


public class OnBoardingActivity extends AppIntro2 {

    private boolean isTakeTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isTakeTour = getIntent().getBooleanExtra(EXTRA_TOURS, FALSE);

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.fragment_slider_1));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.fragment_slider_2));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.fragment_slider_3));

        setVibrate(true);
        setTransformer(new AppIntroPageTransformerType.Parallax(1.0, -1.0, 2.0));
        setNavBarColor(ContextCompat.getColor(this, R.color.navGrayColor));
        setImmersive(true);
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        nextScreen();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        nextScreen();
    }

    private void nextScreen() {
        if (isTakeTour) {
            finish();
        } else {
            SessionManager.get().setOnBoardingDone();
            ScreenManager.showClearTopScreen(getApplicationContext(), HomeActivity.class);
        }
    }
}