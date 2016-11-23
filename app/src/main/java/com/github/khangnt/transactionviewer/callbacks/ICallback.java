package com.github.khangnt.transactionviewer.callbacks;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 */
public interface ICallback<T> {
    void onComplete(T data);

    void onError(Exception ex);
}
