package javax.datagrid;

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

}