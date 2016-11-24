package com.github.khangnt.transactionviewer.model;

import android.os.Handler;
import android.support.annotation.Nullable;

import com.github.khangnt.transactionviewer.callbacks.ICallback;
import com.github.khangnt.transactionviewer.model.datasource.IDataSource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 */

/**
 * A valid rate data set should have a way to convert from a currency to any currency,
 * it also means we can convert between all currencies in the data set. <br>
 * <br>
 * This class perform algorithm to calculate the rate of <b>all currency with GBP</b>.
 * <br> It requires <b>currency rate</b> data source ({@link IDataSource}), and an {@link Executor} to run on.
 * <br> It maybe take a while to finish, so it should be executed independent with main thread.
 */
public class CurrencyRateProcessor {
    private static final String GBP = "GBP";
    private IDataSource<List<CurrencyRate>> currenciesDataSource;
    private Executor executor;

    /**
     * Constructor of {@link CurrencyRateProcessor}.
     *
     * @param currenciesRateDataSource The  <b>currency rate</b> data source to fetch {@link CurrencyRate} data set.
     * @param executor                 The executor to execute task, should run asynchronously with main thread.
     */
    public CurrencyRateProcessor(IDataSource<List<CurrencyRate>> currenciesRateDataSource, Executor executor) {
        this.currenciesDataSource = currenciesRateDataSource;
        this.executor = executor;
    }

    /**
     * Run on given {@link Executor} to calculate the <font color="yellow"><b>rate of all currency with GBP</b></font>.
     *
     * @param handler  Nullable, the handler use to dispatch the callback data. If null,
     *                 trigger callback on thread the executor provided.
     * @param callback The callback.
     */
    public void process(@Nullable final Handler handler, final ICallback<Map<String, Float>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // use doubly linked list for best performance to remove random item
                    final LinkedList<CurrencyRate> rateDataSet = new LinkedList<>(currenciesDataSource.fetch());
                    final Map<String, Float> rateWithGbp = new HashMap<>();
                    // GBP / GBP == 1
                    rateWithGbp.put(GBP, 1f);
                    // consider N = number of currency type, no matter how many item in rateDataSet
                    // With the worst situation (d -> c, c -> b, b -> a, GBP -> a)
                    // it runs loop up to (N^2 + N) / 2 times.
                    // But N seems too small, so O(N^2) can be acceptable.
                    // TODO: 11/23/16 Khang-NT: improve algorithm
                    while (rateDataSet.size() > 0) {
                        boolean flag = false;
                        for (int i = 0; i < rateDataSet.size(); ) {
                            CurrencyRate currencyRate = rateDataSet.get(i);
                            if (rateWithGbp.containsKey(currencyRate.getFrom())) {
                                if (!rateWithGbp.containsKey(currencyRate.getTo()))     // avoid duplicate evaluating
                                    rateWithGbp.put(currencyRate.getTo(),
                                            rateWithGbp.get(currencyRate.getFrom()) * currencyRate.getRate());
                                rateDataSet.remove(i);
                                flag = true;
                            } else if (rateWithGbp.containsKey(currencyRate.getTo())) {
                                rateWithGbp.put(currencyRate.getFrom(),
                                        rateWithGbp.get(currencyRate.getTo()) / currencyRate.getRate());
                                rateDataSet.remove(i);
                                flag = true;
                            } else {
                                i++;
                            }
                        }
                        if (!flag)
                            throw new Exception("Invalid rate data set: can't not calculate the rate of "
                                    + rateDataSet.get(0).getFrom() + " with " + GBP);
                    }
                    if (handler != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onComplete(rateWithGbp);
                            }
                        });
                    } else {
                        callback.onComplete(rateWithGbp);
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
