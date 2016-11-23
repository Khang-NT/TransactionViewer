package com.github.khangnt.transactionviewer.view;

import com.github.khangnt.transactionviewer.model.Transaction;

import java.util.List;
import java.util.Map;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 */
public interface ProductsView {
    void displayLoadingState();
    void displayErrorState(String error);
    void displayMessage(String message);
    void display(List<String> skuList, Map<String, List<Transaction>> transactionDetails);

    interface Presenter {
        void setView(ProductsView view);    //
        void onViewReady();                 // trigger when view ready to retrieve data
        void onRetry();                     // retry on Error
        void cleanUp();
    }
}
