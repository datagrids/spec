package javax.datagrid;

import javax.datagrid.mapreduce.Filter;
import javax.datagrid.mapreduce.Mapper;
import javax.datagrid.mapreduce.Reducer;
import java.io.Serializable;
import java.util.concurrent.Future;

public interface DataGrid {
  public final static long FOREVER=-1;
  public final static long DEFAULT_EXPIRY = FOREVER;

  // general operations
  <K extends Serializable, V extends Serializable> V write(K key, V value);

  <K extends Serializable, V extends Serializable> V write(K key, V value, long expiry);

  <K extends Serializable, V extends Serializable> V read(K key);

  <K extends Serializable, V extends Serializable> V take(K key);

  // async analogues to general operations
  <K extends Serializable, V extends Serializable> Future<V> asyncWrite(K key, V value);

  <K extends Serializable, V extends Serializable> Future<V> asyncWrite(K key, V value, long expiry);

  <K extends Serializable, V extends Serializable> Future<V> asyncRead(K key);

  <K extends Serializable, V extends Serializable> Future<V> asyncTake(K key);

  // distributed processing
  <ResultType extends Serializable> ResultType map(Mapper<ResultType> mapper,
                                                  Reducer<ResultType> reducer);
  <ResultType extends Serializable> ResultType map(Mapper<ResultType> mapper,
                                                  Reducer<ResultType> reducer,
                                                  Filter filter);
}