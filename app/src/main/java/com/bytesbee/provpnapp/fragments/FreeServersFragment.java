package com.bytesbee.provpnapp.fragments;

import static com.bytesbee.provpnapp.constants.IConstants.CLICK_DELAY_TIME;
import static com.bytesbee.provpnapp.constants.IConstants.CONNECTED;
import static com.bytesbee.provpnapp.constants.IConstants.DEFAULT_PAGE_NO;
import static com.bytesbee.provpnapp.constants.IConstants.FALSE;
import static com.bytesbee.provpnapp.constants.IConstants.ONE;
import static com.bytesbee.provpnapp.constants.IConstants.SUCCESS;
import static com.bytesbee.provpnapp.constants.IConstants.TRUE;
import static com.bytesbee.provpnapp.constants.IConstants.TYPE_FREE;
import static com.bytesbee.provpnapp.constants.IConstants.TYPE_PAID;
import static com.bytesbee.provpnapp.constants.IConstants.ZERO;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.adapter.ServerListAdapter;
import com.bytesbee.provpnapp.managers.ScreenManager;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.models.CallbackServers;
import com.bytesbee.provpnapp.models.VPNServers;
import com.bytesbee.provpnapp.rests.ApiInterface;
import com.bytesbee.provpnapp.rests.RestAdapter;
import com.bytesbee.provpnapp.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreeServersFragment extends BaseFragment {
    private View root_view;
    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ServerListAdapter serverListAdapter;
    private Call<CallbackServers> callbackCall = null;
    private int failed_page = ZERO;
    private int post_total = ZERO;
    public int isPaid = TYPE_FREE;
    private ServerClickListener serverClickListener;

    public FreeServersFragment() {
    }

    public FreeServersFragment newInstance(int isPaid) {
        FreeServersFragment f = new FreeServersFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CONNECTED, isPaid);
        f.setArguments(bundle);
        return f;
    }

    public void setServerClickListener(ServerClickListener serverClickListener) {
        this.serverClickListener = serverClickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_free_servers, container, false);
        try {
            if (getArguments() != null)
                isPaid = getArguments().getInt(CONNECTED, TYPE_FREE);
        } catch (Exception e) {
            Utils.getErrors(e);
        }

        init();
        initData();
        return root_view;
    }

    public void init() {
        swipeRefreshLayout = root_view.findViewById(R.id.swipe_refresh_layout_home);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.green, R.color.blue, R.color.orange);
        refreshRecyclerView();
    }

    public void initData() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
                requestAction(DEFAULT_PAGE_NO);
            } catch (Exception e) {
                Utils.getErrors(e);
            }
        });
        requestAction(DEFAULT_PAGE_NO);
    }

    private void refreshRecyclerView() {
        recyclerView = root_view.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView.setHasFixedSize(TRUE);

        //set data and list adapter
        serverListAdapter = new ServerListAdapter(getActivity(), new ArrayList<>(), recyclerView);
        recyclerView.setAdapter(serverListAdapter);

        // on item list clicked
        serverListAdapter.setOnItemClickListener(obj -> {
            if (isPaid == TYPE_PAID && SessionManager.get().isFreeUser()) {
                ScreenManager.showUpgradeActivity(mActivity);
            } else {
                Utils.sout("OnItemClick::: " + obj.getServerName());
                SessionManager.get().saveServer(obj);
                serverClickListener.onServerClick();
            }
        });

        // detect when scroll reach bottom
        serverListAdapter.setOnLoadMoreListener(current_page -> {
            try {
                if (post_total > serverListAdapter.getItemCount() && current_page != ZERO) {
                    int next_page = current_page + 1;
                    showProgress();
                    requestAction(next_page);
                } else {
                    serverListAdapter.setLoaded();
                }
            } catch (Exception e) {
                Utils.getErrors(e);
            }
        });
    }

    private void requestAction(final int page_no) {
        try {
            if (page_no == DEFAULT_PAGE_NO) {
                refreshRecyclerView();
            }
            showFailedView(FALSE, "");
            showNoItemView(FALSE);
            if (page_no == DEFAULT_PAGE_NO) {
                swipeProgress(TRUE);
            } else {
                serverListAdapter.setLoading();
            }
            new Handler().postDelayed(() -> callAPIRequest(page_no), ONE);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void callAPIRequest(final int page_no) {
        try {
            ApiInterface apiInterface = RestAdapter.createAPI(mActivity);
            VPNServers vpnServers = new VPNServers();
            vpnServers.setPage(page_no);
            vpnServers.setApi_key(Utils.getBase64Encoded(Utils.getAPIKey(mActivity)));
            vpnServers.setIsPaid(isPaid);
            Utils.sout("vpnServer json:: " + new Gson().toJson(vpnServers));

            callbackCall = apiInterface.getServers(vpnServers);
            callbackCall.enqueue(new Callback<CallbackServers>() {
                @Override
                public void onResponse(@NonNull Call<CallbackServers> call, @NonNull Response<CallbackServers> response) {
                    CallbackServers resp = response.body();
                    if (resp != null && resp.status == SUCCESS) {
                        post_total = resp.count_total;
                        showAPIResult(resp.posts, page_no);
                    } else {
                        onFailRequest(page_no);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CallbackServers> call, @NonNull Throwable t) {
                    if (!call.isCanceled()) onFailRequest(page_no);
                }
            });
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void showAPIResult(final List<VPNServers> vpnServers, int page_no) {
        try {
            if (page_no == DEFAULT_PAGE_NO) {
                serverListAdapter.clearListData();
            }
            if (page_no > DEFAULT_PAGE_NO) {
                hideProgress();
            }
            serverListAdapter.addItemsData(vpnServers);
            swipeProgress(FALSE);
            if (vpnServers.size() == ZERO) {
                if (page_no == DEFAULT_PAGE_NO) {
                    serverListAdapter.clearListData();
                }
                showNoItemView(TRUE);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void onFailRequest(int page_no) {
        try {
            if (page_no > DEFAULT_PAGE_NO) {
                hideProgress();
            }
            failed_page = page_no;
            serverListAdapter.setLoaded();
            swipeProgress(FALSE);
            if (Utils.isOnline(mActivity)) {
                showNoItemView(TRUE);
            } else {
                showFailedView(TRUE, getString(R.string.lblNoInternet));
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void showFailedView(boolean show, String message) {
        try {
            View lyt_failed = root_view.findViewById(R.id.lyt_failed_home);
            ((TextView) root_view.findViewById(R.id.failedMessage)).setText(message);

            if (show) {
                recyclerView.setVisibility(View.GONE);
                lyt_failed.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                lyt_failed.setVisibility(View.GONE);
            }

            (root_view.findViewById(R.id.layoutNoConnection)).setOnClickListener(view -> new Handler().postDelayed(() -> {
                if (failed_page == DEFAULT_PAGE_NO) {
                    serverListAdapter.clearListData();
                }
                requestAction(failed_page);
            }, CLICK_DELAY_TIME));
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void showNoItemView(boolean show) {
        try {
            View lyt_no_item = root_view.findViewById(R.id.lyt_no_item_home);
            ((TextView) root_view.findViewById(R.id.noItemMessage)).setText(R.string.lblNoPostFound);

            if (show) {
                recyclerView.setVisibility(View.GONE);
                lyt_no_item.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                lyt_no_item.setVisibility(View.GONE);
            }

            (root_view.findViewById(R.id.layoutNoItemFound)).setOnClickListener(view -> new Handler().postDelayed(() -> {
                if (failed_page == DEFAULT_PAGE_NO) {
                    serverListAdapter.clearListData();
                }
                requestAction(failed_page);
            }, CLICK_DELAY_TIME));
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void swipeProgress(final boolean show) {
        try {
            if (!show) {
                swipeRefreshLayout.setRefreshing(FALSE);
                return;
            }
            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(TRUE));
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            swipeProgress(FALSE);
            if (callbackCall != null && callbackCall.isExecuted()) {
                callbackCall.cancel();
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public interface ServerClickListener {
        void onServerClick();
    }
}