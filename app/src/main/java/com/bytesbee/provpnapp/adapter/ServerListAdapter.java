package com.bytesbee.provpnapp.adapter;

import static com.bytesbee.provpnapp.constants.IConstants.FALSE;
import static com.bytesbee.provpnapp.constants.IConstants.LOAD_MORE;
import static com.bytesbee.provpnapp.constants.IConstants.ZERO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.models.VPNServers;
import com.bytesbee.provpnapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ServerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<VPNServers> items;
    private final Context mContext;
    private static final int ITEM_VIEW_TYPE = ZERO;  // it represents items
    private int row_index = 0;
    private OnItemClickListener mOnItemClickListener;

    public ServerListAdapter(Context mContext, ArrayList<VPNServers> mItems, RecyclerView view) {
        this.items = mItems;
        this.mContext = mContext;
        final VPNServers mySer = SessionManager.get().getServer();
        if (mySer != null) {
            row_index = mySer.getId();
        }
        lastItemViewDetector(view);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_servers, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            VPNServers vpnServers = items.get(holder.getAdapterPosition());
            Utils.setServerImageView(mContext, vpnServers.flagURL, ((ViewHolder) holder).imgCountry);
            ((ViewHolder) holder).txtCountryName.setText(items.get(holder.getAdapterPosition()).getServerName());
            ((ViewHolder) holder).layout.setOnClickListener(view -> {
                row_index = vpnServers.getId();
                notifyDataSetChanged();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(items.get(position));
                }
            });

            if (row_index == vpnServers.getId()) {
                ((ViewHolder) holder).layout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_background));
                ((ViewHolder) holder).imgSelected.setVisibility(View.VISIBLE);
            } else {
                ((ViewHolder) holder).layout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_border));
                ((ViewHolder) holder).imgSelected.setVisibility(View.GONE);
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
        private final TextView txtCountryName;
        private final ImageView imgCountry;
        private final ImageView imgSelected;
        private final ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCountry = itemView.findViewById(R.id.imgCountry);
            imgSelected = itemView.findViewById(R.id.imgSelected);
            txtCountryName = itemView.findViewById(R.id.txtCountryName);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public void addItemsData(final List<VPNServers> items) {
        try {
            setLoaded();
            int positionStart = getItemCount();
            int itemCount = items.size();
            this.items.addAll(items);
            notifyItemRangeInserted(positionStart, itemCount);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public void setLoaded() {
        try {
            loading = FALSE;
            for (int i = 0; i < getItemCount(); i++) {
                if (items.get(i) == null) {
                    items.remove(i);
                    notifyItemRemoved(i);
                }
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public void setLoading() {
        try {
            if (getItemCount() != 0) {
                loading = true;
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearListData() {
        try {
            this.items = new ArrayList<>();
            notifyDataSetChanged();
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        try {
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int lastPos = layoutManager.findLastVisibleItemPosition();
                        if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                            int current_page = getItemCount() / LOAD_MORE;
                            onLoadMoreListener.onLoadMore(current_page);
                            loading = true;
                        }
                    }
                });
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(VPNServers obj);
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }
}

