package com.github.khangnt.transactionviewer.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.github.khangnt.transactionviewer.R;
import com.github.khangnt.transactionviewer.fragment.ProductsFragment;
import com.github.khangnt.transactionviewer.fragment.TransactionDetailsFragment;
import com.github.khangnt.transactionviewer.model.Transaction;

import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    public static final String PRODUCT_TRANS_FRAGMENT = "product_trans_fragment";
    public static final String TRANS_DETAIL_FRAGMENT = "trans_detail_fragment";
    public static final String TITLE = "title";
    ProductsFragment productsFragment;
    TransactionDetailsFragment transDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        if (savedInstanceState != null) {
            productsFragment = (ProductsFragment) getSupportFragmentManager().getFragment(savedInstanceState,
                    PRODUCT_TRANS_FRAGMENT);
            transDetailFragment = (TransactionDetailsFragment) getSupportFragmentManager().getFragment(savedInstanceState,
                    TRANS_DETAIL_FRAGMENT);

            setTitle(savedInstanceState.getString(TITLE));
        }

        if (productsFragment == null) {
            productsFragment = new ProductsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, productsFragment)
                    .commit();
        }
    }



    public void showTransactionDetail(String sku, List<Transaction> transactions) {
        setTitle(getString(R.string.transactions_for, sku));
        if (transDetailFragment == null)
            transDetailFragment = new TransactionDetailsFragment();
        transDetailFragment.setTransactions(transactions);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, transDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            setTitle(getString(R.string.products_activity_label));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, PRODUCT_TRANS_FRAGMENT, productsFragment);
        if (transDetailFragment != null && transDetailFragment.isAdded())
            getSupportFragmentManager().putFragment(outState, TRANS_DETAIL_FRAGMENT, transDetailFragment);
        outState.putString(TITLE, getTitle().toString());
    }
}
