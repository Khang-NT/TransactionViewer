package com.github.khangnt.transactionviewer;

import android.content.res.AssetManager;

import com.github.khangnt.transactionviewer.model.CurrencyRate;
import com.github.khangnt.transactionviewer.model.Transaction;
import com.github.khangnt.transactionviewer.model.datasource.CurrencyRateFromAsset;
import com.github.khangnt.transactionviewer.model.datasource.IDataSource;
import com.github.khangnt.transactionviewer.model.datasource.TransactionFromAssets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Khang NT on 11/24/16.
 * Email: khang.neon.1997@gmail.com
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DataFromAssetSmokeTest {
    private static final String TAG = "DataFromAssetSmokeTest";

    AssetManager assetManager;

    @Before
    public void setUp() throws Exception {
        assetManager = RuntimeEnvironment.application.getAssets();
    }

    @Test
    public void testGetListCurrencyRate_ShouldBeSuccessful() throws Exception {
        IDataSource<List<CurrencyRate>> currencyRateDataSource = new CurrencyRateFromAsset("rates.json", assetManager);
        assertThat(currencyRateDataSource.fetch()).isNotEmpty();
    }

    @Test
    public void testGetListTransaction_ShouldBeSuccessful() throws Exception {
        IDataSource<List<Transaction>> transactionsDataSource = new TransactionFromAssets("transactions.json", assetManager);
        assertThat(transactionsDataSource.fetch()).isNotEmpty();
    }
}
