package javax.datagrid.impl;

import javax.datagrid.DataGrid;
import javax.datagrid.DataGridException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractDataGrid implements DataGrid {
  @SuppressWarnings({"unchecked"})
  @Override
  public <K extends Serializable, V extends Serializable> V read(K key) {
    try {
      return (V) asyncRead(key).get();
    } catch (InterruptedException e) {
      throw new DataGridException(e);
    } catch (ExecutionException e) {
      throw new DataGridException(e);
    }
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public <K extends Serializable, V extends Serializable> V take(K key) {
    try {
      return (V) asyncTake(key).get();
    } catch (InterruptedException e) {
      throw new DataGridException(e);
    } catch (ExecutionException e) {
      throw new DataGridException(e);
    }
  }

  @Override
  public <K extends Serializable, V extends Serializable> V write(K key, V value) {
    return write(key, value, DataGrid.DEFAULT_EXPIRY);
  }

  @Override
  public <K extends Serializable, V extends Serializable> V write(K key, V value, long expiry) {
    try {
      return asyncWrite(key, value, expiry).get();
    } catch (InterruptedException e) {
      throw new DataGridException(e);
    } catch (ExecutionException e) {
      throw new DataGridException(e);
    }
  }

  @Override
  public <K extends Serializable, V extends Serializable> Future<V> asyncWrite(K key, V value) {
    return asyncWrite(key, value, DataGrid.DEFAULT_EXPIRY);
  }
}
