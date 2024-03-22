package com.bytesbee.provpnapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.managers.HistoryManager;
import com.bytesbee.provpnapp.speedtest.GetSpeedTestHostsHandler;
import com.bytesbee.provpnapp.speedtest.HttpDownloadTest;
import com.bytesbee.provpnapp.speedtest.HttpUploadTest;
import com.bytesbee.provpnapp.speedtest.PingTest;
import com.bytesbee.provpnapp.utils.TickProgressBar;
import com.bytesbee.provpnapp.utils.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * The type Speed test fragment.
 */
public class SpeedTestFragment extends Fragment {
    private final DecimalFormat dec = new DecimalFormat("#.##");
    /**
     * The View.
     */
    private View view;
    /**
     * The Get speed test hosts handler.
     */
    private GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
    /**
     * The Temp black list.
     */
    private HashSet<String> tempBlackList;

//    private TextView tv_information;
    /**
     * The Upload addr.
     */
    private String uploadAddr;
    /**
     * The Info.
     */
    private List<String> info;
    /**
     * The Distance.
     */
    private double distance;
    private LineChart lcMeasure;
    private LineDataSet lineDataSet;
    private LineData lineData;
    private TickProgressBar tickProgressMeasure;
    private ImageView ivPBDownload;
    private ImageView ivPBUpload;
    private ImageView tvBegin;
    private TextView tv_ping_value;
    private TextView tvDownload;
    private TextView tvUpload;
    private TextView myHostname, myLocationName, myPingValue;
    private float i = 0, j = 0, k = 0;
    private TextView tvDownloadU;
    private TextView tvUploadU;
    private SharedPreferences sharedPref;
    private boolean testing = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_speed_test, container, false);
        init();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Init.
     */
    public void init() {
        sharedPref = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);

        tvDownloadU = view.findViewById(R.id.tv_download_unit);
        tvDownloadU.setText(sharedPref.getString("UNIT", "Mbps"));
        tvUploadU = view.findViewById(R.id.tv_upload_unit);
        tvUploadU.setText(sharedPref.getString("UNIT", "Mbps"));
        tvBegin = view.findViewById(R.id.tv_start);
        tv_ping_value = view.findViewById(R.id.tv_ping_value);
        tvDownload = view.findViewById(R.id.tv_download_value);
        tvUpload = view.findViewById(R.id.tv_upload_value);
        lcMeasure = view.findViewById(R.id.linechart);
        tickProgressMeasure = view.findViewById(R.id.tickProgressBar);
        ivPBDownload = view.findViewById(R.id.iv_download);
        ivPBUpload = view.findViewById(R.id.iv_upload);
        //host , location , ping , textview
        myHostname = view.findViewById(R.id.myHostName);
        myLocationName = view.findViewById(R.id.myLocationName);
        myPingValue = view.findViewById(R.id.myPingValue);
        //host , location , ping , datametar type textview

        TextView txtPingLabel = view.findViewById(R.id.txtPingLabel);
        TextView txtDownloadLabel = view.findViewById(R.id.txtDownloadLabel);
        TextView txtUploadLabel = view.findViewById(R.id.txtUploadLabel);
        tickProgressMeasure.setMax(100 * 100);

        Utils.PaintTextView(txtPingLabel, txtDownloadLabel, txtUploadLabel);
        ivPBUpload.setAlpha(0.5f);
        ivPBDownload.setAlpha(0.5f);
        tvBegin.setImageResource(R.drawable.ic_play);
        testing = false;
        List<Entry> entryList = new ArrayList<>();
        entryList.add(new Entry(0, 0));
        lineDataSet = new LineDataSet(entryList, "");
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setColor(Color.rgb(145, 174, 210));
        lineDataSet.setCircleColor(Color.rgb(145, 174, 210));
        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawFilled(false);
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setDrawCircles(false);
        lineData = new LineData(lineDataSet);
        lcMeasure.setData(lineData);
        lcMeasure.getAxisRight().setDrawGridLines(false);
        lcMeasure.getAxisRight().setDrawLabels(false);
        lcMeasure.getAxisLeft().setDrawGridLines(false);
        lcMeasure.getAxisLeft().setDrawLabels(false);
        lcMeasure.fitScreen();
        lcMeasure.setVisibleXRange(0, 10);
        lcMeasure.setNoDataText("TAP SCAN");
        lcMeasure.setNoDataTextColor(R.color.cp_0);
        lcMeasure.getXAxis().setDrawGridLines(false);
        lcMeasure.getXAxis().setDrawLabels(false);
        lcMeasure.getLegend().setEnabled(false);
        lcMeasure.getDescription().setEnabled(false);
        lcMeasure.setScaleEnabled(true);
        lcMeasure.setDrawBorders(false);
        lcMeasure.getAxisLeft().setEnabled(false);
        lcMeasure.getAxisRight().setEnabled(false);
        lcMeasure.getXAxis().setEnabled(false);
        lcMeasure.setViewPortOffsets(10f, 10f, 10f, 10f);
        lcMeasure.animateX(1000);
        tempBlackList = new HashSet<>();
        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
        defaultValues();
        tvBegin.setOnClickListener(v -> {
            if (!testing) {
                testing = true;
                testSpeed();
            }
        });
    }

    private void testSpeed() {
        tvBegin.setImageResource(R.drawable.ic_stop);
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(650);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        if (getSpeedTestHostsHandler == null) {
            getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
            getSpeedTestHostsHandler.start();
        }
        new Thread(() -> {
            if (getActivity() == null)
                return;

            int timeCount = 600;
            while (!getSpeedTestHostsHandler.isFinished()) {
                timeCount--;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                if (timeCount <= 0) {
                    if (getActivity() == null)
                        return;
                    try {
                        getActivity().runOnUiThread(() -> {
                            testing = false;
                            tvBegin.setImageResource(R.drawable.ic_play);
                        });
                        getSpeedTestHostsHandler = null;
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            HashMap<Integer, String> mapKey = getSpeedTestHostsHandler.getMapKey();
            HashMap<Integer, List<String>> mapValue = getSpeedTestHostsHandler.getMapValue();
            double selfLat = getSpeedTestHostsHandler.getSelfLat();
            double selfLon = getSpeedTestHostsHandler.getSelfLon();
            double tmp = 19349458;
            double dist = 0.0;
            int findServerIndex = 0;
            for (int index : mapKey.keySet()) {
                if (tempBlackList.contains(mapValue.get(index).get(5))) {
                    continue;
                }
                Location source = new Location("Source");
                source.setLatitude(selfLat);
                source.setLongitude(selfLon);
                List<String> ls = mapValue.get(index);
                Location dest = new Location("Dest");
                dest.setLatitude(Double.parseDouble(ls.get(0)));
                dest.setLongitude(Double.parseDouble(ls.get(1)));
                distance = source.distanceTo(dest);
                if (tmp > distance) {
                    tmp = distance;
                    dist = distance;
                    findServerIndex = index;
                }
            }

            uploadAddr = mapKey.get(findServerIndex);
            info = mapValue.get(findServerIndex);

            distance = dist;
            if (info != null) {
                if (info.size() > 0) {
                    getActivity().runOnUiThread(() -> {
                        myHostname.setText(Html.fromHtml(String.format("%s", info.get(5))));
                        myLocationName.setText(String.format("%s [%s km]", info.get(3), new DecimalFormat("#.##").format(distance / 1000)));
                    });
                    getActivity().runOnUiThread(() -> {
                        tv_ping_value.setText("0");
                        myPingValue.setText("0" + " ms");
                        tvDownload.setText("0");
                        tvUpload.setText("0");
                        i = 0f;
                        j = 0f;
                        k = 0f;
                        ivPBDownload.setAlpha(0.5f);
                        ivPBUpload.setAlpha(0.5f);
                    });
                    final List<Double> pingRateList = new ArrayList<>();
                    final List<Double> downloadRateList = new ArrayList<>();
                    final List<Double> uploadRateList = new ArrayList<>();
                    boolean pingTestStarted = false;
                    boolean pingTestFinished = false;
                    boolean downloadTestStarted = false;
                    boolean downloadTestFinished = false;
                    boolean uploadTestStarted = false;
                    boolean uploadTestFinished = false;
                    //Init Test
                    final PingTest pingTest = new PingTest(info.get(6).replace(":8080", ""), 3);
                    uploadAddr = uploadAddr.replaceAll("http://", "https://");
                    Utils.sout("TestURL : " + uploadAddr);
                    final HttpDownloadTest downloadTest = new HttpDownloadTest(uploadAddr.replace(uploadAddr.split("/")[uploadAddr.split("/").length - 1], ""));
                    final HttpUploadTest uploadTest = new HttpUploadTest(uploadAddr);

                    while (true) {
                        if (!pingTestStarted) {
                            pingTest.start();
                            pingTestStarted = true;
                        }
                        if (pingTestFinished && !downloadTestStarted) {
                            downloadTest.start();
                            downloadTestStarted = true;
                        }
                        if (downloadTestFinished && !uploadTestStarted) {
                            uploadTest.start();
                            uploadTestStarted = true;
                        }
                        if (pingTestFinished) {
                            if (pingTest.getAvgRtt() == 0) {
                            } else {
                                if (getActivity() == null)
                                    return;
                                try {
                                    getActivity().runOnUiThread(() -> {
                                        tickProgressMeasure.setmPUnit("ms");
                                        tv_ping_value.setText(dec.format(pingTest.getAvgRtt()));
                                        myPingValue.setText(dec.format(pingTest.getAvgRtt()) + " ms");
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            pingRateList.add(pingTest.getInstantRtt());
                            if (getActivity() == null)
                                return;
                            try {
                                getActivity().runOnUiThread(() -> {
                                    tickProgressMeasure.setmPUnit("ms");
                                    tv_ping_value.setText(dec.format(pingTest.getInstantRtt()));
                                    myPingValue.setText(dec.format(pingTest.getInstantRtt()) + " ms");

                                    tickProgressMeasure.setProgress((int) (pingTest.getInstantRtt() * 100));
                                    if (i == 0) {
                                        lcMeasure.clear();
                                        lineDataSet.clear();
//                                        lineDataSet.setColor(Color.rgb(255, 207, 223));//Light Pink - #FFCFDF
                                        lineDataSet.setColor(Color.rgb(104, 159, 56));//Green - #689F38
                                        lineData = new LineData(lineDataSet);
                                        lcMeasure.setData(lineData);
                                        lcMeasure.invalidate();
                                    }
                                    if (i > 10) {
                                        LineData data = lcMeasure.getData();
                                        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                                        if (set != null) {
                                            data.addEntry(new Entry(i, (float) (10 * pingTest.getInstantRtt())), 0);
                                            lcMeasure.notifyDataSetChanged();
                                            lcMeasure.setVisibleXRange(0, i);
                                            lcMeasure.invalidate();
                                        }
                                    } else {
                                        lcMeasure.setVisibleXRange(0, 10);
                                        LineData data = lcMeasure.getData();
                                        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                                        if (set != null) {
                                            data.addEntry(new Entry(i, (float) (10 * pingTest.getInstantRtt())), 0);
                                            lcMeasure.notifyDataSetChanged();
                                            lcMeasure.invalidate();
                                        }
                                    }
                                    i++;
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (pingTestFinished) {
                            if (downloadTestFinished) {
                                if (downloadTest.getFinalDownloadRate() == 0) {
                                } else {
                                    if (getActivity() == null)
                                        return;
                                    try {
                                        getActivity().runOnUiThread(() -> {
                                            tickProgressMeasure.setmPUnit(sharedPref.getString("UNIT", "Mbps"));
                                            switch (sharedPref.getString("UNIT", "Mbps")) {
                                                case "MBps":
                                                    tvDownload.setText(dec.format(0.125 * downloadTest.getFinalDownloadRate()) + "");
                                                    break;
                                                case "kBps":
                                                    tvDownload.setText(dec.format(125 * downloadTest.getFinalDownloadRate()) + "");
                                                    break;
                                                case "Mbps":
                                                    tvDownload.setText(dec.format(downloadTest.getFinalDownloadRate()) + "");
                                                    break;
                                                case "kbps":
                                                    tvDownload.setText(dec.format(1000 * downloadTest.getFinalDownloadRate()) + "");
                                                    break;
                                                default:
                                                    break;
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                double downloadRate = downloadTest.getInstantDownloadRate();
                                downloadRateList.add(downloadRate);
                                if (getActivity() == null)
                                    return;
                                try {
                                    getActivity().runOnUiThread(() -> {
                                        tickProgressMeasure.setmPUnit(sharedPref.getString("UNIT", "Mbps"));
                                        switch (sharedPref.getString("UNIT", "Mbps")) {
                                            case "MBps":
                                                tvDownload.setText(dec.format(0.125 * downloadTest.getInstantDownloadRate()) + "");
                                                tickProgressMeasure.setProgress((int) (0.125 * downloadTest.getInstantDownloadRate() * 100));
                                                break;
                                            case "kBps":
                                                tvDownload.setText(dec.format(125 * downloadTest.getInstantDownloadRate()) + "");
                                                tickProgressMeasure.setProgress((int) (125 * downloadTest.getInstantDownloadRate() * 100));
                                                break;
                                            case "Mbps":
                                                tvDownload.setText(dec.format(downloadTest.getInstantDownloadRate()) + "");
                                                tickProgressMeasure.setProgress((int) (downloadTest.getInstantDownloadRate() * 100));
                                                break;
                                            case "kbps":
                                                tvDownload.setText(dec.format(1000 * downloadTest.getInstantDownloadRate()) + "");
                                                tickProgressMeasure.setProgress((int) (1000 * downloadTest.getInstantDownloadRate() * 100));
                                                break;
                                            default:
                                                break;
                                        }

                                        if (j == 0) {
                                            ivPBDownload.setAlpha(1.0f);
                                            ivPBUpload.setAlpha(0.5f);
                                            lcMeasure.clear();
                                            lineDataSet.clear();
//                                            lineDataSet.setColor(Color.rgb(224, 249, 181)); //Light Green - #E0F9B5
                                            lineDataSet.setColor(Color.rgb(255, 167, 38));//Orange - #FFA726
                                            lineData = new LineData(lineDataSet);
                                            lcMeasure.setData(lineData);
                                            lcMeasure.invalidate();
                                        }
                                        if (j > 100) {
                                            LineData data = lcMeasure.getData();
                                            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                                            if (set != null) {
                                                data.addEntry(new Entry(j, (float) (1000 * downloadTest.getInstantDownloadRate())), 0);
                                                lcMeasure.notifyDataSetChanged();
                                                lcMeasure.setVisibleXRange(0, j);
                                                lcMeasure.invalidate();
                                            }
                                        } else {
                                            lcMeasure.setVisibleXRange(0, 100);
                                            LineData data = lcMeasure.getData();
                                            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                                            if (set != null) {
                                                data.addEntry(new Entry(j, (float) (1000 * downloadTest.getInstantDownloadRate())), 0);
                                                lcMeasure.notifyDataSetChanged();
                                                lcMeasure.invalidate();
                                            }
                                        }
                                        j++;
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (downloadTestFinished) {
                            if (uploadTestFinished) {
                                if (uploadTest.getFinalUploadRate() == 0) {

                                } else {
                                    if (getActivity() == null)
                                        return;
                                    try {
                                        getActivity().runOnUiThread(() -> {
                                            tickProgressMeasure.setmPUnit(sharedPref.getString("UNIT", "Mbps"));
                                            switch (sharedPref.getString("UNIT", "Mbps")) {
                                                case "MBps":
                                                    tvUpload.setText(String.format("%.1f", dec.format(0.125 * uploadTest.getFinalUploadRate())));
                                                    break;
                                                case "kBps":
                                                    tvUpload.setText(String.format("%.1f", dec.format(125 * uploadTest.getFinalUploadRate())));
                                                    break;
                                                case "Mbps":
                                                    tvUpload.setText(String.format("%.1f", dec.format(uploadTest.getFinalUploadRate())));
                                                    break;
                                                case "kbps":
                                                    tvUpload.setText(String.format("%.1f", dec.format(1000 * uploadTest.getFinalUploadRate())));
                                                    break;
                                                default:

                                                    break;
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                double uploadRate = uploadTest.getInstantUploadRate();
                                uploadRateList.add(uploadRate);
                                if (getActivity() == null)
                                    return;
                                try {
                                    getActivity().runOnUiThread(() -> {
                                        tickProgressMeasure.setmPUnit(sharedPref.getString("UNIT", "Mbps"));
                                        switch (sharedPref.getString("UNIT", "Mbps")) {
                                            case "MBps":
                                                tvUpload.setText(dec.format(0.125 * uploadTest.getInstantUploadRate()) + "");
                                                tickProgressMeasure.setProgress((int) (0.125 * uploadTest.getInstantUploadRate() * 100));
                                                break;
                                            case "kBps":
                                                tvUpload.setText(dec.format(125 * uploadTest.getInstantUploadRate()) + "");
                                                tickProgressMeasure.setProgress((int) (125 * uploadTest.getInstantUploadRate() * 100));
                                                break;
                                            case "Mbps":
                                                tvUpload.setText(dec.format(uploadTest.getInstantUploadRate()) + "");
                                                tickProgressMeasure.setProgress((int) (uploadTest.getInstantUploadRate() * 100));
                                                break;
                                            case "kbps":
                                                tvUpload.setText(dec.format(1000 * uploadTest.getInstantUploadRate()) + "");
                                                tickProgressMeasure.setProgress((int) (1000 * uploadTest.getInstantUploadRate() * 100));
                                                break;
                                            default:

                                                break;
                                        }

                                        if (k == 0) {
                                            ivPBDownload.setAlpha(1.0f);
                                            ivPBUpload.setAlpha(1.0f);
                                            lcMeasure.clear();
                                            lineDataSet.clear();
//                                            lineDataSet.setColor(Color.rgb(145, 174, 210));//Light Blue - #91AED2
                                            lineDataSet.setColor(Color.rgb(244, 81, 30));//Red #F4511E
                                            lineData = new LineData(lineDataSet);
                                            lcMeasure.setData(lineData);
                                            lcMeasure.invalidate();
                                        }
                                        if (k > 100) {
                                            LineData data = lcMeasure.getData();
                                            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                                            if (set != null) {
                                                data.addEntry(new Entry(k, (float) (1000 * uploadTest.getInstantUploadRate())), 0);
                                                lcMeasure.notifyDataSetChanged();
                                                lcMeasure.setVisibleXRange(0, k);
                                                lcMeasure.invalidate();
                                            }
                                        } else {
                                            lcMeasure.setVisibleXRange(0, 100);
                                            LineData data = lcMeasure.getData();
                                            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                                            if (set != null) {
                                                data.addEntry(new Entry(k, (float) (1000 * uploadTest.getInstantUploadRate())), 0);
                                                lcMeasure.notifyDataSetChanged();
                                                lcMeasure.invalidate();
                                            }
                                        }
                                        k++;
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (pingTestFinished && downloadTestFinished && uploadTest.isFinished()) {
                            break;
                        }
                        if (pingTest.isFinished()) {
                            pingTestFinished = true;
                        }
                        if (downloadTest.isFinished()) {
                            downloadTestFinished = true;
                        }
                        if (uploadTest.isFinished()) {
                            uploadTestFinished = true;
                        }
                        if (pingTestStarted && !pingTestFinished) {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                Thread.currentThread().interrupt();
                            }
                        } else {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                    if (getActivity() == null)
                        return;
                    try {
                        getActivity().runOnUiThread(() -> {
                            testing = false;
                            tvBegin.setImageResource(R.drawable.ic_play);
                            String _data = HistoryManager.get().getHistory();
                            JSONObject jsonData = new JSONObject();
                            try {
                                jsonData.put("date", String.valueOf(System.currentTimeMillis()));
                                jsonData.put("ping", tv_ping_value.getText());
                                jsonData.put("download", tvDownload.getText());
                                jsonData.put("upload", tvUpload.getText());
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
                                HistoryManager.get().setHistory(new_data.toString());
                            } catch (Exception e) {
                                Utils.getErrors(e);
                            }
                            testing = false;
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                tvBegin.setImageResource(R.drawable.ic_play);
                testing = false;
            }
        }).start();
    }

    private void defaultValues() {
        tickProgressMeasure.setProgress(0);
        tv_ping_value.setText("0");
        myPingValue.setText("0" + " ms");
        tvDownload.setText("0");
        tvUpload.setText("0");
    }

    @Override
    public void onResume() {
        super.onResume();
        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
        if (tvDownloadU != null)
            tvDownloadU.setText(sharedPref.getString("UNIT", "Mbps"));
        if (tvUploadU != null)
            tvUploadU.setText(sharedPref.getString("UNIT", "Mbps"));
    }

}
