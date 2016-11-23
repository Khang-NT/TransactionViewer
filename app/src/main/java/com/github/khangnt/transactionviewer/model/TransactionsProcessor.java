package com.github.khangnt.transactionviewer.model;

/**
 * Created by Khang NT on 11/23/16.
 * Email: khang.neon.1997@gmail.com
 */

import android.os.Handler;
import android.support.annotation.Nullable;

import com.github.khangnt.transactionviewer.callbacks.ICallback;
import com.github.khangnt.transactionviewer.model.datasource.IDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * This class fetch transaction list from data source and merge all transactions have same SKU to one group.
 */
public class TransactionsProcessor {
    private static final String TAG = "TransactionsProcessor";

    private Executor executor;
    private IDataSource<List<Transaction>> transactionsDataSource;

    public TransactionsProcessor(Executor executor, IDataSource<List<Transaction>> transactionsDataSource) {
        this.executor = executor;
        this.transactionsDataSource = transactionsDataSource;
    }

    /**
     * Fetch transaction list from data source and merge all transactions have same SKU to one group.
     *
     * @param handler  Nullable, the handler use to dispatch the callback data. If null,
     *                 trigger callback on thread the executor provided.
     * @param callback The callback.
     */
    public void process(@Nullable final Handler handler, final ICallback<Map<String, List<Transaction>>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Map<String, List<Transaction>> result = new HashMap<>();  // group transaction list by SKU
                    final List<Transaction> raw = transactionsDataSource.fetch();
                    for (Transaction transaction : raw) {
                        List<Transaction> transactionList = result.get(transaction.getSku());
                        if (transactionList != null) {
                            transactionList.add(transaction);
                        } else {
                            transactionList = new ArrayList<>();
                            transactionList.add(transaction);
                            result.put(transaction.getSku(), transactionList);
                        }
                    }
                    if (handler != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onComplete(result);
                            }
                        });
                    } else {
                        callback.onComplete(result);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    if (handler != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    } else {
                        callback.onError(e);
                    }
                }
            }
        });
    }
}
