package com.bytesbee.provpnapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.models.DataInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * The type Data adapter.
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {
    /**
     * The Data list.
     */
    public List<DataInfo> dataList;
    /**
     * The Context.
     */
    Context context;

    /**
     * Instantiates a new Data adapter.
     *
     * @param context  the context
     * @param dataList the data list
     */
    public DataAdapter(Context context, List<DataInfo> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_speed_history_layout, parent, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        DataInfo di = dataList.get(position);
        SimpleDateFormat formatter = new SimpleDateFormat(context.getString(R.string.lblDateFormat), Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(di.getDate());
        holder.vDate.setText(formatter.format(calendar.getTime()));
        holder.vPing.setText(String.valueOf(di.getPing()));
        holder.vDownload.setText(String.valueOf(di.getDownload()));
        holder.vUpload.setText(String.valueOf(di.getUpload()));
        if (position % 2 == 0) {
            holder.card_view.setBackgroundColor(context.getResources().getColor(R.color.white_10));
        } else {
            holder.card_view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * The type Data view holder.
     */
    static class DataViewHolder extends RecyclerView.ViewHolder {
        /**
         * The V date.
         */
        TextView vDate;
        /**
         * The V wifi.
         */
        TextView vPing;
        /**
         * The V mobile.
         */
        TextView vDownload;
        /**
         * The V total.
         */
        TextView vUpload;
        /**
         * The Card view.
         */
        ConstraintLayout card_view;

        /**
         * Instantiates a new Data view holder.
         *
         * @param itemView the item view
         */
        public DataViewHolder(View itemView) {
            super(itemView);
            vDate = itemView.findViewById(R.id.txtDate);
            vPing = itemView.findViewById(R.id.txtPing);
            vDownload = itemView.findViewById(R.id.txtDownload);
            vUpload = itemView.findViewById(R.id.txtUpload);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
