package des.wangku.operate.standard.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class SWTTableDemo {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("SWT Table Demo");

        // table cell values
        String[] titles = { "Column1", "Column2", "Column3", "Column4" };
        int items = 20000;
        String[][] cellValues = new String[items][titles.length];
        for (int i = 0; i < items; i++) {
            for (int j = 0; j < titles.length; j++) {
                cellValues[i][j] = "cell_" + (i + 1) + "_" + (j + 1);
            }
        }
        System.out.println("Create data cost:"+ (System.currentTimeMillis() - start));

        Table table = new Table(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setHeaderVisible(true);

        // set table title
        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NULL);
            column.setText(titles[i]);
            column.pack();
        }

        for (int loopIndex = 0; loopIndex < items; loopIndex++) {
            TableItem item = new TableItem(table, SWT.NULL);
            item.setText(cellValues[loopIndex]);
        }

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
