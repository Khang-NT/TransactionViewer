package com.github.khangnt.transactionviewer;

import com.github.khangnt.transactionviewer.callbacks.ICallback;
import com.github.khangnt.transactionviewer.model.CurrencyRate;
import com.github.khangnt.transactionviewer.model.CurrencyRateProcessor;
import com.github.khangnt.transactionviewer.model.datasource.CurrencyRateFromAsset;
import com.github.khangnt.transactionviewer.model.datasource.IDataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Khang NT on 11/24/16.
 * Email: khang.neon.1997@gmail.com
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CurrencyRateProcessorSmokeTest {
    static final CurrencyRate[] testCase1 = new CurrencyRate[]{
            CurrencyRate.of("GBP", "A", 1),
            CurrencyRate.of("GBP", "B", 1),
            CurrencyRate.of("C", "D", 1)
    };

    static final CurrencyRate[] testCase2 = new CurrencyRate[]{
            CurrencyRate.of("GBP", "A", 1),
            CurrencyRate.of("GBP", "B", 1),
            CurrencyRate.of("C", "D", 1),
            CurrencyRate.of("D", "E", 1),
            CurrencyRate.of("A", "E", 2),
    };

    @Mock
    ICallback<Map<String, Float>> callback;
    @Captor
    ArgumentCaptor<Map<String, Float>> rateWithGbpCaptor;
    @Captor
    ArgumentCaptor<Exception> exceptionCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessInvalidCurrencyRateData_shouldThrowException() throws Exception {
        IDataSource<List<CurrencyRate>> fakeData = provideFakeDataSource(testCase1);
        CurrencyRateProcessor currencyRateProcessor = new CurrencyRateProcessor(fakeData, new SynchronizedExecutor());
        currencyRateProcessor.process(null, callback);
        verify(callback, times(0)).onComplete(rateWithGbpCaptor.capture());
        verify(callback).onError(any(Exception.class));
    }

    @Test
    public void testProcessValidCurrencyRateData_shouldSuccess() throws Exception {
        IDataSource<List<CurrencyRate>> fakeData = provideFakeDataSource(testCase2);
        CurrencyRateProcessor currencyRateProcessor = new CurrencyRateProcessor(fakeData, new SynchronizedExecutor());
        currencyRateProcessor.process(null, callback);
        verify(callback).onComplete(rateWithGbpCaptor.capture());
        // GBP / A = 1
        // A / E = 2
        // E / GBP = 0.5
        assertThat(rateWithGbpCaptor.getValue().get("E")).isEqualTo(0.5f);
    }

    @Test
    public void testProcessCurrencyRateWithRealAssetFile_shouldSuccess() throws Exception {
        IDataSource<List<CurrencyRate>> currencyRateDataSource = new CurrencyRateFromAsset("rates.json",
                RuntimeEnvironment.application.getAssets());
        CurrencyRateProcessor currencyRateProcessor = new CurrencyRateProcessor(currencyRateDataSource, new SynchronizedExecutor());
        currencyRateProcessor.process(null, callback);
        verify(callback, times(1)).onComplete(anyMap());
        verify(callback, times(0)).onError(any(Exception.class));
    }

    private IDataSource<List<CurrencyRate>> provideFakeDataSource(final CurrencyRate[] testCase) {
        return new IDataSource<List<CurrencyRate>>() {
            @Override
            public List<CurrencyRate> fetch() throws Exception {
                return Arrays.asList(testCase);
            }
        };
    }
}
