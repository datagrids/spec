package javax.datagrid.mapreduce;

import java.io.Serializable;

public interface Mapper<ResultType extends Serializable> {
  <T extends Serializable> ResultType execute(T t);
}
