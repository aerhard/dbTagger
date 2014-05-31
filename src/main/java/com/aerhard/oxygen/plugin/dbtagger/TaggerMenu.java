package com.aerhard.oxygen.plugin.dbtagger;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.aerhard.oxygen.plugin.dbtagger.config.ConfigStore;
import com.aerhard.oxygen.plugin.dbtagger.config.ConfigDialog;
import com.aerhard.oxygen.plugin.dbtagger.pageaccess.AuthorPageAccess;
import com.aerhard.oxygen.plugin.dbtagger.pageaccess.EditorPageAccess;
import com.aerhard.oxygen.plugin.dbtagger.pageaccess.AbstractPageAccess;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

/**
 * The menu component in oXygen's toolbar.
 */
public class TaggerMenu extends Menu {

    private static final long serialVersionUID = 1L;

    /** The menu title. */
    private String title;

    /** oXygen's workspace object. */
    private StandalonePluginWorkspace workspace;

    /** The localization resource bundle. */
    private ResourceBundle i18n;

    /** The config store. */
    private ConfigStore configStore;

    /** The interface to the current author or editor page. */
    private AbstractPageAccess pageAccess;

    /**
     * Instantiates a new tagger menu component.
     * 
     * @param workspace
     *            oXygen's workspace object.
     * @param title
     *            the menu title
     */
    TaggerMenu(StandalonePluginWorkspace workspace, String title) {
        super(title, true);
        this.workspace = workspace;
        this.title = title;
        configStore = new ConfigStore(workspace);
        i18n = ResourceBundle.getBundle("Tagger");
    }

    /**
     * Creates new instances of the menu items; called every time the user
     * submits the config dialog with "OK".
     */
    public void createMenuItems() {
        if (getItemCount() > 0) {
            removeAll();
        }
        String[][] configItems = configStore.getAll();
        addSearchButtons(configItems);
        if (configItems.length > 0) {
            addSeparator();
        }
        addConfigButton();
    }

    /**
     * Adds the search buttons to the menu.
     * 
     * @param configItems
     *            the config for the individual search buttons
     */
    private void addSearchButtons(String[][] configItems) {
        for (final String[] configItem : configItems) {
            JMenuItem searchButton = new JMenuItem();
            searchButton.setAction(createSearchAction(configItem));
            searchButton.setAccelerator(KeyStroke
                    .getKeyStroke(configItem[ConfigStore.ITEM_SHORTCUT]));
            searchButton.setText(configItem[ConfigStore.ITEM_TITLE]);
            add(searchButton);
        }
    }

    /**
     * Adds the config button to the menu.
     */
    private void addConfigButton() {
        JMenuItem configButton = new JMenuItem();
        configButton.setAction(createConfigDialogAction());
        configButton.setText(i18n.getString("configDialog.configure"));
        add(configButton);
    }

    /**
     * Creates a search action
     * 
     * @param configItem
     *            The config for this search action
     * @return the search action
     */
    private Action createSearchAction(final String[] configItem) {
        Action searchAction = new AbstractAction() {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                pageAccess = getPageAccess();
                if (pageAccess != null) {
                    openDialog(pageAccess.getSelectedText(), configItem);
                }
            }
        };
        return searchAction;
    };

    /**
     * Opens the search dialog.
     * 
     * @param selection
     *            the selection in the current editor pane
     * @return the results from the search or null if there are no results
     */
    private void openDialog(final String selection, final String[] configItem) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SearchDialog dialog = new SearchDialog(workspace, selection,
                        configItem);
                if (dialog.hasValidData()) {
                    final String[] selectedSearchResult = dialog.showDialog();
                    if (selectedSearchResult != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                pageAccess.insertTemplatedData(
                                        selectedSearchResult, configItem);
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Returns the access to the current editor pane.
     * 
     * @return a new editor or author page access object, or null, if there is
     *         no editor pane
     */
    public AbstractPageAccess getPageAccess() {
        WSEditorPage currentPage = workspace.getCurrentEditorAccess(
                PluginWorkspace.MAIN_EDITING_AREA).getCurrentPage();
        if (currentPage instanceof WSTextEditorPage) {
            return new EditorPageAccess((WSTextEditorPage) currentPage);
        } else if (currentPage instanceof WSAuthorEditorPage) {
            return new AuthorPageAccess((WSAuthorEditorPage) currentPage);
        } else {
            return null;
        }
    };

    /**
     * Creates the action to open the config dialog
     * 
     * @return the new action
     */
    private Action createConfigDialogAction() {
        return new AbstractAction() {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigDialog dialog = new ConfigDialog(workspace, configStore,
                        title);

                String[][] newConfig = dialog.show();
                if (newConfig != null) {
                    configStore.setAll(newConfig);
                    createMenuItems();
                }
            }
        };
    };

}
