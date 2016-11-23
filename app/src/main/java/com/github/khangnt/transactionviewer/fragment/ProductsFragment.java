package com.github.khangnt.transactionviewer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.khangnt.transactionviewer.R;
import com.github.khangnt.transactionviewer.activity.ProductsActivity;
import com.github.khangnt.transactionviewer.model.Transaction;
import com.github.khangnt.transactionviewer.model.TransactionsProcessor;
import com.github.khangnt.transactionviewer.model.datasource.TransactionFromAssets;
import com.github.khangnt.transactionviewer.presenter.ProductsPresenter;
import com.github.khangnt.transactionviewer.view.ProductsView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static android.R.id.message;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 */
public class ProductsFragment extends Fragment implements ProductsView {

    private List<String> skuList;
    private Map<String, List<Transaction>> productTransactionDetails;
    private Presenter presenter;

    private RecyclerView recyclerView;
    private View emptyState, loadingState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_sku_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new SimpleAdapter(LayoutInflater.from(view.getContext())));

        emptyState = view.findViewById(R.id.tv_empty_state);
        loadingState = view.findViewById(R.id.pb_loading);

        if (presenter == null) {
            TransactionFromAssets transactionsDataSource = new TransactionFromAssets("transactions.json",
                    view.getContext().getAssets());
            TransactionsProcessor transactionsProcessor = new TransactionsProcessor(
                    Executors.newSingleThreadExecutor(), transactionsDataSource);
            presenter = new ProductsPresenter(transactionsProcessor, new Handler(), this);
        }

        presenter.onViewReady();
    }

    @Override
    public void displayLoadingState() {
        if (isAdded()) {
            if (loadingState != null)
                loadingState.setVisibility(VISIBLE);
            if (emptyState != null)
                emptyState.setVisibility(GONE);
        }
    }

    @Override
    public void displayErrorState(String error) {
        if (isAdded()) {
            final View view = getView();
            if (view != null) {
                Snackbar.make(view, error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.onRetry();
                    }
                }).show();
            }
            if (loadingState != null)
                loadingState.setVisibility(GONE);
            if (emptyState != null)
                emptyState.setVisibility(GONE);
        }
    }

    @Override
    public void displayMessage(String message) {
        if (getActivity() != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void display(List<String> skuList, Map<String, List<Transaction>> transactionDetails) {
        this.skuList = skuList;
        this.productTransactionDetails = transactionDetails;
        if (isAdded()) {
            if (recyclerView != null && recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            if (emptyState != null)
                emptyState.setVisibility(skuList.size() == 0 ? VISIBLE : GONE);

            if (loadingState != null)
                loadingState.setVisibility(GONE);
        }
    }

    private void displayTransactionDetail(String sku) {
        if (getActivity() instanceof ProductsActivity) {    // null check
            ProductsActivity productsActivity = ((ProductsActivity) getActivity());
            productsActivity.showTransactionDetail(sku, productTransactionDetails.get(sku));
        }
    }

    private class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvSku, tvTransCount;
        private String sku;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvSku = (TextView) itemView.findViewById(R.id.tv_sku);
            tvTransCount = (TextView) itemView.findViewById(R.id.tv_trans_count);
        }

        public void bind(String sku, int transCount) {
            tvSku.setText(this.sku = sku);
            tvTransCount.setText(String.valueOf(transCount));
        }

        @Override
        public void onClick(View view) {
            displayTransactionDetail(sku);
        }
    }

    private class SimpleAdapter extends RecyclerView.Adapter<SimpleViewHolder> {

        private LayoutInflater layoutInflater;

        private SimpleAdapter(LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SimpleViewHolder(layoutInflater.inflate(R.layout.sku_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            String sku = skuList.get(position);
            holder.bind(sku, productTransactionDetails.get(sku).size());
        }

        @Override
        public int getItemCount() {
            return skuList == null ? 0 : skuList.size();
        }
    }
}
