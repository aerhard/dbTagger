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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import de.snmusic.oxygen.plugin.dbtagger.data.HttpUtil;
import de.snmusic.oxygen.plugin.dbtagger.data.JsonUtil;
import de.snmusic.oxygen.plugin.dbtagger.data.TableData;
import de.snmusic.oxygen.plugin.dbtagger.prefs.PrefsData;
import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class QueryWindow extends OKCancelDialog {

	private static final int FIRST_ROW_MAX_WIDTH = 50;
	private static final long serialVersionUID = 1L;
	private String[] prefsSet;
	private DefaultTableModel tableModel;
	private JTable searchResultsTable = new JTable();
	private final JTextField searchField = new JTextField();
	private static final String ENTER = "enter";
	private JsonUtil jsonUtil;
	private HttpUtil httpUtil;
	/**
	 * Data chooser dialog.
	 * 
	 * @param workspaceParam
	 * @param selection
	 *            The current selection in the editor document.
	 * @param prefsSet
	 *            The preferences string array used to configure the http
	 *            request.
	 * @throws Exception
	 */
	public QueryWindow(StandalonePluginWorkspace workspace, String selection,
			String[] prefsSet) {

		super((Frame) workspace.getParentFrame(), prefsSet[PrefsData.NAME],
				true);

		this.jsonUtil = new JsonUtil(workspace);
		this.httpUtil = new HttpUtil(workspace);
		this.prefsSet = Arrays.copyOf(prefsSet, prefsSet.length);

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
				loadTableModelData(text);
				tableModel.fireTableDataChanged();
			}
		});
		searchField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				selectSearchFieldText(true);
			}

			@Override
			public void focusLost(FocusEvent e) {
				selectSearchFieldText(false);
			}
		});
		searchPane.add(searchField, c);
		getContentPane().add(searchPane, BorderLayout.NORTH);

		// Search results table in center panel
		JScrollPane scrollPane = new JScrollPane(searchResultsTable);
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		searchResultsTable.getInputMap(
				JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, ENTER);
		searchResultsTable.getActionMap().put(ENTER, new AbstractAction() {
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

	private void selectSearchFieldText(boolean focus) {
		if (focus) {
			searchField.select(0, searchField.getText().length());
		} else {
			searchField.select(0, 0);
		}
	}

	/**
	 * Sets the table model and rendering.
	 * 
	 * @param searchString
	 *            The search results data to be passed the table model.
	 * @throws Exception
	 */
	private void loadTableModelData(String searchString) {

		TableData result = null;
		
		String response = httpUtil.get(prefsSet[PrefsData.USER],
				prefsSet[PrefsData.PASSWORD], prefsSet[PrefsData.URL],
				searchString);
		
		if (response != null) {
			result = this.jsonUtil.transform(response);
		}
		
		if (result == null) {
			if (tableModel != null) {
				tableModel.setRowCount(0);
			}
		} else {

			String[][] data = result.getData();
			String[] titles = result.getColumnTitles();

			tableModel = new DefaultTableModel(data, titles) {
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
			selectSearchFieldText(true);
			// key column width
			searchResultsTable.getColumnModel().getColumn(0)
					.setMaxWidth(FIRST_ROW_MAX_WIDTH);
		}

	};

	private String[] getRowData() {
		int row = searchResultsTable.getSelectedRow();
		int colCount = searchResultsTable.getModel().getColumnCount();
		if (row != -1 && colCount != 0) {
			String[] result = new String[colCount];
			for (int i = 0; i < colCount; i++) {
				result[i] = searchResultsTable.getModel().getValueAt(row, i)
						.toString();
			}
			return result;
		}
		return null;
	};

	public String[] showDialog() {
		setVisible(true);
		return (getResult() == RESULT_OK) ? getRowData() : null;
	}

}