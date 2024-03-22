package com.bytesbee.provpnapp.adapter;

import static com.bytesbee.provpnapp.constants.IConstants.ZERO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.models.ProductModel;
import com.bytesbee.provpnapp.utils.Utils;

import java.util.List;

public class SubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ProductModel> items;
    private final Context mContext;
    private static final int ITEM_VIEW_TYPE = ZERO;  // it represents items
    private int row_index = -1;
    private OnItemClickListener mOnItemClickListener;
    private final Drawable withBG;
    private final Drawable withoutBG;
    private final int white;
    private final int black;

    public SubListAdapter(Context mContext, List<ProductModel> mItems) {
        this.items = mItems;
        this.mContext = mContext;
        withBG = ContextCompat.getDrawable(mContext, R.drawable.rounded_border_background);
        withoutBG = ContextCompat.getDrawable(mContext, R.drawable.rounded_corners);
        white = mContext.getResources().getColor(R.color.colorText);
        black = mContext.getResources().getColor(R.color.colorTitleText);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subscription_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            ProductModel pModel = items.get(position);
            ((ViewHolder) holder).txtPrice.setText(pModel.getFormattedPrice());
            ((ViewHolder) holder).txtDuration.setText(Utils.getDuration(mContext, pModel.getBillingPeriod()));

            ((ViewHolder) holder).layout.setOnClickListener(view -> {
                row_index = pModel.getId();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(items.get(position));
                }
                notifyDataSetChanged();
            });

            if (row_index == pModel.getId()) {
                ((ViewHolder) holder).layout.setBackground(withBG);
                ((ViewHolder) holder).txtPrice.setTextColor(white);
                ((ViewHolder) holder).txtDuration.setTextColor(white);
            } else {
                ((ViewHolder) holder).layout.setBackground(withoutBG);
                ((ViewHolder) holder).txtPrice.setTextColor(black);
                ((ViewHolder) holder).txtDuration.setTextColor(black);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_VIEW_TYPE;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layout;

        private final TextView txtPrice;
        private final TextView txtDuration;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDuration = itemView.findViewById(R.id.txtDuration);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ProductModel obj);
    }

}

