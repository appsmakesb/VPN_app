package com.bytesbee.provpnapp.utils;

import static com.bytesbee.provpnapp.constants.IConstants.DEFAULT_UPDATE_URL;
import static com.bytesbee.provpnapp.constants.IConstants.DEFAULT_UPDATE_URL_2;
import static com.bytesbee.provpnapp.constants.IConstants.FALSE;
import static com.bytesbee.provpnapp.constants.IConstants.HANDLER_FAILURE;
import static com.bytesbee.provpnapp.constants.IConstants.HANDLER_SUCCESS;
import static com.bytesbee.provpnapp.constants.IConstants.IMAGE_LOGO_PATH;
import static com.bytesbee.provpnapp.constants.IConstants.P1M;
import static com.bytesbee.provpnapp.constants.IConstants.P3M;
import static com.bytesbee.provpnapp.constants.IConstants.P6M;
import static com.bytesbee.provpnapp.constants.IConstants.SUCCESS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bytesbee.provpnapp.BuildConfig;
import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.constants.IConstants;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.models.APIKey;
import com.bytesbee.provpnapp.models.CallbackSettings;
import com.bytesbee.provpnapp.models.Setting;
import com.bytesbee.provpnapp.rests.ApiInterface;
import com.bytesbee.provpnapp.rests.RestAdapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {
    public static final boolean IS_TRIAL = false;

    public static String getAPIUrl(Context context) {
        return context.getString(R.string.api_url);
    }

    public static String getAPIKey(Context context) {
        return context.getString(R.string.api_key);
    }

    public static String getTokenURL(Context context) {
        return getAPIUrl(context) + File.separator + "register.php";
    }

    public static void getErrors(final Exception e) {
        final String stackTrace = Log.getStackTraceString(e);
        Utils.sout("" + stackTrace);
    }

    /**
     * to print message on console
     */
    public static void sout(final String msg) {
        if (IS_TRIAL) {
            System.out.println(IConstants.SOUT_MSG_PREFIX + msg);
        }
    }

    public static Bitmap getBitmapFromURL(final String strURL) {
        try {
            final URL url = new URL(strURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void RTLSupport(Window window) {
        try {
            window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static void setSettingImageView(Context context, Setting setting, ImageView imgAppLogo) {
        try {
            Glide.with(context)
                    .load(getAPIUrl(context) + IMAGE_LOGO_PATH + setting.getApp_logo())
                    .placeholder(R.drawable.image_load)
                    .into(imgAppLogo);
        } catch (Exception e) {
            Glide.with(context)
                    .load(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(imgAppLogo);
        }
    }

    /**
     * check user is connected to internet or not
     *
     * @return true =>> user is connected to internet
     * false ==> user is not connected to internet
     */
    public static boolean isOnline(final Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            }
        } else {
            final NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return FALSE;
    }


    /**
     * shows toast message
     *
     * @param mActivity context
     * @param message   message to be displayed
     */
    public static void showToast(Activity mActivity, String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

    public static String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static void setDarkMode(final boolean b) {
        try {
            if (b) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static Typeface getRegularFont(Context context) {
        return ResourcesCompat.getFont(context, R.font.poppins);
    }

    public static Typeface getBoldFont(Context context) {
        return ResourcesCompat.getFont(context, R.font.poppins_bold);
    }

    public static void rateApp(final Activity mActivity) {
        final String appName = mActivity.getPackageName();
        try {
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DEFAULT_UPDATE_URL_2 + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DEFAULT_UPDATE_URL + appName)));
        }
    }

    public static String getBase64Encoded(String text) {
        try {
            final byte[] data = text.getBytes(StandardCharsets.UTF_8);
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public static String getBase64Decoded(String strDecode) {
        try {
            byte[] data = Base64.decode(strDecode, Base64.DEFAULT); //https://www.codegrepper.com/code-examples/java/base64.decode+android
            return new String(data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecode;
    }

    public static void setServerImageView(Context context, String imgUrl, ImageView mImageView) {
        try {
            mImageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(Utils.getAPIUrl(context) + File.separator + imgUrl).placeholder(R.drawable.image_load).diskCacheStrategy(DiskCacheStrategy.ALL).into(mImageView);
        } catch (Exception ignored) {
        }
    }

    public static void shareApp(final Activity mActivity, final String title) {
        final String app_name = android.text.Html.fromHtml(title).toString();
        final String share_text = android.text.Html.fromHtml(mActivity.getResources().getString(R.string.msgShareContent)).toString();
        final Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, app_name + "\n\n" + share_text + "\n\n" + DEFAULT_UPDATE_URL + mActivity.getPackageName());
        sendIntent.setType("text/plain");
        mActivity.startActivity(sendIntent);
    }

    public static void shareApp(final Activity mActivity) {
        shareApp(mActivity, mActivity.getResources().getString(R.string.msgShareTitle));
    }

    public static void requestSettingApi(final Context context) {
        requestSettingApi(context, null);
    }

    public static void requestSettingApi(final Context context, final Handler handler) {
        try {
            final ApiInterface apiInterface = RestAdapter.createAPI(context);
            APIKey apiKey = new APIKey();
            apiKey.setApi_key(Utils.getBase64Encoded(Utils.getAPIKey(context)));
            final Call<CallbackSettings> callbackCall = apiInterface.getSettings(apiKey);
            callbackCall.enqueue(new Callback<CallbackSettings>() {
                @Override
                public void onResponse(@NonNull Call<CallbackSettings> call, @NonNull Response<CallbackSettings> response) {
                    CallbackSettings resp = response.body();
                    Message message = Message.obtain();
                    if (resp != null && resp.status == SUCCESS) {
                        SessionManager session = new SessionManager(context);
                        session.setSettingModel(resp.data);
                        message.what = HANDLER_SUCCESS;
                    } else {
                        message.what = HANDLER_FAILURE;
                    }
                    if (handler != null) {
                        message.setTarget(handler);
                        message.sendToTarget();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CallbackSettings> call, @NonNull Throwable t) {
                    Utils.sout(t.getMessage());
                    if (handler != null) {
                        Message message = Message.obtain();
                        message.setTarget(handler);
                        message.what = HANDLER_FAILURE;
                        message.sendToTarget();
                    }
                }
            });
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public static void PaintTextView(final TextView... txtView) {
        for (int i = 0; i < txtView.length; i++) {
            int finalI = i;
            txtView[i].post(() -> {
                int length = txtView[finalI].getMeasuredWidth();
                float angle = 45;
                Shader textShader = new LinearGradient(0, 0, (int) (Math.sin(Math.PI * angle / 180) * length),
                        (int) (Math.cos(Math.PI * angle / 180) * length),
                        new int[]{0xFF29B6F6, 0xFFa5dee5},
                        null,
                        Shader.TileMode.CLAMP);
                txtView[finalI].getPaint().setShader(textShader);
                txtView[finalI].invalidate();
            });
        }
    }

    public static String getDuration(Context context, String duration) {
        String strDuration;
        if (duration.equalsIgnoreCase(P1M)) {
            strDuration = context.getString(R.string.lblMonthly);
        } else if (duration.equalsIgnoreCase(P3M)) {
            strDuration = context.getString(R.string.lblQuarterly);
        } else if (duration.equalsIgnoreCase(P6M)) {
            strDuration = context.getString(R.string.lblHalfYearly);
        } else {
            strDuration = context.getString(R.string.lblYearly);
        }
        return strDuration;

    }
}
