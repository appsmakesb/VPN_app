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
import com.bytesbee.provpnapp.adapter.DataAdapter;
import com.bytesbee.provpnapp.managers.HistoryManager;
import com.bytesbee.provpnapp.models.DataInfo;
import com.bytesbee.provpnapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Daily data fragment.
 */
public class SpeedTestHistoryFragment extends Fragment {
    /**
     * The Month data.
     */
    List<DataInfo> monthData;
    /**
     * The Root view.
     */
    View rootView;
    private DataAdapter dataAdapter;
    private RecyclerView recList;
    private TextView txtDate;
    private TextView txtPing;
    private TextView txtDownload;
    private TextView txtUpload;

    @SuppressLint("NotifyDataSetChanged")
    public void refreshData() {
        monthData.clear();
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_speed_test_history, container, false);
        txtDate = rootView.findViewById(R.id.txtDate);
        txtPing = rootView.findViewById(R.id.txtTimer);
        txtDownload = rootView.findViewById(R.id.txtDownload);
        txtUpload = rootView.findViewById(R.id.txtUpload);
        init();
        return rootView;
    }

    private void init() {
        Utils.PaintTextView(txtDate, txtPing, txtDownload, txtUpload);
        recList = rootView.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
        recList.setLayoutManager(layoutManager);
        monthData = createList(30);
        dataAdapter = new DataAdapter(getActivity(), monthData);
        recList.setAdapter(dataAdapter);
    }

    private List<DataInfo> createList(int size) {
        List<DataInfo> result = new ArrayList<>();
        final String _data = HistoryManager.get().getHistory();
        if (!_data.equals("")) {
            JSONObject js;
            try {
                js = new JSONObject(_data);
                JSONArray array = js.getJSONArray("History");
                if (array.length() <= size) {

                    for (int i = array.length() - 1; i >= 0; i--) {
                        JSONObject jo = array.getJSONObject(i);
//                        result.add(new DataInfo(jo.getLong("date"), jo.getDouble("ping"), jo.getDouble("download"), jo.getDouble("upload")));
                        result.add(new DataInfo(jo.getLong("date"), Double.parseDouble(jo.getString("ping").replaceAll(" ms", "")), jo.getDouble("download"), jo.getDouble("upload")));
                    }
                } else {

                    int count = 0;
                    while (count <= size) {
                        JSONObject jo = array.getJSONObject((array.length() - 1) - count);
                        result.add(new DataInfo(jo.getLong("date"), jo.getDouble("ping"), jo.getDouble("download"), jo.getDouble("upload")));
                        count++;
                    }
                }
            } catch (Exception e) {
                Utils.getErrors(e);
            }
        } else {

            for (int i = 0; i < size; i++) {
                result.add(new DataInfo());
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
        monthData = createList(30);
        dataAdapter = new DataAdapter(getActivity(), monthData);
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
