package com.github.khangnt.transactionviewer.model.datasource;

import android.content.ContentResolver;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.annotation.WorkerThread;
import android.util.JsonReader;
import android.util.Log;

import com.github.khangnt.transactionviewer.model.CurrencyRate;
import com.github.khangnt.transactionviewer.utils.Preconditions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 * <br><br>
 *
 * An implementation of {@link IDataSource} to fetch json data in local file
 * and parse it into List of {@link CurrencyRate}.
 */
public class CurrencyRateFromAsset implements IDataSource<List<CurrencyRate>> {
    private static final String TAG = "CurrencyRateFromAsset";

    private String path;
    private AssetManager assetManager;

    public CurrencyRateFromAsset(Uri sourceUri, ContentResolver contentResolver) {
        this.sourceUri = sourceUri;
        this.contentResolver = contentResolver;
    }

    public void setSourceUri(Uri sourceUri) {
        this.sourceUri = sourceUri;
    }

    @WorkerThread
    @Override
    public List<CurrencyRate> fetch() throws Exception {
        InputStream inputStream = Preconditions.checkNotNull(contentResolver.openInputStream(sourceUri));
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        List<CurrencyRate> result = new ArrayList<>();
        // start parsing json
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            CurrencyRate.Builder builder = CurrencyRate.builder();
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("from".equals(name))
                    builder.from(jsonReader.nextString());
                else if ("to".equals(name))
                    builder.to(jsonReader.nextName());
                else if ("rate".equals(name))
                    builder.rate((float) jsonReader.nextDouble());
            }
            jsonReader.endObject();
            result.add(builder.build());
        }
        jsonReader.endArray();
        jsonReader.close();
        Log.d(TAG, "fetch() returned: " + result);
        return result;
    }


}
