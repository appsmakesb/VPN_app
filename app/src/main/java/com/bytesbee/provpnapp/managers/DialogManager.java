package com.bytesbee.provpnapp.managers;

import static com.bytesbee.provpnapp.constants.IConstants.ZERO;

import android.content.Context;

import com.bytesbee.provpnapp.R;

public class DialogManager {

    public static void showYesNo(Context context, int title, int message, DialogListener dialogListener) {
        BottomFluxDialog.confirmDialog(context)
                .setTextTitle(context.getString(title))
                .setTextMessage(context.getString(message))
                .setImageDialog(ZERO)
                .setLeftButtonText(context.getString(R.string.lblNo))
                .setRightButtonText(context.getString(R.string.lblYes))
                .setConfirmListener(new BottomFluxDialog.OnConfirmListener() {
                    @Override
                    public void onLeftClick() {

                    }

                    @Override
                    public void onRightClick() {
                        dialogListener.onYesClick();
                    }
                })
                .show();
    }

    public static void showOK(Context context, int title, int message, DialogListener dialogListener) {
        BottomFluxDialog.alertDialog(context)
                .setTextTitle(context.getString(title))
                .setTextMessage(context.getString(message))
                .setImageDialog(ZERO)
                .setAlertButtonText(context.getString(R.string.lblOK))
                .setAlertListener(dialogListener::onYesClick)
                .show();
    }

    public static void showDisconnect(Context context, int title, int message, DialogListener dialogListener) {
        BottomFluxDialog.confirmDialog(context)
                .setTextTitle(context.getString(title))
                .setTextMessage(context.getString(message))
                .setImageDialog(ZERO)
                .setLeftButtonText(context.getString(R.string.cancel))
                .setRightButtonText(context.getString(R.string.lblDisconnect))
                .setConfirmListener(new BottomFluxDialog.OnConfirmListener() {
                    @Override
                    public void onLeftClick() {

                    }

                    @Override
                    public void onRightClick() {
                        dialogListener.onYesClick();
                    }
                })
                .show();
    }

    public interface DialogListener {
        void onYesClick();
    }
}
