package des.wangku.operate.standard.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class SWTVirtualTableDemo {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Virtual Table Demo");

        //table cell values
        String[] titles = { "Column1", "Column2", "Column3", "Column4" };
        int items = 20000;
        final String[][] cellValues = new String[items][titles.length];
        for (int i = 0; i < items; i++) {
            for (int j = 0; j < titles.length; j++) {
                cellValues[i][j] = "cell_" + (i + 1) + "_" + (j + 1);
            }
        }
        System.out.println("create data cost:"+(System.currentTimeMillis()-start));

        Table table = new Table(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
        table.setHeaderVisible(true);

        // set table title
        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NULL);
            column.setText(titles[i]);
            column.pack();
        }
        
        table.addListener(SWT.SetData, new Listener(){
            public void handleEvent(Event event) {
                TableItem item = (TableItem)event.item;
                int index = event.index;
                System.out.println("::"+index);
                item.setText(cellValues [index]);
            }
        });
        table.setItemCount(items);
        table.setBounds(10, 10, 280, 350);

        shell.pack();
        shell.open();
        long end = System.currentTimeMillis();
        System.out.println("All cost:" + (end - start));
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
