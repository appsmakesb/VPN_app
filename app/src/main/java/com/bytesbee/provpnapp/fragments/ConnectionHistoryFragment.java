package com.bytesbee.provpnapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.adapter.ConnectionAdapter;
import com.bytesbee.provpnapp.managers.ConnectionManager;
import com.bytesbee.provpnapp.models.ConnectionInfo;
import com.bytesbee.provpnapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Daily data fragment.
 */
public class ConnectionHistoryFragment extends Fragment {
    /**
     * The Month data.
     */
    List<ConnectionInfo> dataList;
    /**
     * The Root view.
     */
    View rootView;
    private ConnectionAdapter dataAdapter;
    private RecyclerView recList;
    private TextView txtDate;
    private TextView txtLocation;
    private TextView txtTimer;
    private TextView txtDownload;
    private TextView txtUpload;

    @SuppressLint("NotifyDataSetChanged")
    public void refreshData() {
        dataList.clear();
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_connection_history, container, false);
        txtDate = rootView.findViewById(R.id.txtDate);
        txtLocation = rootView.findViewById(R.id.txtLocation);
        txtTimer = rootView.findViewById(R.id.txtTimer);
        txtDownload = rootView.findViewById(R.id.txtDownload);
        txtUpload = rootView.findViewById(R.id.txtUpload);
        init();
        return rootView;
    }

    private void init() {
        Utils.PaintTextView(txtDate, txtLocation, txtTimer, txtDownload, txtUpload);
        recList = rootView.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
        recList.setLayoutManager(layoutManager);
        dataList = createList(30);
        dataAdapter = new ConnectionAdapter(getActivity(), dataList);
        recList.setAdapter(dataAdapter);
    }

    private List<ConnectionInfo> createList(int size) {
        List<ConnectionInfo> result = new ArrayList<>();
        final String _data = ConnectionManager.get().getHistory();
        if (!_data.equals("")) {
            JSONObject js;
            try {
                js = new JSONObject(_data);
                JSONArray array = js.getJSONArray(getString(R.string.lblHistory));
                if (array.length() <= size) {

                    for (int i = array.length() - 1; i >= 0; i--) {
                        JSONObject jo = array.getJSONObject(i);
                        result.add(new ConnectionInfo(jo.getLong("date"), jo.getString("location"), jo.getString("totalTime"), jo.getString("download"), jo.getString("upload")));
                    }
                } else {

                    int count = 0;
                    while (count <= size) {
                        JSONObject jo = array.getJSONObject((array.length() - 1) - count);
                        result.add(new ConnectionInfo(jo.getLong("date"), jo.getString("location"), jo.getString("totalTime"), jo.getString("download"), jo.getString("upload")));
                        count++;
                    }
                }
            } catch (Exception e) {
                Utils.getErrors(e);
            }
        } else {

            for (int i = 0; i < size; i++) {
                result.add(new ConnectionInfo());
            }
        }

        return result;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        dataList = createList(30);
        dataAdapter = new ConnectionAdapter(getActivity(), dataList);
        recList.setAdapter(dataAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
