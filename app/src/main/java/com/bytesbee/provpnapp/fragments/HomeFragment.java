package com.bytesbee.provpnapp.fragments;

import static com.bytesbee.provpnapp.constants.IConstants.DEF_FRAME;
import static com.bytesbee.provpnapp.constants.IConstants.TYPE_PAID;
import static com.bytesbee.provpnapp.constants.IConstants.ZERO;
import static com.bytesbee.provpnapp.managers.UsageManager.KEY_TOTAL_CONNECTIONS;
import static com.bytesbee.provpnapp.managers.UsageManager.STR_CONNECTIONS;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.activity.SelectServersActivity;
import com.bytesbee.provpnapp.constants.IConstants;
import com.bytesbee.provpnapp.managers.AdsManager;
import com.bytesbee.provpnapp.managers.ConnectionManager;
import com.bytesbee.provpnapp.managers.DialogManager;
import com.bytesbee.provpnapp.managers.ScreenManager;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.managers.UsageManager;
import com.bytesbee.provpnapp.models.VPNServers;
import com.bytesbee.provpnapp.utils.Utils;
import com.google.android.ads.nativetemplates.TemplateView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private View root_view;
    private Activity mActivity;
    private TextView txtStatus, txtCountryName, txtTime, txtUploadSpeed, txtDownloadSpeed;
    private ImageView imgCountryFlag;
    private TemplateView templateView;
    private ConstraintLayout layoutServers;
    private LottieAnimationView animationView, imgConnect, imgDisconnect, imgDownload, imgUpload, imgTime;
    private boolean vpnStart = false;
    private boolean isBegin = false;
    private VPNServers server;
    private UsageManager usageManager;
    private String TODAY;
    private String conn = "";
    private long startTimer;
    private final Handler timerHandler = new Handler();

    public HomeFragment newInstance() {
        return new HomeFragment();
    }

    public boolean isVpnRunning() {
        return vpnStart;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_home, container, false);
        mActivity = requireActivity();
        usageManager = new UsageManager(mActivity);
        init();
        initData();
        initListeners();
        isServiceRunning();
        AdsManager.initNativeAds(mActivity, templateView);
        return root_view;
    }

    private void isServiceRunning() {
        setStatus(OpenVPNService.getStatus());
    }

    public void init() {
        final Date Today = Calendar.getInstance().getTime();
        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        TODAY = df.format(Today);

        txtStatus = root_view.findViewById(R.id.txtStatus);
        layoutServers = root_view.findViewById(R.id.layoutServers);
        templateView = root_view.findViewById(R.id.templateView);
        imgConnect = root_view.findViewById(R.id.imgConnect);
        imgDisconnect = root_view.findViewById(R.id.imgDisconnect);
        imgCountryFlag = root_view.findViewById(R.id.imgCountryFlag);
        txtCountryName = root_view.findViewById(R.id.txtCountryName);
        txtTime = root_view.findViewById(R.id.txtTime);
        animationView = root_view.findViewById(R.id.animationView);
        imgDownload = root_view.findViewById(R.id.imgDownload);
        imgUpload = root_view.findViewById(R.id.imgUpload);
        imgTime = root_view.findViewById(R.id.imgTime);
        txtDownloadSpeed = root_view.findViewById(R.id.txtDownloadSpeed);
        txtUploadSpeed = root_view.findViewById(R.id.txtUploadSpeed);

        imgDownload.setFrame(DEF_FRAME);
        imgUpload.setFrame(DEF_FRAME);
    }

    public void initData() {
        try {
            server = SessionManager.get().getServer();
            if (server != null) {
                txtCountryName.setText(server.getServerName());
                Utils.setServerImageView(mActivity, server.getFlagURL(), imgCountryFlag);
            } else {
                txtCountryName.setText(R.string.lblChooseServer);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }

        VpnStatus.initLogCache(mActivity.getCacheDir());
    }

    public void initListeners() {
        layoutServers.setOnClickListener(this);
        imgConnect.setOnClickListener(this);
        imgDisconnect.setOnClickListener(this);
    }

    public final Handler myChooseServer = new Handler(message -> {
        openServerList();
        return true;
    });

    private void openServerList() {
        final Intent intent = new Intent(mActivity, SelectServersActivity.class);
        serverListLauncher.launch(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.layoutServers) {
            myChooseServer.sendEmptyMessage(ZERO);
        } else if (id == R.id.imgConnect || id == R.id.imgDisconnect) {// if user is connected to any VPN server disconnect from currently connected server
            if (vpnStart) {
                DisconnectFromConnectedServer();
            } else {
                if (SessionManager.get().getServer() != null) {
                    if (animationView.getVisibility() == View.GONE) {
                        //If server is Premium and subscription period is over or cancelled, prevent to connect with premium server
                        if (!SessionManager.get().isPremiumUser() && SessionManager.get().getServer().getIsPaid() == TYPE_PAID) {
                            Utils.showToast(mActivity, getString(R.string.msgNewRequest));
                            ScreenManager.showUpgradeActivity(mActivity);
                        } else {
                            AdsManager.showIntAds(mActivity, myHandler);
                        }
                    } else {
                        stopVpn();
                    }
                } else {
                    Utils.showToast(mActivity, getString(R.string.msgChooseBestServer));
                    layoutServers.callOnClick();
                }

            }
        }
    }

    public final Handler myHandler = new Handler(message -> {
        strDownloadSpeed = getString(R.string.lblStaticSpeed);
        strUploadSpeed = getString(R.string.lblStaticSpeed);
        prepareVpn();
        return true;
    });

    public final Handler openHandler = new Handler(message -> {
        stopVpn();
        server = SessionManager.get().getServer();
        if (server != null) {
            Utils.sout("MyServer result:: " + server.getServerName());
            txtCountryName.setText(server.getServerName());
            Utils.setServerImageView(mActivity, server.getFlagURL(), imgCountryFlag);
        } else {
            txtCountryName.setText(R.string.lblChooseServer);
        }
        new Handler(Looper.getMainLooper()).postDelayed(this::prepareVpn, 1000);
        return true;
    });


    final ActivityResultLauncher<Intent> serverListLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        openHandler.sendEmptyMessage(ZERO);
                    } catch (Exception e) {
                        Utils.getErrors(e);
                    }
                } else {
                    Utils.sout(getString(R.string.msgAuthFailed));
                }
            });

    private void startVpn() {
        try {
            final String config = Utils.getBase64Decoded(server.getOvpnConfig());
            Utils.sout("config: " + config);
            OpenVpnApi.startVpn(mActivity, config, server.getServerName(), server.getUsername(), server.getPassword());

            isBegin = true;
            start_vpn();

            txtCountryName.setText(server.getServerName());
            txtStatus.setText(getString(R.string.msgWaiting, txtCountryName.getText()));
            txtStatus.setTextColor(getResources().getColor(R.color.colorTitleText));
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void prepareVpn() {
        if (!vpnStart && !isBegin) {
            if (Utils.isOnline(mActivity)) {

                Intent intent = VpnService.prepare(mActivity);
                Utils.sout("prepare VPN: " + intent);
                if (intent != null)
                    resultLauncher.launch(intent);
                else
                    startVpn();//have already permission

                txtStatus.setText(getString(R.string.msgWaiting, txtCountryName.getText()));
                txtStatus.setTextColor(getResources().getColor(R.color.colorTitleText));
                hideShowLoading(true);
            } else {
                Utils.showToast(mActivity, getString(R.string.msgNoInternetConnection));
            }
        } else {
            isBegin = false;
            stopVpn();
        }
    }

    /**
     * when permission for VPN is given by user onActivityResult
     * will be called and VPN starts
     */
    final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        startVpn();
                    } catch (Exception e) {
                        Utils.getErrors(e);
                    }
                } else {
                    stopVpn();
                    Utils.sout(getString(R.string.msgPermissionDenied));
                }
            });

    public void stopVpn() {
        try {
            vpnStart = false;
            isBegin = false;
            hideShowLoading(false);
            OpenVPNService.abortConnectionVPN = true;
            ProfileManager.setConntectedVpnProfileDisconnected(mActivity);
            OpenVPNThread.stop();
            txtStatus.setText(getString(R.string.msgTapStart));
        } catch (Exception e) {
            Utils.getErrors(e);
        }

    }

    /**
     * Show show disconnect confirm dialog
     */
    public void DisconnectFromConnectedServer() {
        DialogManager.showDisconnect(mActivity, R.string.strDisconnect, R.string.msgDisconnect, this::stopVpn);
    }

    private void start_vpn() {
        long connection_today = usageManager.getUsage(TODAY + STR_CONNECTIONS);
        long connection_total = usageManager.getUsage(KEY_TOTAL_CONNECTIONS);
        usageManager.setUsage(TODAY + STR_CONNECTIONS, connection_today + 1);
        usageManager.setUsage(KEY_TOTAL_CONNECTIONS, connection_total + 1);
    }

    /**
     * Status change with corresponding vpn connection status
     *
     * @param connectionState states which we get onRecieve() of broadcast
     */
    public void setStatus(String connectionState) {
        if (connectionState != null) {
            conn = connectionState;
//            Utils.sout("ConnectionState:: " + connectionState);
            switch (connectionState) {
                case IConstants.AUTH_FAILED:
                    txtStatus.setText(getString(R.string.msgAuthFailed));
                    break;
                case IConstants.DISCONNECTED:
                    Utils.sout("vpnStart: " + vpnStart);
                    isBegin = false;
                    try {
                        vpnStart = false;
                        OpenVPNService.setDefaultStatus();
                        txtStatus.setText(getString(R.string.msgTapStart));
                        stopTimer();
                    } catch (Exception e) {
                        Utils.getErrors(e);
                    }
                    imgConnect.setVisibility(View.VISIBLE);
                    imgDisconnect.setVisibility(View.GONE);
                    hideShowLoading(false);
                    break;

                case IConstants.WAIT:
                    txtStatus.setText(getString(R.string.msgWaiting, txtCountryName.getText()));
                    txtStatus.setTextColor(getResources().getColor(R.color.colorTitleText));
                    hideShowLoading(true);
                    break;

                case IConstants.AUTH:
                    txtStatus.setText(getString(R.string.msgObtainingServer));
                    txtStatus.setTextColor(getResources().getColor(R.color.colorTitleText));
                    hideShowLoading(true);
                    break;

                case IConstants.RECONNECTING:
                    txtStatus.setText(getString(R.string.msgReconnection));
                    txtStatus.setTextColor(getResources().getColor(R.color.colorTitleText));
                    hideShowLoading(true);
                    break;

                case IConstants.NO_NETWORK:
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        if (conn.equalsIgnoreCase(IConstants.NO_NETWORK)) {
                            txtStatus.setText(getString(R.string.msgNoConnection));
                            txtStatus.setTextColor(getResources().getColor(R.color.colorTitleText));
                            hideShowLoading(true);
                            stopTimer();
                        }
                    }, 1000 * 10);
                    break;

                case IConstants.CONNECTED:
                    vpnStart = true;// it will use after restart this activity
                    isBegin = true;
                    txtStatus.setText(getString(R.string.msgConnected));
                    imgConnect.setVisibility(View.GONE);
                    imgDisconnect.setVisibility(View.VISIBLE);
                    hideShowLoading(false);
                    imgDownload.playAnimation();
                    imgUpload.playAnimation();
                    imgTime.playAnimation();
                    startTimer = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    break;
            }
        }
    }

    private final Runnable timerRunnable = new Runnable() {
        public void run() {
            try {
                long millis = System.currentTimeMillis() - startTimer;
                Date date = new Date(millis);
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formatted = formatter.format(date);
                txtTime.setText(formatted);
                int interval = 500;
                timerHandler.postDelayed(this, interval);
            } catch (Exception ignored) {
            }
        }
    };

    private void stopTimer() {
        try {
            final String strTime = txtTime.getText().toString();
            imgDownload.pauseAnimation();
            imgUpload.pauseAnimation();
            imgTime.pauseAnimation();
            imgDownload.setFrame(DEF_FRAME);
            imgUpload.setFrame(DEF_FRAME);
            imgTime.setFrame(ZERO);
            if (timerRunnable != null) {
                timerHandler.removeCallbacks(timerRunnable);
                txtTime.setText(R.string.lblStaticTime);
            }
            String _data = ConnectionManager.get().getHistory();
            JSONObject jsonData = new JSONObject();
            try {
                jsonData.put("date", String.valueOf(System.currentTimeMillis()));
                jsonData.put("totalTime", strTime);
                jsonData.put("download", txtDownloadSpeed.getText());
                jsonData.put("upload", txtUploadSpeed.getText());
                jsonData.put("location", txtCountryName.getText());
                JSONArray array;
                if (!_data.equals("")) {
                    JSONObject js = new JSONObject(_data);
                    array = js.getJSONArray(getString(R.string.lblHistory));
                } else {
                    array = new JSONArray();
                }
                array.put(jsonData);
                JSONObject new_data = new JSONObject();
                new_data.put(getString(R.string.lblHistory), array);
                Utils.sout("Array got it: " + array);
                ConnectionManager.get().setHistory(new_data.toString());
            } catch (Exception e) {
                Utils.getErrors(e);
            }
        } catch (Exception e) {
            Utils.sout(e.getMessage());
        }
    }

    /**
     * update textview with  latest duration user is connected to VPN
     *
     * @param bytesOut upload speed
     * @param byteIn   download speed
     */
    public void updateConnectionStatus(String byteIn, String bytesOut) {
        txtDownloadSpeed.setText(byteIn);
        txtUploadSpeed.setText(bytesOut);
    }

    private String strDownloadSpeed, strUploadSpeed;

    /**
     * Receives broadcast message
     */
    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setStatus(intent.getStringExtra("state"));
            } catch (Exception e) {
                Utils.getErrors(e);
            }
            try {

                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (byteIn == null) {
                    strDownloadSpeed = getString(R.string.lblStaticSpeed);
                } else {
                    strDownloadSpeed = byteIn.split("-")[0];
                    strDownloadSpeed = strDownloadSpeed.substring(1);
                }

                if (byteOut == null) {
                    strUploadSpeed = getString(R.string.lblStaticSpeed);
                } else {
                    strUploadSpeed = byteOut.split("-")[0];
                    strUploadSpeed = strUploadSpeed.substring(1);
                }

                updateConnectionStatus(strDownloadSpeed, strUploadSpeed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * shows lottie animation  when user is about to connect to VPN
     *
     * @param value true ==> shows animation
     *              value false ==> hides animation
     */
    public void hideShowLoading(boolean value) {
        if (value) {
            animationView.setVisibility(View.VISIBLE);
        } else {
            animationView.setVisibility(View.GONE);
            imgConnect.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

        if (server == null) {
            server = SessionManager.get().getServer();
        }

        super.onResume();
    }
}
