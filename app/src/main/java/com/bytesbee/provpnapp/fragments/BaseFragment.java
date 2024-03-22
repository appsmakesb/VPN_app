package com.bytesbee.provpnapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bytesbee.provpnapp.R;

public class BaseFragment extends Fragment {
    private ProgressDialog progress;
    public Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    private void initProgressDialog() {
        try {
            progress = new ProgressDialog(getActivity());
            progress.setCancelable(false);
        } catch (Exception ignored) {

        }
    }

    void showProgress() {
        showProgress(null);
    }

    private synchronized void showProgress(String message) {
        if (progress == null) {
            initProgressDialog();
        }
        if (progress != null && !progress.isShowing()) {
            try {
                if (message != null && !message.isEmpty()) {
                    progress.setMessage(message);
                }
                progress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progress.show();
                progress.setContentView(R.layout.dialog_progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    synchronized void hideProgress() {
        try {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
