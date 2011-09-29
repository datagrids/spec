package javax.datagrid.transientdatagrid;

import javax.datagrid.DataGrid;
import javax.datagrid.DataGridEntry;
import javax.datagrid.impl.AbstractDataGrid;
import javax.datagrid.mapreduce.Filter;
import javax.datagrid.mapreduce.Mapper;
import javax.datagrid.mapreduce.Reducer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class TransientDataGrid extends AbstractDataGrid {
  Map<Serializable, DataGridEntry<Serializable>> data = new ConcurrentHashMap<Serializable, DataGridEntry<Serializable>>();
  /**
   * This field indicates how many entries can exist in the map before each access triggers
   * a scan for expired entries. Thus, if the "data grid" has more than this number of entries,
   * each read/write/take will try to reduce the size, which will negatively impact performance
   * by quite a bit.
   * <p/>
   * This is mitigated in this class only in that the TransientDataGrid is not a "real datagrid"
   * but only a sort of local cache that acts like a datagrid should.
   */
  private int ceilingBeforeScan = 1000;
  private long defaultExpiry = DataGrid.DEFAULT_EXPIRY;

  public int getCeilingBeforeScan() {
    return ceilingBeforeScan;
  }

  public void setCeilingBeforeScan(int ceilingBeforeScan) {
    this.ceilingBeforeScan = ceilingBeforeScan;
  }

  public long getDefaultExpiry() {
    return defaultExpiry;
  }

  public void setDefaultExpiry(long defaultExpiry) {
    this.defaultExpiry = defaultExpiry;
  }

  private void scanForExpiredEntries() {
    long now = System.currentTimeMillis();
    Iterator<Map.Entry<Serializable, DataGridEntry<Serializable>>> iterator = data.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Serializable, DataGridEntry<Serializable>> entry = iterator.next();
      if (entry.getValue().getExpirationTime() < now) {
        iterator.remove();
      }
    }
  }

  @Override
  public <K extends Serializable, V extends Serializable> Future<V> asyncWrite(K key, V value, long expiry) {
    if (data.size() > ceilingBeforeScan) {
      scanForExpiredEntries();
    }
    long expirationTime = expiry;
    if (expiry != DataGrid.FOREVER) {
      expirationTime = System.currentTimeMillis() + expiry;
    }
    DataGridEntry<Serializable> dataGridEntry = new DataGridEntry<Serializable>(value, expirationTime);
    data.put(key, dataGridEntry);
    return new TransientDataGridFuture<V>(value);
  }

  @Override
  public <K extends Serializable, V extends Serializable> Future<V> asyncRead(K key) {
    if (data.size() > ceilingBeforeScan) {
      scanForExpiredEntries();
    }

    DataGridEntry<Serializable> dataGridEntry = data.get(key);
    if (dataGridEntry != null && dataGridEntry.isExpired()) {
      data.remove(key);
      dataGridEntry = null;
    }
    V payload = (dataGridEntry == null ? null : (V) dataGridEntry.getPayload());
    return new TransientDataGridFuture<V>(payload);
  }

  @Override
  public <K extends Serializable, V extends Serializable> Future<V> asyncTake(K key) {
    if (data.size() > ceilingBeforeScan) {
      scanForExpiredEntries();
    }
    @SuppressWarnings({"unchecked"})
    DataGridEntry<Serializable> dataGridEntry = data.get(key);
    if (dataGridEntry != null) {
      data.remove(key);
      if (dataGridEntry.isExpired()) {
        dataGridEntry = null;
      }
    }
    V payload = (dataGridEntry == null ? null : (V) dataGridEntry.getPayload());
    return new TransientDataGridFuture<V>(payload);
  }

  @Override
  public <ResultType extends Serializable> ResultType map(Mapper<ResultType> mapper, Reducer<ResultType> reducer) {
    return map(mapper, reducer, ACCEPT_ALL_FILTER);
  }

  @Override
  public <ResultType extends Serializable> ResultType map(Mapper<ResultType> mapper, Reducer<ResultType> reducer,
                                                         Filter filter) {
    List<ResultType> results = new ArrayList<ResultType>();

    // in a real implementation, mapper.execute() would run for each node in the data grid.
    // for the transient grid, however, there's only one node to work with.
    mapper.setLocalGrid(this);

    results.add(mapper.execute());

    return reducer.reduce(results);
  }
}

