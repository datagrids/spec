package javax.datagrid;

import java.io.Serializable;

public class DataGridEntry<V extends Serializable> implements Serializable {
  V payload;
  private long expirationTime;

  public DataGridEntry(V value, long expirationTime) {
    payload = value;
    setExpirationTime(expirationTime);
  }

  public long getExpirationTime() {
    return expirationTime;
  }

  public void setExpirationTime(long expirationTime) {
    this.expirationTime = expirationTime;
  }

  public V getPayload() {
    return payload;
  }

  public void setPayload(V payload) {
    this.payload = payload;
  }

  public boolean isExpired() {
    return getExpirationTime() != DataGrid.FOREVER &&
           getExpirationTime() < System.currentTimeMillis();
  }
}
