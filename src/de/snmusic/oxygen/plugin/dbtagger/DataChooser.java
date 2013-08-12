/**
 * Copyright 2013 Alexander Erhard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.snmusic.oxygen.plugin.dbtagger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class DataChooser extends OKCancelDialog {

	private static final long serialVersionUID = 1L;
	private String[] prefsSet;
	private StandalonePluginWorkspace workspace;
	private DefaultTableModel tableModel;
	private String[] columnNames = { "Key", "Text" };
	private JTable searchResultsTable = new JTable();
	private final JTextField searchField = new JTextField();
	private static final String solve = "Solve";
	DataLoader dataLoader;

	/**
	 * Data chooser dialog.
	 * 
	 * @param workspaceParam
	 * @param selection
	 *            The current selection in the editor document.
	 * @param prefsSet
	 *            The preferences string array used to configure the http request.
	 * @throws Exception
	 */
	public DataChooser(StandalonePluginWorkspace workspaceParam,
			String selection, String[] prefsSet) throws Exception {

		super((Frame) workspaceParam.getParentFrame(),
				prefsSet[PrefsStore.NAME], true);

		this.workspace = workspaceParam;
		this.prefsSet = prefsSet;
		this.dataLoader = new DataLoader(workspace); 

		// Search field in north pane
		JPanel searchPane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 5, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		searchPane.add(new JLabel("Suchbegriffe:"), c);
		c.insets = new Insets(0, 0, 5, 0);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		searchField.setText(selection);
		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = searchField.getText();
				try {
					loadTableModelData(text);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				tableModel.fireTableDataChanged();
			}
		});
		searchPane.add(searchField, c);
		getContentPane().add(searchPane, BorderLayout.NORTH);

		// Search results table in center panel
		JScrollPane scrollPane = new JScrollPane(searchResultsTable);
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		searchResultsTable.getInputMap(
				JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
		searchResultsTable.getActionMap().put(solve, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				doOK();
			}
		});
		searchResultsTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					doOK();
				}
			}
		});
		scrollPane.setPreferredSize(new Dimension(600, 300));
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		pack();
		setResizable(true);

		loadTableModelData(selection);
	}

	/**
	 * Sets the table model and rendering.
	 * 
	 * @param searchString The search results data to be passed the table model.
	 * @throws Exception 
	 */
	private void loadTableModelData(String searchString) throws Exception {
		
		String[][] searchResults = this.dataLoader.getArray(prefsSet, searchString);
		if (searchResults == null)
			throw new Exception("");
		
		tableModel = new DefaultTableModel(searchResults, columnNames) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			};

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
		searchResultsTable.setModel(tableModel);

		searchResultsTable
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (searchResultsTable.getRowCount() > 0) {
			searchResultsTable.setRowSelectionInterval(0, 0);
			searchResultsTable.requestFocus();
		}
		// key column width
		searchResultsTable.getColumnModel().getColumn(0).setMaxWidth(50);
	};

	private String[] getRowData() {
		int row = searchResultsTable.getSelectedRow();
		if (row != -1) {
			return new String[] {
					searchResultsTable.getModel().getValueAt(row, 0).toString(), // key
					searchResultsTable.getModel().getValueAt(row, 1).toString() // text
							.replaceAll("\\s+", " ") };
		}
		return null;
	};

	public String[] showDialog() {
		setVisible(true);
		return (getResult() == RESULT_OK) ? getRowData() : null;
	}

}