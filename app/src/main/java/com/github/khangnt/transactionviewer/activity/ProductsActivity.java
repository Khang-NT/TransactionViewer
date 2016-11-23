package com.github.khangnt.transactionviewer.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.github.khangnt.transactionviewer.R;
import com.github.khangnt.transactionviewer.fragment.ProductsFragment;
import com.github.khangnt.transactionviewer.model.Transaction;

import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    public static final String PRODUCT_TRANS_FRAGMENT = "product_trans_fragment";
    ProductsFragment productsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        if (savedInstanceState != null) {
            productsFragment = (ProductsFragment) getSupportFragmentManager().getFragment(savedInstanceState,
                    PRODUCT_TRANS_FRAGMENT);
        }

        if (productsFragment == null) {
            productsFragment = new ProductsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, productsFragment)
                    .commit();
        }
    }



    public void showTransactionDetail(String sku, List<Transaction> transactions) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, PRODUCT_TRANS_FRAGMENT, productsFragment);
    }
}
