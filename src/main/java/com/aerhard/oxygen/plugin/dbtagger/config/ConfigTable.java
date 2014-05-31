package com.aerhard.oxygen.plugin.dbtagger.config;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.DefaultCellEditor;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * The table component of the config dialog, see {@link ConfigDialog}.
 */
public class ConfigTable extends JTable {

    private static final long serialVersionUID = 1L;

    private static final int NARROW_COL_WIDTH = 50;

    /** The table model. */
    private DefaultTableModel tableModel;

    /** The config store. */
    private ConfigStore configStore;

    /** The localization resource bundle. */
    private ResourceBundle i18n;

    /**
     * Instantiates a new config editor table.
     * 
     * @param configStore
     *            the config store
     */
    public ConfigTable(ConfigStore configStore) {
        this.configStore = configStore;
        i18n = ResourceBundle.getBundle("Tagger");
    }

    /**
     * Gets the table data.
     * 
     * @return The table data from the table model.
     */
    public String[][] getData() {
        int rows = tableModel.getRowCount();
        int cols = tableModel.getColumnCount();
        String[][] tableData = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tableData[i][j] = (String) tableModel.getValueAt(i, j);
            }
        }
        return tableData;
    };

    /**
     * Reset the table data to the values currently stored in the config object.
     */
    public void setData() {
        String[][] data = configStore.getAll();
        String[][] configItems = Arrays.copyOf(data, data.length);

        tableModel = createTableModel(configItems);
        setModel(tableModel);
        // make sure the currently edited cell gets stored when the user clicks
        // "OK"
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (getRowCount() > 0) {
            setRowSelectionInterval(0, 0);
            requestFocus();
        }

        // password column
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(new LineBorder(Color.BLACK));
        getColumnModel().getColumn(ConfigStore.ITEM_PASSWORD).setCellEditor(
                new DefaultCellEditor(passwordField));
        getColumnModel().getColumn(ConfigStore.ITEM_PASSWORD).setCellRenderer(
                createPasswordCellRenderer());

        // column widths
        getColumnModel().getColumn(ConfigStore.ITEM_USER).setPreferredWidth(
                NARROW_COL_WIDTH);
        getColumnModel().getColumn(ConfigStore.ITEM_PASSWORD).setPreferredWidth(
                NARROW_COL_WIDTH);
        getColumnModel().getColumn(ConfigStore.ITEM_SHORTCUT).setPreferredWidth(
                NARROW_COL_WIDTH);
    }

    /**
     * Creates the table model.
     * 
     * @param configItems
     *            the config items
     * @return the default table model
     */
    private DefaultTableModel createTableModel(String[][] configItems) {
        String[] tableHeaders = i18n.getString("configTable.tableHeaders")
                .split(",");
        return new DefaultTableModel(configItems, tableHeaders) {

            private static final long serialVersionUID = 1L;

            @Override
            public Class<?> getColumnClass(int column) {
                Class<?> returnValue;
                if ((column >= 0) && (column < getColumnCount())) {
                    returnValue = getValueAt(0, column).getClass();
                } else {
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };
    }

    /**
     * Adds a new empty row to the table. The new row will be added to the end
     * of the table.
     */
    public void addRow() {
        tableModel.addRow(ConfigStore.createEmptyConfigItem());
        int lastRow = getRowCount() - 1;
        setRowSelectionInterval(lastRow, lastRow);
    }

    /**
     * Deletes the current row.
     */
    public void deleteRow() {
        int row = getSelectedRow();
        if (row != -1) {
            tableModel.removeRow(row);
            int rowCount = tableModel.getRowCount();
            if (rowCount > 0) {
                int newFocusRow = (row == rowCount) ? row - 1 : row;
                setRowSelectionInterval(newFocusRow, newFocusRow);
            }
        }
    }

    /**
     * Duplicates the current row. The new row will be added to the end of the
     * table.
     */
    public void duplicateRow() {
        int row = getSelectedRow();
        if (row != -1) {
            int cols = tableModel.getColumnCount();
            String[] newRow = new String[cols];
            for (int i = 0; i < cols; i++) {
                newRow[i] = (String) tableModel.getValueAt(row, i);
            }
            tableModel.addRow(newRow);
            int lastRow = getRowCount() - 1;
            setRowSelectionInterval(lastRow, lastRow);
        }
    };

    /**
     * Creates the renderer for the password cells in the table.
     * 
     * @return the password cell renderer
     */
    private DefaultTableCellRenderer createPasswordCellRenderer() {
        return new DefaultTableCellRenderer() {

            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, col);
                String v = (String) value;
                if (v.length() == 0) {
                    setText("");
                } else {
                    setText("***");
                }
                return this;
            }
        };
    };

}