package javax.datagrid.transientdatagrid;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This is a "Future" that is a placeholder for TransientDataGrid. It wraps a
 * payload with the Future interface, and does absolutely nothing else that a
 * "normal" Future would.
 */
public class TransientDataGridFuture<V extends Serializable> implements Future<V>{
  private final V payload;

  public TransientDataGridFuture(V payload) {
    this.payload = payload;
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public boolean isDone() {
    return true;
  }

  @Override
  public V get() throws InterruptedException, ExecutionException {
    return payload;
  }

  @Override
  public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return get();
  }
}
