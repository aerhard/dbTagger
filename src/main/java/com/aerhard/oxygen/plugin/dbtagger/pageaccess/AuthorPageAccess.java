package com.aerhard.oxygen.plugin.dbtagger.pageaccess;

import org.apache.log4j.Logger;

import com.aerhard.oxygen.plugin.dbtagger.config.ConfigStore;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;

/**
 * An interface to an oXygen author page.
 */
public class AuthorPageAccess extends AbstractPageAccess {

    /** The oXygen author page. */
    private WSAuthorEditorPage editorPage;

    /** The logger. */
    private static final Logger LOGGER = Logger
            .getLogger(AuthorPageAccess.class.getName());

    /**
     * Instantiates a new author page access.
     * 
     * @param editorPage
     *            the oXygen editor page
     */
    public AuthorPageAccess(WSAuthorEditorPage editorPage) {
        this.editorPage = editorPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aerhard.oxygen.plugin.dbtagger.pageaccess.AbstractPageAccess#
     * getSelectedText()
     */
    @Override
    public String getSelectedText() {
        return (editorPage.hasSelection()) ? editorPage.getSelectedText() : "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aerhard.oxygen.plugin.dbtagger.pageaccess.AbstractPageAccess#
     * processSearchResult(java.lang.String[], java.lang.String[],
     * java.lang.String)
     */
    @Override
    public void insertTemplatedData(String[] data, String[] configItem) {

        String template = configItem[ConfigStore.ITEM_AUTHOR_PAGE_TEMPLATE];

        String fragment = applyTemplate(data, template, "");

        if (template.contains("${selection}")) {
            surroundPageSelectionWith(fragment);
        } else {
            replacePageSelectionWith(fragment);
        }

    }

    /**
     * Performs a replace operation in the author page.
     * 
     * @param fragment
     *            the fragment to insert
     */
    private void replacePageSelectionWith(String fragment) {
        AuthorAccess authorAccess = editorPage.getAuthorAccess();
        AuthorDocumentController controller = authorAccess
                .getDocumentController();
        controller.beginCompoundEdit();
        boolean deleteSelection = false;
        try {
            if (authorAccess.getEditorAccess().hasSelection()) {
                deleteSelection = true;
                authorAccess.getEditorAccess().deleteSelection();
            }
            CommonsOperationsUtil.surroundWithFragment(authorAccess, false,
                    fragment);
        } catch (AuthorOperationException e) {
            if (deleteSelection) {
                controller.cancelCompoundEdit();
            }
            LOGGER.warn("Error: replacePageSelectionWith()", e);
        } finally {
            controller.endCompoundEdit();
        }
    }

    /**
     * Performs a surround operation in the author page.
     * 
     * @param fragment
     *            the fragment to insert
     */
    private void surroundPageSelectionWith(String fragment) {
        AuthorAccess authorAccess = editorPage.getAuthorAccess();
        try {
            CommonsOperationsUtil.surroundWithFragment(authorAccess, false,
                    fragment);
        } catch (AuthorOperationException e) {
            LOGGER.warn("Error: surroundPageSelectionWith()", e);
        }
    }

}
