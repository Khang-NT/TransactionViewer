package com.github.khangnt.transactionviewer.model.datasource;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 */

public interface IDataSource<T> {
    T fetch() throws Exception;
}
