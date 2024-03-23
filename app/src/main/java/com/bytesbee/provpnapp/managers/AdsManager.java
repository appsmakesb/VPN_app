package com.bytesbee.provpnapp.managers;

import static com.bytesbee.provpnapp.constants.IConstants.HANDLER_SUCCESS;
import static com.bytesbee.provpnapp.constants.IConstants.MINUS;
import static com.bytesbee.provpnapp.constants.IConstants.TEST_ADS;
import static com.bytesbee.provpnapp.constants.IConstants.ZERO;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.bytesbee.provpnapp.BuildConfig;
import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.models.Setting;
import com.bytesbee.provpnapp.utils.EasyAdsDynamic;
import com.bytesbee.provpnapp.utils.Utils;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdsManager {
    //TESTING ADS
    private static final String BANNER_AD = BuildConfig.BANNER_AD;//"ca-app-pub-3940256099942544/6300978111";
    private static final String INTER_AD = BuildConfig.INIT_AD;//"ca-app-pub-3940256099942544/1033173712";
    private static final String NATIVE_AD = BuildConfig.NATIVE_AD;//"ca-app-pub-3940256099942544/2247696110";

    private static InterstitialAd mInterstitialAd;
    private static int LAUNCHED_AD = ZERO;

    public static void initializeMobileAds(final Context context) {
        MobileAds.initialize(context, initializationStatus -> {
        });
    }

    //================ BANNER START ==================
    public static void showBannerAd(final Activity mActivity) {
        try {
            final LinearLayout layout = mActivity.findViewById(R.id.adsBottomView);
            layout.setVisibility(View.VISIBLE);
            final Setting setting = SessionManager.get().getSettingModel();
            final boolean isPremium = SessionManager.get().isPremiumUser();
            if (setting != null && !isPremium) {
                if (setting.getBanner_ad() == HANDLER_SUCCESS) {
                    //For testing purpose only, if you need to look the TEST ADS, just uncomment below code and when you get the ads, then don't forget to comment the below code
                    if (TEST_ADS) {
                        setting.setBanner_ad_id(BANNER_AD);
                    }
                    final AdView mAdView = new AdView(mActivity);
                    layout.removeAllViews();
                    EasyAdsDynamic.forBanner(mActivity)
                            .withLayout(layout, mAdView)
                            .listener(new AdListener() {
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    super.onAdFailedToLoad(loadAdError);
                                    Utils.sout("onAdFailedToLoad:: " + loadAdError.getMessage());
                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();
                                }
                            })
                            .adUnitId(setting.getBanner_ad_id())
                            .adSize(getAdSize(mActivity))
                            .show();
                } else {
                    layout.setVisibility(View.GONE);
                }
            } else {
                layout.setVisibility(View.GONE);
            }
        } catch (Exception ignored) {
        }
    }

    private static AdSize getAdSize(final Activity mActivity) {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = mActivity.getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth);
    }
    //================ BANNER END ==================

    //================ INTERSTITIAL START ================
    public static void firstLoadAds(final Activity mActivity) {
        final Setting setting = SessionManager.get().getSettingModel();
        if (setting != null) {
            //For testing purpose only, if you need to look the TEST ADS, just uncomment below code and when you get the ads, then don't forget to comment the below code
            setting.setInterstital_ad_id(INTER_AD);
            InterstitialAd.load(mActivity, setting.getInterstital_ad_id(), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    mInterstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    mInterstitialAd = null;
                }
            });
        }
    }

    public static void showIntAds(final Activity mActivity, final Handler handler) {
        showIntAds(mActivity, null, ZERO, handler);
    }

    public static void showIntAds(final Activity mActivity, final Class<?> cls) {
        showIntAds(mActivity, cls, MINUS, null);
    }

    public static void showIntAds(final Activity mActivity, final Class<?> cls, final int what, final Handler handler) {
        final Setting setting = SessionManager.get().getSettingModel();
        final boolean isPremium = SessionManager.get().isPremiumUser();
        if (setting != null && !isPremium) {
            if (setting.getInterstital_ad() == HANDLER_SUCCESS) {
                if (setting.getInterstital_ad_click() <= LAUNCHED_AD) {
                    LAUNCHED_AD = ZERO;
                    if (mInterstitialAd != null) {
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                openNextActivity(mActivity, cls, what, handler);
                                firstLoadAds(mActivity);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                openNextActivity(mActivity, cls, what, handler);
                            }
                        });
                        mInterstitialAd.show(mActivity);
                    } else {
                        openNextActivity(mActivity, cls, what, handler);
                    }
                } else {
                    LAUNCHED_AD++;
                    openNextActivity(mActivity, cls, what, handler);
                }
            } else {
                openNextActivity(mActivity, cls, what, handler);
            }
        } else {
            openNextActivity(mActivity, cls, what, handler);
        }
    }

    private static void openNextActivity(final Activity mActivity, final Class<?> cls, final int what, final Handler handler) {
        if (handler != null) {
            final Message message = Message.obtain();
            handler.sendEmptyMessage(what);
            message.setTarget(handler);
            message.sendToTarget();
        } else {
            ScreenManager.showCustomScreen(mActivity, cls);
        }
    }
    //================ INTERSTITIAL END ================

    //================ NATIVE START ====================
    public static void initNativeAds(final Activity mActivity, final TemplateView template) {
        final Setting setting = SessionManager.get().getSettingModel();
        final boolean isPremium = SessionManager.get().isPremiumUser();
        if (setting != null && !isPremium) {
            if (setting.getNative_ad() == HANDLER_SUCCESS) {
                if (TEST_ADS) {
                    setting.setNative_ad_id(NATIVE_AD);
                }
                AdLoader adLoader = new AdLoader.Builder(mActivity, setting.getNative_ad_id())
                        .forNativeAd(template::setNativeAd).withAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();
                                Utils.sout("onAdClosed: ");
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                super.onAdFailedToLoad(loadAdError);
                                Utils.sout("onAdFailedToLoad: ");
                            }

                            @Override
                            public void onAdOpened() {
                                Utils.sout("onAdOpened: ");
                                super.onAdOpened();
                            }

                            @Override
                            public void onAdLoaded() {
                                template.setVisibility(View.VISIBLE);
                                super.onAdLoaded();
                            }

                            @Override
                            public void onAdClicked() {
                                Utils.sout("onAdClicked: ");
                                super.onAdClicked();
                            }

                            @Override
                            public void onAdImpression() {
                                super.onAdImpression();
                            }
                        })
                        .build();

                adLoader.loadAd(new AdRequest.Builder().build());
            }
        }
    }
    //================ NATIVE END ====================

}
