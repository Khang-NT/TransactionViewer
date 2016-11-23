package com.github.khangnt.transactionviewer.callbacks;


import java.lang.ref.WeakReference;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 */
public class WeakReferenceCallback<T> implements ICallback<T> {

    private WeakReference<ICallback<T>> callbackWeakReference;

    public WeakReferenceCallback(ICallback<T> originCallback) {
        this.callbackWeakReference = new WeakReference<>(originCallback);
    }

    @Override
    public void onComplete(final T data) {
        final ICallback<T> originCallback = callbackWeakReference.get();
        if (originCallback != null)
            originCallback.onComplete(data);
    }

    @Override
    public void onError(final Exception ex) {
        final ICallback<T> originCallback = callbackWeakReference.get();
        if (originCallback != null)
            originCallback.onError(ex);
    }
}
