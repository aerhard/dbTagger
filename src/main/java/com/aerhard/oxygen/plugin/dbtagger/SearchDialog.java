/**
 * Copyright 2013 Alexander Erhard
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aerhard.oxygen.plugin.dbtagger;

import com.aerhard.oxygen.plugin.dbtagger.ui.Table;
import com.aerhard.oxygen.plugin.dbtagger.util.HttpUtil;
import com.aerhard.oxygen.plugin.dbtagger.util.JsonUtil;
import com.jidesoft.swing.InfiniteProgressPanel;
import ro.sync.annotations.api.API;
import ro.sync.annotations.api.APIType;
import ro.sync.annotations.api.SourceType;
import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;
import ro.sync.exml.workspace.api.Workspace;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;

/**
 * The search dialog component.
 */
@API(type = APIType.INTERNAL, src = SourceType.PUBLIC)
public class SearchDialog extends OKCancelDialog {

    private static final long serialVersionUID = 1L;

    /**
     * The preferred width of the config window.
     */
    private static final int PREFERRED_TABLE_WIDTH = 800;

    /**
     * The preferred heigth of the config window.
     */
    private static final int PREFERRED_TABLE_HEIGHT = 400;

    /**
     * The default border width of the content items
     */
    private static final int BORDER_W = 5;

    /**
     * The search action key "search"
     */
    private static final String SEARCH = "search";

    /**
     * The search field component.
     */
    private JTextField searchField;

    /**
     * The main search results table component.
     */
    private Table mainTable;

    /**
     * The search results table component for sub item results.
     */
    private Table subTable = null;

    /**
     * The model of the search results table.
     */
    private DefaultTableModel tableModel = null;

    /**
     * The json utility.
     */
    private JsonUtil jsonUtil;

    /**
     * The http utility.
     */
    private HttpUtil httpUtil;

    /**
     * The localization resource bundle.
     */
    private ResourceBundle i18n;

    /**
     * The loading mask.
     */
    private InfiniteProgressPanel loadingMask;

    /**
     * The fixed part of the main table server request URL.
     */
    private String mainUrl;

    /**
     * The server request user name.
     */
    private String user;

    /**
     * The server request password.
     */
    private String password;

    /**
     * The submittedItem
     */
    private String[] submittedItem = null;

