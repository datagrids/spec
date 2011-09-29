package javax.datagrid.mapreduce;

import javax.datagrid.DataGrid;
import java.io.Serializable;

public abstract class Mapper<ResultType extends Serializable> {
  private DataGrid localGrid;

  public DataGrid getLocalGrid() {
    return localGrid;
  }

  public void setLocalGrid(DataGrid localGrid) {
    this.localGrid = localGrid;
  }

  public abstract ResultType execute();
}
