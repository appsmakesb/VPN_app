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
import com.bytesbee.provpnapp.models.ConnectionInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * The type Data adapter.
 */
public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.DataViewHolder> {
    /**
     * The Data list.
     */
    public List<ConnectionInfo> dataList;
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
    public ConnectionAdapter(Context context, List<ConnectionInfo> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_connection_history_layout, parent, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        ConnectionInfo cInfo = dataList.get(position);
        SimpleDateFormat formatter = new SimpleDateFormat(context.getString(R.string.lblDateFormat), Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(cInfo.getDate());
        holder.txtDate.setText(formatter.format(calendar.getTime()));
        holder.txtLocation.setText(String.valueOf(cInfo.getLocation()));
        holder.txtTimer.setText(String.valueOf(cInfo.getTimer()));
        holder.txtDownload.setText(String.valueOf(cInfo.getDownload()));
        holder.txtUpload.setText(String.valueOf(cInfo.getUpload()));
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
        TextView txtDate;
        TextView txtLocation;
        TextView txtTimer;
        TextView txtDownload;
        TextView txtUpload;
        ConstraintLayout card_view;

        /**
         * Instantiates a new Data view holder.
         *
         * @param itemView the item view
         */
        public DataViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtTimer = itemView.findViewById(R.id.txtTimer);
            txtDownload = itemView.findViewById(R.id.txtDownload);
            txtUpload = itemView.findViewById(R.id.txtUpload);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
