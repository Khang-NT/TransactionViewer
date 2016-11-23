package com.github.khangnt.transactionviewer.model.datasource;

import android.content.res.AssetManager;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import android.util.JsonReader;
import android.util.Log;

import com.github.khangnt.transactionviewer.model.Transaction;
import com.github.khangnt.transactionviewer.utils.Preconditions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 * <br><BR>
 * <p>
 * An implementation of {@link IDataSource} to fetch json data from file asset
 * and parse it into List of {@link Transaction}.
 */
public class TransactionFromAssets implements IDataSource<List<Transaction>> {
    private static final String TAG = "TransactionFromAssets";

    private AssetManager assetManager;
    private String path;

    public TransactionFromAssets(String path, AssetManager assetManager) {
        this.assetManager = assetManager;
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @WorkerThread
    @Override
    public List<Transaction> fetch() throws Exception {
        long start = SystemClock.currentThreadTimeMillis();
        InputStream inputStream = Preconditions.checkNotNull(assetManager.open(path));
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        List<Transaction> result = new ArrayList<>();
        // start parsing list transactions from json
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            Transaction.Builder builder = Transaction.builder();
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("amount".equals(name))
                    builder.amount((float) jsonReader.nextDouble());
                else if ("sku".equals(name))
                    builder.sku(jsonReader.nextString());
                else if ("currency".equals(name))
                    builder.currency(jsonReader.nextString());
            }
            jsonReader.endObject();
            result.add(builder.build());
        }
        jsonReader.endArray();
        jsonReader.close();
        Log.d(TAG, "fetch() returned: " + result);
        Log.d(TAG, "fetch() finish on " + (SystemClock.currentThreadTimeMillis() - start) + " milliseconds");
        return result;
    }
}
