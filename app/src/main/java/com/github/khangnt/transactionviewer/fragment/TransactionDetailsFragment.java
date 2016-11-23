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

import com.github.khangnt.transactionviewer.R;
import com.github.khangnt.transactionviewer.callbacks.ICallback;
import com.github.khangnt.transactionviewer.model.CurrencyRate;
import com.github.khangnt.transactionviewer.model.CurrencyRateProcessor;
import com.github.khangnt.transactionviewer.model.Transaction;
import com.github.khangnt.transactionviewer.model.datasource.CurrencyRateFromAsset;
import com.github.khangnt.transactionviewer.model.datasource.IDataSource;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created by Khang NT on 11/23/16.
 * Email: khang.neon.1997@gmail.com
 */
public class TransactionDetailsFragment extends Fragment implements ICallback<Map<String, Float>> {

    private static final Map<String, Character> sCurrencySymbol;

    static {
        sCurrencySymbol = new HashMap<>();
        sCurrencySymbol.put("GBP", '£');
        sCurrencySymbol.put("AUD", '$');
        sCurrencySymbol.put("CNY", '¥');
        sCurrencySymbol.put("EUR", '€');
        sCurrencySymbol.put("CAD", '$');
        sCurrencySymbol.put("USD", '$');
    }

    private TextView total;
    private RecyclerView recyclerView;
    private List<Transaction> transactions;
    private Map<String, Float> rateWithGbp;
    private CurrencyRateProcessor currencyRateProcessor;
    private Handler handler;
    private boolean errorEncountered = false;
    private DecimalFormat decimalFormat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trans_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_trans_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        decimalFormat = new DecimalFormat("#,###.00");
        recyclerView.setAdapter(new SimpleAdapter(LayoutInflater.from(view.getContext()),
                decimalFormat));

        total = (TextView) view.findViewById(R.id.tv_total);

        if (currencyRateProcessor == null) {
            IDataSource<List<CurrencyRate>> currencyDataSource = new CurrencyRateFromAsset("rates.json", view.getContext().getAssets());
            currencyRateProcessor = new CurrencyRateProcessor(currencyDataSource, Executors.newSingleThreadExecutor());
            handler = new Handler();
            currencyRateProcessor.process(handler, this);
        }

        if (errorEncountered) promptRetryLoadCurrencyRate();

        // reflect data to UI if available.
        onDataChanged();
    }

    private void onDataChanged() {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        if (rateWithGbp != null && transactions != null && total != null) {
            float totalAmount = 0;
            for (Transaction trans : transactions) {
                totalAmount += trans.getAmount() / rateWithGbp.get(trans.getCurrency());
            }
            total.setText(getString(R.string.total_amount_format,
                    sCurrencySymbol.get("GBP"), decimalFormat.format(totalAmount)));
        }
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        onDataChanged();
    }

    @Override
    public void onComplete(Map<String, Float> data) {
        this.errorEncountered = false;
        this.rateWithGbp = data;
        onDataChanged();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        this.errorEncountered = true;
        promptRetryLoadCurrencyRate();
    }

    private void promptRetryLoadCurrencyRate() {
        final View view = getView();
        if (view != null) {
            Snackbar.make(view, R.string.load_currency_rate_error_message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currencyRateProcessor.process(handler, TransactionDetailsFragment.this);
                        }
                    })
                    .show();
        }
    }

    private class SimpleViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAmount, tvAmountInGbp;
        private String sku;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            tvAmount = (TextView) itemView.findViewById(R.id.tv_amount);
            tvAmountInGbp = (TextView) itemView.findViewById(R.id.tv_amount_in_gbp);
        }

        public void bind(String amount, String amountInGbp) {
            tvAmount.setText(amount);
            tvAmountInGbp.setText(amountInGbp);
        }
    }

    private class SimpleAdapter extends RecyclerView.Adapter<SimpleViewHolder> {

        private LayoutInflater layoutInflater;
        private DecimalFormat decimalFormat;

        private SimpleAdapter(LayoutInflater layoutInflater, DecimalFormat decimalFormat) {
            this.layoutInflater = layoutInflater;
            this.decimalFormat = decimalFormat;
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SimpleViewHolder(layoutInflater.inflate(R.layout.trans_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);
            final Character gbpSym = sCurrencySymbol.get("GBP"),
                    transCurrencySym = sCurrencySymbol.containsKey(transaction.getCurrency()) ?
                            sCurrencySymbol.get(transaction.getCurrency()) : '$';
            float amount = transaction.getAmount();
            float amountInGbp = amount / rateWithGbp.get(transaction.getCurrency());
            holder.bind(getString(R.string.currency_format, transCurrencySym, decimalFormat.format(amount)),
                    getString(R.string.currency_format, gbpSym, decimalFormat.format(amountInGbp)));
        }

        @Override
        public int getItemCount() {
            return rateWithGbp == null || transactions == null ? 0 : transactions.size();
        }
    }
}
