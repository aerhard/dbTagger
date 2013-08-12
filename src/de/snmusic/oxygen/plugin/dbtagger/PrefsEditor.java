/*
 * Copyright 2013 Alexander Erhard

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package de.snmusic.oxygen.plugin.dbtagger;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Creates the user preferences editor dialog.
 */
public class PrefsEditor {

	private DefaultTableModel prefsTableModel;
	private JTable prefsTable = new JTable();
	private StandalonePluginWorkspace workspace;
	private TaggerPluginExtension pluginExtension;

	public PrefsEditor(StandalonePluginWorkspace workspace,
			TaggerPluginExtension pluginExtension) {
		this.pluginExtension = pluginExtension;
		pluginExtension.getPrefsStore();
		this.workspace = workspace;
	};

	public void show() {
		String dialogLabel = "dbTagger-Einstellungen";

		JPanel prefsPane = new JPanel();
		prefsPane.setLayout(new BoxLayout(prefsPane, BoxLayout.PAGE_AXIS));

		JScrollPane scrollPane = new JScrollPane(prefsTable);
		scrollPane.setPreferredSize(new Dimension(800, 400));
		prefsPane.add(scrollPane);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createLoweredBevelBorder());

		JButton newButton = new JButton("Neu");
		JButton deleteButton = new JButton("Löschen");
		JButton duplicateButton = new JButton("Duplizieren");
		JButton resetButton = new JButton("Zurücksetzen");
		Dimension d = resetButton.getMaximumSize();
		newButton.setMaximumSize(new Dimension(d));
		deleteButton.setMaximumSize(new Dimension(d));
		duplicateButton.setMaximumSize(new Dimension(d));

		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefsTableModel.addRow(pluginExtension.getPrefsStore().getEmptyPrefsSet());
				int lastRow = prefsTable.getRowCount() - 1;
				prefsTable.setRowSelectionInterval(lastRow, lastRow);
			}
		});
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = prefsTable.getSelectedRow();
				if (row != -1) {
					prefsTableModel.removeRow(row);
					int rowCount = prefsTableModel.getRowCount();
					if (rowCount > 0) {
						int newFocusRow = (row == rowCount) ? row - 1 : row;
						prefsTable.setRowSelectionInterval(newFocusRow,
								newFocusRow);
					}
				}
			}
		});
		duplicateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = prefsTable.getSelectedRow();
				if (row != -1) {
					int cols = prefsTableModel.getColumnCount();
					String[] newRow = new String[cols];
					for (int i = 0; i < cols; i++) {
						newRow[i] = (String) prefsTableModel.getValueAt(row, i);
					}
					prefsTableModel.addRow(newRow);
					int lastRow = prefsTable.getRowCount() - 1;
					prefsTable.setRowSelectionInterval(lastRow, lastRow);
				}
			}
		});
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initTable(pluginExtension.getPrefsStore().getDefaultPrefsSets());
			}
		});
		buttonPane.add(newButton);
		buttonPane.add(deleteButton);
		buttonPane.add(duplicateButton);
		buttonPane.add(resetButton);
		prefsPane.add(buttonPane);

		initTable(pluginExtension.getPrefsStore().getCurrentPrefsSets());

		int result = JOptionPane.showConfirmDialog(
				(java.awt.Frame) workspace.getParentFrame(), prefsPane,
				dialogLabel, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			pluginExtension.getPrefsStore().setCurrentPrefsSets(getTableData());
			pluginExtension.setMenuItems();
		}
	};

	/**
	 * Returns the user preferences stored in the table model.
	 * 
	 * @return The table model contents.
	 */
	private String[][] getTableData() {
		int rows = prefsTableModel.getRowCount();
		int cols = prefsTableModel.getColumnCount();
		String[][] tableData = new String[rows][cols];
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				tableData[i][j] = (String) prefsTableModel.getValueAt(i, j);
		return tableData;
	};

	/**
	 * Sets the table model and rendering.
	 * 
	 * @param currentPrefsSets
	 *            The preferences data to be passed the table model.
	 */
	private void initTable(String[][] currentPrefsSets) {
		// declare table model
		prefsTableModel = new DefaultTableModel(currentPrefsSets,
				this.pluginExtension.getPrefsStore().prefsNames) {
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
		prefsTable.setModel(prefsTableModel);

		// make sure the cell currently edited gets stored when the user clicks "OK"
		prefsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		prefsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (prefsTable.getRowCount() > 0) {
			prefsTable.setRowSelectionInterval(0, 0);
			prefsTable.requestFocus();
		}

		// password column
		JPasswordField passwordField = new JPasswordField();
		passwordField.setBorder(new LineBorder(Color.BLACK));
		prefsTable.getColumnModel().getColumn(PrefsStore.PASSWORD)
				.setCellEditor(new DefaultCellEditor(passwordField));
		prefsTable.getColumnModel().getColumn(PrefsStore.PASSWORD)
				.setCellRenderer(new DefaultTableCellRenderer() {
					private static final long serialVersionUID = 1L;

					@Override
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int col) {
						super.getTableCellRendererComponent(table, value,
								isSelected, hasFocus, row, col);
						String v = (String) value;
						if (v.length() == 0) {
							setText("");
						} else {
							setText("***");
						}
						return this;
					}
				});

		// column widths
		prefsTable.getColumnModel().getColumn(PrefsStore.USER)
				.setMaxWidth(50);
		prefsTable.getColumnModel().getColumn(PrefsStore.PASSWORD)
				.setMaxWidth(50);
		prefsTable.getColumnModel().getColumn(PrefsStore.SHORTCUT)
				.setMaxWidth(100);
	};

}
