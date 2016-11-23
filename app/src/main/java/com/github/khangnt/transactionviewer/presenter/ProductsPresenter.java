package com.github.khangnt.transactionviewer.presenter;

import android.os.Handler;

import com.github.khangnt.transactionviewer.callbacks.ICallback;
import com.github.khangnt.transactionviewer.model.Transaction;
import com.github.khangnt.transactionviewer.model.TransactionsProcessor;
import com.github.khangnt.transactionviewer.view.ProductsView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 */
public class ProductsPresenter implements ProductsView.Presenter, ICallback<Map<String, List<Transaction>>> {

    private enum State {
        IDLE,
        LOADING,
        LOADED,
        ERROR
    }

    private WeakReference<ProductsView> viewWeakReference;
    private TransactionsProcessor transactionsProcessor;
    private Handler mainHandler;
    private State currentState;
    private String errorMessage;

    private List<String> skuList;
    private Map<String, List<Transaction>> transactionDetails;

    public ProductsPresenter(TransactionsProcessor transactionsProcessor, Handler mainHandler, ProductsView view) {
        this.transactionsProcessor = transactionsProcessor;
        this.mainHandler = mainHandler;
        this.currentState = State.IDLE;
        this.viewWeakReference = new WeakReference<>(view);
    }

    @Override
    public void setView(ProductsView view) {
        this.viewWeakReference = new WeakReference<>(view);
    }

    @Override
    public void onViewReady() {
        if (currentState == State.IDLE)
            callProcessor();
        displayCurrentStateIfNeeded();
    }

    @Override
    public void onRetry() {
        if (currentState == State.ERROR) {
            callProcessor();
        } /* else
            throw exception
        */
    }

    @Override
    public void cleanUp() {
        this.currentState = State.IDLE;
        this.skuList = null;
        this.transactionDetails = null;
    }

    private void displayCurrentStateIfNeeded() {
        final ProductsView productsView = viewWeakReference.get();
        if (productsView != null) {
            if (currentState == State.LOADING)
                productsView.displayLoadingState();
            else if (currentState == State.ERROR)
                productsView.displayErrorState(errorMessage);
            else if (currentState == State.LOADED)
                productsView.display(skuList, transactionDetails);
        }
    }

    private void callProcessor() {
        this.currentState = State.LOADING;
        transactionsProcessor.process(mainHandler, this);
    }

    @Override
    public void onComplete(Map<String, List<Transaction>> data) {
        this.currentState = State.LOADED;
        this.skuList = new ArrayList<>(data.keySet());
        this.transactionDetails = data;
        this.displayCurrentStateIfNeeded();
    }

    @Override
    public void onError(Exception ex) {
        this.currentState = State.ERROR;
        this.errorMessage = ex.getMessage();
        displayCurrentStateIfNeeded();
    }
}
