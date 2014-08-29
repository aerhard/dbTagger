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

package com.aerhard.oxygen.plugin.dbtagger;

import java.awt.BorderLayout;
import java.awt.Component;
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
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.aerhard.oxygen.plugin.dbtagger.util.HttpUtil;
import com.aerhard.oxygen.plugin.dbtagger.util.JsonUtil;
import com.jidesoft.swing.InfiniteProgressPanel;

import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;
import ro.sync.exml.workspace.api.Workspace;

/**
 * The search dialog component.
 */
@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class SearchDialog extends OKCancelDialog {

    private static final long serialVersionUID = 1L;

    /** The preferred height of the config window. */
    private static final int PREFERRED_WIDTH = 800;

    /** The preferred heigth of the config window. */
    private static final int PREFERRED_HEIGHT = 400;

    /** The maximum width of the first row. */
    private static final int FIRST_ROW_MAX_WIDTH = 50;

    /** The default border width of the content items */
    private static final int BORDER_W = 5;

    /** The search action key "search" */
    private static final String SEARCH = "search";

    /** The search field component. */
    private JTextField searchField;

    /** The search results table component. */
    private JTable searchResultsTable;

    /** The model of the search results table. */
    private DefaultTableModel tableModel;

    /** The json utility. */
    private JsonUtil jsonUtil;

    /** The http utility. */
    private HttpUtil httpUtil;

    /** The localization resource bundle. */
    private ResourceBundle i18n;

    /** Indicates if valid data has been received from the server. */
    private boolean validData;

    /** The loading mask. */
    private InfiniteProgressPanel loadingMask;

    /** The fixed part of the server request URL. */
    private String url;

    /** The server request user name. */
    private String user;

    /** The server request password. */
    private String password;

    /**
     * Checks if valid data has been received from the server.
     * 
     * @return {Boolean}
     */
    public boolean hasValidData() {
        return validData;
    }

    /**
     * Instantiates a new query window.
     * 
     * @param workspace
     *            oXygen's workspace object.
     */
    public SearchDialog(Workspace workspace) {
        super((Frame) workspace.getParentFrame(), null, true);
        jsonUtil = new JsonUtil(workspace);
        httpUtil = new HttpUtil(workspace);
        i18n = ResourceBundle.getBundle("Tagger");

        loadingMask = new InfiniteProgressPanel();
        setGlassPane(loadingMask);

        getContentPane().add(createSearchFieldPane(), BorderLayout.NORTH);
        getContentPane().add(createSearchResultsPane(), BorderLayout.CENTER);
        initSearchFieldListeners();
        initTableListeners();

        pack();
        setResizable(true);

        setLocationRelativeTo((Component) workspace.getParentFrame());
    }

    /**
     * Configures the search dialog with a new set of basic parameters. This
     * method is called each time the user opens the search dialog.
     * 
     * @param title
     *            The dialog title
     * @param user
     *            The database user
     * @param password
     *            The database password
     * @param url
     *            The URL.
     * @param searchFieldText
     *            The text to put in the search field.
     */
    public void setConfig(String title, String user, String password,
            String url, String searchFieldText) {
        setTitle(title);
        searchField.setText(searchFieldText);
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Creates the search field pane and adds the provided text to the contained
     * text field.
     * 
     * @param text
     *            The text to add.
     * @return The search field pane.
     */
    private JPanel createSearchFieldPane() {
        JPanel searchFieldPane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, BORDER_W, BORDER_W);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        searchFieldPane
                .add(new JLabel(i18n.getString("searchDialog.searchTerms")
                        + ":"), c);
        c.insets = new Insets(0, 0, BORDER_W, 0);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        searchField = new JTextField();
        searchFieldPane.add(searchField, c);
        return searchFieldPane;
    }

    /**
     * Creates the pane containing the search results table and its components.
     * 
     * @return The search results pane component.
     */
    private JScrollPane createSearchResultsPane() {
        searchResultsTable = new JTable();
        JScrollPane searchResultsPane = new JScrollPane(searchResultsTable);
        searchResultsPane.setPreferredSize(new Dimension(PREFERRED_WIDTH,
                PREFERRED_HEIGHT));
        return searchResultsPane;
    }

    /** Initializes the search field action listeners. */
    private void initSearchFieldListeners() {
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                invokeLoadData(searchField.getText());
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
    }

    /** Initializes the search results table action listeners. */
    private void initTableListeners() {
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        searchResultsTable.getInputMap(
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter,
                SEARCH);
        searchResultsTable.getActionMap().put(SEARCH, new AbstractAction() {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                doOK();
            }
        });
        searchResultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    doOK();
                }
            }
        });
    }

    /**
     * Selects the search field text.
     * 
     * @param select
     *            specifies if the content of the text field should be selected.
     */
    private void selectSearchFieldText(boolean select) {
        if (select) {
            searchField.select(0, searchField.getText().length());
        } else {
            searchField.select(0, 0);
        }
    }

    /**
     * Calls {@link #loadData(String)} in a new thread
     * 
     * @param searchString
     *            The search string.
     */
    private void invokeLoadData(final String searchString) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadingMask.start();
                searchField.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadData(searchString, false);
                        loadingMask.stop();
                        searchField.setEnabled(true);
                        searchField.requestFocus();
                        selectSearchFieldText(true);
                        tableModel.fireTableDataChanged();
                    }
                }).start();
            }
        });
    }

    /**
     * Initiates a server request with the provided search string; if data is
     * returned, calls {@link #initTableModel(TableData)}.
     * 
     * @param searchString
     *            The search string.
     * @param isFirst
     *            Indicates if this is the first search in the search dialog.
     *            This information can be used in a server script to process the
     *            initial search (based on the selected text in the editor pane)
     *            in a distinct manner.
     */
    public void loadData(String searchString, Boolean isFirst) {
        TableData result = null;
        String response = httpUtil.get(user, password, url, searchString,
                isFirst);
        if (response != null) {
            result = jsonUtil.getTableData(response);
        }
        if (result != null) {
            validData = true;
            initTableModel(result);
            return;
        }
        validData = false;
    }

    void initTableModel(TableData result) {
        String[][] data = result.getBody();
        String[] titles = result.getHeaders();

        tableModel = new DefaultTableModel(data, titles) {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            };

        };
        searchResultsTable.setModel(tableModel);

        searchResultsTable
                .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // key column width
        searchResultsTable.getColumnModel().getColumn(0)
                .setMaxWidth(FIRST_ROW_MAX_WIDTH);
    };

    /**
     * Gets the data of the currently selected row.
     * 
     * @return the row data
     */
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

    /**
     * Shows the dialog and returns the value of {@link #getRowData()} if the
     * the "OK" button was pressed.
     * 
     * @return the string[]
     */
    public String[] showDialog() {
        setVisible(true);
        return (getResult() == RESULT_OK) ? getRowData() : null;
    }

}