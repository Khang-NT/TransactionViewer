package com.github.khangnt.transactionviewer.model.datasource;

/**
 * Created by Khang NT on 11/22/16.
 * Email: khang.neon.1997@gmail.com
 * <br><br>
 * <p>
 * This interface includes simple protocol {@link #fetch()} to fetch data from data source.
 * <br>
 * <br> The implementation could be GetSomeThingFromApi, **FromDataBase, **FromDisk, e.g.
 *
 * @param <T> The data type to be fetched.
 */
public interface IDataSource<T> {
    T fetch() throws Exception;
}
