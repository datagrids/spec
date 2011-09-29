package javax.datagrid.mapreduce;

import java.io.Serializable;

public class Filter {
  private final Class<? extends Serializable> payloadType;
  private final Class<? extends Serializable> keyType;

  public Filter() {
    this(Serializable.class, Serializable.class);
  }

  public Filter(Class<? extends Serializable> keyType, Class<? extends Serializable> payloadType) {
    this.payloadType = payloadType;
    this.keyType = keyType;
  }

  public <K extends Serializable, V extends Serializable> boolean acceptable(K key, V payload) {
    return (payloadType.isAssignableFrom(payload.getClass())) &&
           (keyType.isAssignableFrom(key.getClass())) &&
           accept(key, payload);
  }

  public <K extends Serializable, V extends Serializable> boolean accept(K key, V payload) {
    return true;
  }
}
