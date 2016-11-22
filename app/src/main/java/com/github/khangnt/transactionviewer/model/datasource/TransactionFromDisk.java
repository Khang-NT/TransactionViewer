package com.github.khangnt.transactionviewer.model.datasource;

import android.content.ContentResolver;
import android.net.Uri;
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
 */
public class TransactionFromDisk implements IDataSource<List<Transaction>> {
    private static final String TAG = "TransactionFromDisk";
    private ContentResolver contentResolver;
    private Uri sourceUri;

    public TransactionFromDisk(ContentResolver contentResolver, Uri sourceUri) {
        this.contentResolver = contentResolver;
        this.sourceUri = sourceUri;
    }

    public void setSourceUri(Uri sourceUri) {
        this.sourceUri = sourceUri;
    }

    @Override
    public List<Transaction> fetch() throws Exception {
        InputStream inputStream = Preconditions.checkNotNull(contentResolver.openInputStream(sourceUri));
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
        return result;
    }
}
