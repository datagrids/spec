package javax.datagrid.transientdatagrid;

import org.testng.annotations.Test;

import javax.datagrid.DataGrid;
import javax.datagrid.mapreduce.Mapper;
import javax.datagrid.mapreduce.Reducer;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class TransientDataGridTest {
  @Test
  public void instantiateDataGrid() {
    DataGrid dataGrid = new TransientDataGrid();
    assertNotNull(dataGrid);
  }

  @Test
  public void testReadWrite() {
    DataGrid dataGrid = new TransientDataGrid();
    String result = dataGrid.write("key", "value");
    assertEquals(result, "value");
    result = dataGrid.read("key");
    assertEquals(result, "value");
    result = dataGrid.take("key");
    assertEquals(result, "value");
    result = dataGrid.read("key");
    assertNull(result);
    result = dataGrid.read("newKey");
    assertNull(result);
  }

  @Test
  public void testExpirations() {
    DataGrid dataGrid = new TransientDataGrid();
    String result = dataGrid.write("key", "value", 100);
    assertEquals("value", result);
    result = dataGrid.read("key");
    assertEquals(result, "value");
    sleep(110);
    result = dataGrid.read("key");
    assertNull(result);

    result = dataGrid.write("key", "value", 100);
    assertEquals(result, "value");
    result = dataGrid.read("key");
    assertEquals(result, "value");
    sleep(110);
    result = dataGrid.take("key");
    assertNull(result);
  }

  @Test(expectedExceptions = ClassCastException.class)
  public void testCastingProblems() {
    DataGrid dataGrid = new TransientDataGrid();
    String result = dataGrid.write("key", "value");
    Integer r=dataGrid.read("key");
  }

  @Test
  public void testMapReduce() {
    DataGrid dataGrid = new TransientDataGrid();
    Integer result = dataGrid.map(new Mapper<Integer>() {
      @Override
      public Integer execute() {
        return 1;
      }
    },
                                 new Reducer<Integer>() {
                                   @Override
                                   public Integer reduce(Collection<Integer> mappedResults) {
                                     int sum = 0;
                                     for (Integer i : mappedResults) {
                                       sum += i;
                                     }
                                     return sum;
                                   }
                                 }
    );
    assertEquals(result.intValue(), 1);
  }

  private void sleep(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException ignored) {
    }
  }
}