package de.snmusic.oxygen.plugin.dbtagger.prefs;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


@SuppressWarnings("serial")
public class PrefsTable extends JTable {

	private DefaultTableModel tableModel;
	private PrefsData preferences;

	public PrefsTable(PrefsData preferences) {
		this.preferences = preferences;
	}

	void addRow() {
		tableModel.addRow(preferences.getEmptyPreference());
		int lastRow = getRowCount() - 1;
		setRowSelectionInterval(lastRow, lastRow);
	}

	void deleteRow() {
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

	void duplicateRow() {
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
	 * Sets the table model and rendering.
	 * 
	 * @param prefsSets
	 *            The preferences data to be passed the table model.
	 */
	void init() {
		String[][] currentPreferences = preferences.getCurrentPreferences();

		String[][] prefsSets = Arrays.copyOf(currentPreferences,
				currentPreferences.length);
		// declare table model
		tableModel = new DefaultTableModel(prefsSets, PrefsData.PREFS_NAMES) {
			private static final long serialVersionUID = 1L;

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
		getColumnModel().getColumn(PrefsData.PASSWORD).setCellEditor(
				new DefaultCellEditor(passwordField));
		getColumnModel().getColumn(PrefsData.PASSWORD).setCellRenderer(
				getPasswordCellRenderer());

		// column widths
		getColumnModel().getColumn(PrefsData.USER).setMaxWidth(50);
		getColumnModel().getColumn(PrefsData.PASSWORD).setMaxWidth(50);
		getColumnModel().getColumn(PrefsData.SHORTCUT).setMaxWidth(120);
	}

	private DefaultTableCellRenderer getPasswordCellRenderer() {
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

	/**
	 * Returns the user preferences stored in the table model.
	 * 
	 * @return The table model contents.
	 */
	public String[][] getTableData() {
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

}