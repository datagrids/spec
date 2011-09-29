package javax.datagrid.mapreduce;

import java.io.Serializable;
import java.util.Collection;

public interface Reducer<ResultType extends Serializable> {
  ResultType reduce(Collection<ResultType> mappedResults);
}
