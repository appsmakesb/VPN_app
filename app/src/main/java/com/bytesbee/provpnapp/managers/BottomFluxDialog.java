package com.bytesbee.provpnapp.managers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytesbee.provpnapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public final class BottomFluxDialog {
    private static View view;
    private static BottomFluxDialog instance;
    private static BottomSheetDialog dialog;
    private static TextView leftButton;
    private static TextView rightButton;
    private static ImageView ivDialog;
    private static int imageDialog;
    private static TextView tvTitle;
    private static String textTitle;
    private static TextView tvMessage;
    private static String textMessage;
    private static EditText etInput;
    private static boolean isCancel = false;

    private static BottomFluxDialog getInstance() {
        if (instance == null) {
            instance = new BottomFluxDialog();
        }
        return instance;
    }

    public static BottomFluxDialog infoDialog(Context context) {
        view = ((Activity) context).getLayoutInflater().inflate(R.layout.bf_info_dialog, null);
        leftButton = view.findViewById(R.id.a_btn);
        ivDialog = view.findViewById(R.id.image);
        tvTitle = view.findViewById(R.id.title);
        tvMessage = view.findViewById(R.id.message);

        dialog = new BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme);

        leftButton.setOnClickListener(v -> dialog.dismiss());

        return getInstance();
    }

    public static BottomFluxDialog alertDialog(final Context context) {
        view = ((Activity) context).getLayoutInflater().inflate(R.layout.bf_info_dialog, null);
        leftButton = view.findViewById(R.id.a_btn);
        ivDialog = view.findViewById(R.id.image);
        tvTitle = view.findViewById(R.id.title);
        tvMessage = view.findViewById(R.id.message);

        dialog = new BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme);

        return getInstance();
    }

    public static BottomFluxDialog confirmDialog(final Context context) {
        view = ((Activity) context).getLayoutInflater().inflate(R.layout.bf_confirm_dialog, null);
        leftButton = view.findViewById(R.id.a_btn);
        rightButton = view.findViewById(R.id.b_btn);
        ivDialog = view.findViewById(R.id.image);
        tvTitle = view.findViewById(R.id.title);
        tvMessage = view.findViewById(R.id.message);

        dialog = new BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme);

        return getInstance();
    }

    public static BottomFluxDialog inputDialog(final Context context) {
        view = ((Activity) context).getLayoutInflater().inflate(R.layout.bf_input_dialog, null);
        leftButton = view.findViewById(R.id.a_btn);
        rightButton = view.findViewById(R.id.b_btn);
        ivDialog = view.findViewById(R.id.image);
        tvTitle = view.findViewById(R.id.title);
        tvMessage = view.findViewById(R.id.message);
        etInput = view.findViewById(R.id.input);

        dialog = new BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme);

        etInput.requestFocus();

        return getInstance();
    }

    public BottomFluxDialog setInfoButtonText(String textBtn) {
        if (textBtn != null) {
            leftButton.setText(textBtn);
        }
        return getInstance();
    }

    public BottomFluxDialog setAlertButtonText(String textBtn) {
        if (textBtn != null) {
            leftButton.setText(textBtn);
        }
        return getInstance();
    }

    public BottomFluxDialog setLeftButtonText(String textBtn) {
        if (textBtn != null) {
            leftButton.setText(textBtn);
        }
        return getInstance();
    }

    public BottomFluxDialog setRightButtonText(String textBtn) {
        if (textBtn != null && rightButton != null) {
            rightButton.setText(textBtn);
        }
        return getInstance();
    }


    public BottomFluxDialog setTextTitle(String textTitle) {
        tvTitle.setVisibility(View.GONE);
        if (textTitle != null) {
            tvTitle.setText(textTitle);
            tvTitle.setVisibility(View.VISIBLE);
        }
        return getInstance();
    }

    public BottomFluxDialog setTextMessage(String textMessage) {
        tvMessage.setVisibility(View.GONE);
        if (textMessage != null) {
            tvMessage.setText(textMessage);
            tvMessage.setVisibility(View.VISIBLE);
        }
        return getInstance();
    }

    public BottomFluxDialog setImageDialog(int image) {
        ivDialog.setVisibility(View.GONE);
        if (image != 0) {
            ivDialog.setImageResource(image);
            ivDialog.setVisibility(View.VISIBLE);
        }
        return getInstance();
    }

    public void setCancelable(boolean isCancelable) {
        isCancel = isCancelable;
    }

    public void show() {
        if (dialog != null) {
            dialog.setContentView(view);
            dialog.setCancelable(isCancel);
            dialog.show();
        }
    }

    public BottomFluxDialog setAlertListener(final OnAlertListener confirmListener) {
        leftButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (confirmListener != null) {
                confirmListener.onClick();
            }
        });
        return getInstance();
    }

    public BottomFluxDialog setConfirmListener(final OnConfirmListener confirmListener) {
        leftButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (confirmListener != null) {
                confirmListener.onLeftClick();
            }
        });
        if (rightButton != null) {
            rightButton.setOnClickListener(v -> {
                dialog.dismiss();
                if (confirmListener != null) {
                    confirmListener.onRightClick();
                }
            });
        }
        return getInstance();
    }

    public BottomFluxDialog setInputListener(final OnInputListener inputListener) {
        leftButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (inputListener != null) {
                inputListener.onCancelInput();
            }
        });
        if (rightButton != null) {
            rightButton.setOnClickListener(v -> {
                dialog.dismiss();
                if (inputListener != null) {
                    inputListener.onSubmitInput(etInput.getText().toString());
                }
            });
        }
        return getInstance();
    }

    public interface OnAlertListener {
        void onClick();
    }

    public interface OnConfirmListener {
        void onLeftClick();

        void onRightClick();
    }

    public interface OnInputListener {
        void onSubmitInput(String text);

        void onCancelInput();
    }

    public interface LeftRightConfirmListener {
        void onLeftClick();

        void onRightRight();
    }
}
