package javax.datagrid.transientdatagrid;

import javax.datagrid.DataGrid;
import javax.datagrid.DataGridEntry;
import javax.datagrid.impl.AbstractDataGrid;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class TransientDataGrid extends AbstractDataGrid {
  Map<Serializable, DataGridEntry> data = new ConcurrentHashMap<Serializable, DataGridEntry>();
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

  @SuppressWarnings({"UnusedDeclaration"})
  public int getCeilingBeforeScan() {
    return ceilingBeforeScan;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void setCeilingBeforeScan(int ceilingBeforeScan) {
    this.ceilingBeforeScan = ceilingBeforeScan;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public long getDefaultExpiry() {
    return defaultExpiry;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void setDefaultExpiry(long defaultExpiry) {
    this.defaultExpiry = defaultExpiry;
  }

  private void scanForExpiredEntries() {
    long now = System.currentTimeMillis();
    Iterator<Map.Entry<Serializable, DataGridEntry>> iterator = data.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Serializable, DataGridEntry> entry = iterator.next();
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
    long expirationTime=expiry;
    if(expiry!=DataGrid.FOREVER) {
      expirationTime=System.currentTimeMillis()+expiry;
    }
    DataGridEntry dataGridEntry = new DataGridEntry<V>(value, expirationTime);
    data.put(key, dataGridEntry);
    return new TransientDataGridFuture<V>(value);
  }

  @Override
  public <K extends Serializable, V extends Serializable> Future<V> asyncRead(K key) {
    if (data.size() > ceilingBeforeScan) {
      scanForExpiredEntries();
    }
    @SuppressWarnings({"unchecked"})
    DataGridEntry<V> dataGridEntry = data.get(key);
    if (dataGridEntry!=null && dataGridEntry.isExpired()) {
      data.remove(key);
      dataGridEntry = null;
    }
    V payload = (dataGridEntry == null ? null : dataGridEntry.getPayload());
    return new TransientDataGridFuture<V>(payload);
  }

  @Override
  public <K extends Serializable, V extends Serializable> Future<V> asyncTake(K key) {
    if (data.size() > ceilingBeforeScan) {
      scanForExpiredEntries();
    }
    @SuppressWarnings({"unchecked"})
    DataGridEntry<V> dataGridEntry = data.get(key);
    if (dataGridEntry != null) {
      data.remove(key);
      if (dataGridEntry.isExpired()) {
        dataGridEntry = null;
      }
    }

    V payload = (dataGridEntry == null ? null : dataGridEntry.getPayload());
    return new TransientDataGridFuture<V>(payload);
  }
}

