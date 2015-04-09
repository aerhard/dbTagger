package com.aerhard.oxygen.plugin.dbtagger.ui;

import com.aerhard.oxygen.plugin.dbtagger.TableData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Table extends JTable {

    /** The maximum width of the first row. */
    private static final int FIRST_ROW_MAX_WIDTH = 100;

    public Table () {
        setDefaultFocusTraversal();
    }

    /**
     * Sets default focus traversal rules: tab and shift tab should select new siblings rather than other parts of the table.
     */
    private void setDefaultFocusTraversal() {
        Set<AWTKeyStroke> forward = new HashSet<AWTKeyStroke>(
                getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                forward);
        Set<AWTKeyStroke> backward = new HashSet<AWTKeyStroke>(
                getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
                backward);
    }

    /**
     * Gets the data of the currently selected row.
     *
     * @return the row data
     */
    public String[] getSelectedRowData() {
        int row = getSelectedRow();
        int colCount = getModel().getColumnCount();
        if (row != -1 && colCount != 0) {
            String[] result = new String[colCount];
            for (int i = 0; i < colCount; i++) {
                result[i] = getModel().getValueAt(row, i)
                        .toString();
            }
            return result;
        }
        return null;
    }

    public void initTableModel(TableData result) {
        String[][] data = result.getBody();
        String[] titles = result.getHeaders();

        DefaultTableModel tableModel = new DefaultTableModel(data, titles) {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };
        setModel(tableModel);

        // key column width
        getColumnModel().getColumn(0)
                .setMaxWidth(FIRST_ROW_MAX_WIDTH);
    }

}