    /**
     * Instantiates a new search dialog.
     *
     * @param workspace       oXygen's workspace object.
     * @param title           The dialog title
     * @param user            The database user
     * @param password        The database password
     * @param mainUrl          The URL for main item queries.
     * @param subUrl          The URL for sub item queries.
     * @param searchFieldText The text to put in the search field.
     */
    public SearchDialog(Workspace workspace, String title, String user, String password,
                        String mainUrl, final String subUrl, String searchFieldText) {
        super((Frame) workspace.getParentFrame(), title, true);

        this.mainUrl = mainUrl;
        this.user = user;
        this.password = password;

        jsonUtil = new JsonUtil(workspace);
        httpUtil = new HttpUtil();
        i18n = ResourceBundle.getBundle("Tagger");

        loadingMask = new InfiniteProgressPanel();
        setGlassPane(loadingMask);

        getContentPane().setLayout(new BorderLayout(0, 8));

        getContentPane().add(createSearchFieldPane(searchFieldText), BorderLayout.NORTH);

        mainTable = new Table();
        mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        initTableListeners(mainTable);

        if (subUrl == null || "".equals(subUrl)) {
            getContentPane().add(createScrollPane(mainTable, PREFERRED_TABLE_HEIGHT), BorderLayout.CENTER);
        } else {
            subTable = new Table();
            subTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            initTableListeners(subTable);

            JScrollPane mainScrollPane = createScrollPane(mainTable, PREFERRED_TABLE_HEIGHT / 2);
            JScrollPane subScrollPane = createScrollPane(subTable, PREFERRED_TABLE_HEIGHT / 2);
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainScrollPane, subScrollPane);
            splitPane.setDividerLocation(PREFERRED_TABLE_HEIGHT / 3);

            getContentPane().add(splitPane, BorderLayout.CENTER);

            mainTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        int row = mainTable.getSelectedRow();
                        if (row != -1) {
                            String key = mainTable.getModel().getValueAt(row, 0)
                                    .toString();
                            loadData(subTable, subUrl, key, false);
                        }
                    }
                }
            });
        }

        initSearchFieldListeners();

        pack();
        setResizable(true);

        setLocationRelativeTo((Component) workspace.getParentFrame());
    }

    /**
     * Creates the search field pane and its components.
     *
     * @param text the search field text
     * @return The search field pane.
     */
    private JPanel createSearchFieldPane(String text) {
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
        searchField = new JTextField(text);
        searchFieldPane.add(searchField, c);
        return searchFieldPane;
    }

    /**
     * Creates a scroll pane for a table component.
     *
     * @param table  the table component
     * @param height the preferred height of the scroll pane
     * @return The search results pane component.
     */
    private JScrollPane createScrollPane(Table table, int height) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(PREFERRED_TABLE_WIDTH,
                height));
        return scrollPane;
    }

    /**
     * Initializes the search field action listeners.
     */
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

    /**
     * Initializes the search results mainTable action listeners.
     *
     * @param table the table to add the listeners to
     */
    private void initTableListeners(final Table table) {
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        table.getInputMap(
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter,
                SEARCH);
        table.getActionMap().put(SEARCH, new AbstractAction() {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                submit(table);
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    submit(table);
                }
            }
        });
    }

    private void submit(Table table) {
        submittedItem = table.getSelectedRowData();
        doOK();
    }

    /**
     * Selects the search field text.
     *
     * @param select specifies if the content of the text field should be selected.
     */
    private void selectSearchFieldText(boolean select) {
        if (select) {
            searchField.select(0, searchField.getText().length());
        } else {
            searchField.select(0, 0);
        }
    }

    /**
     * Calls loadData(String) in a new thread
     *
     * @param searchString The search string.
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
                        loadData(mainTable, mainUrl, searchString, false);
                        loadingMask.stop();
                        searchField.setEnabled(true);
                        searchField.requestFocus();
                        selectSearchFieldText(true);
                        if (tableModel != null) {
                            tableModel.fireTableDataChanged();
                        }
                    }
                }).start();
            }
        });
    }

    /**
     * calls {@link #loadData(Table, String, String, Boolean)} and provides
     * the main table as value for the table field and true for isFirst
     *
     * @param searchString the search string
     */
    public boolean load(String searchString) {
        return loadData(mainTable, mainUrl, searchString, true);
    }

    /**
     * Initiates a server request with the provided search string; if data is
     * returned, calls {@link Table#initTableModel(TableData)}.
     *
     * @param table        the table component displaying the data
     * @param url          the query mainUrl without the search string
     * @param searchString The search string.
     * @param isFirst      Indicates if this is the first search in the search dialog.
     *                     This information can be used in a server script to process the
     *                     initial search (based on the selected text in the editor pane)
     *                     in a distinct manner.
     * @return true if data has been successfully loaded and parsed, otherwise false
     */
    private boolean loadData(Table table, String url, String searchString, Boolean isFirst) {
        TableData result = null;
        System.out.println(url+searchString);
        String response = httpUtil.get(user, password, url, searchString,
                isFirst);
        if (response != null) {
            result = jsonUtil.getTableData(response);
        }
        if (result != null) {
            table.initTableModel(result);
            return true;
        } else {
            if (table.getModel() instanceof DefaultTableModel) {
                ((DefaultTableModel) table.getModel()).setRowCount(0);
            }
        }
        return false;
    }


    /**
     * Gets the data from the subTable's selection (if it exists); if this returns null,
     * the data from the mainTable's selection is returned
     *
     * @return the selected data or null if there is no selection
     */
    private String[] getSelectionData() {
        String[] result = null;
        if (subTable != null) {
            result = subTable.getSelectedRowData();
        }
        if (result != null) {
            return result;
        }
        return mainTable.getSelectedRowData();
    }

    /**
     * Shows the dialog and returns the value of {@link Table#getSelectedRowData()} if the
     * the "OK" button was pressed.
     *
     * @return the string[]
     */
    public String[] showDialog() {
        setVisible(true);

        if (getResult() == RESULT_OK) {
            if (submittedItem != null) {
                return submittedItem;
            } else {
                return getSelectionData();
            }
        } else {
            return null;
        }
    }


}
