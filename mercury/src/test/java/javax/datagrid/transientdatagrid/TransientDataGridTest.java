package javax.datagrid.transientdatagrid;

import org.testng.annotations.Test;

import javax.datagrid.DataGrid;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class TransientDataGridTest {
  @Test
  public void instantiateDataGrid() {
    DataGrid dataGrid = new TransientDataGrid();
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

  private void sleep(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException ignored) {
    }
  }
}