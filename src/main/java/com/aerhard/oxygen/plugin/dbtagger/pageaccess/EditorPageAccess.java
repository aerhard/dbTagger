package com.aerhard.oxygen.plugin.dbtagger.pageaccess;

import org.apache.log4j.Logger;



import com.aerhard.oxygen.plugin.dbtagger.config.ConfigStore;

import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;

/**
 * An interface to an oXygen editor page.
 */
public class EditorPageAccess extends AbstractPageAccess {

    /** The oXygen editor page. */
    private WSTextEditorPage editorPage;

    /** The logger. */
    private static final Logger LOGGER = Logger
            .getLogger(EditorPageAccess.class.getName());

    /**
     * Instantiates a new editor page access.
     * 
     * @param editorPage
     *            the oXygen editor page
     */
    public EditorPageAccess(WSTextEditorPage editorPage) {
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

        String template = configItem[ConfigStore.ITEM_TEXT_PAGE_TEMPLATE];
        
        String selectedText = getSelectedText();
        String fragment = applyTemplate(data, template, selectedText);

        editorPage.beginCompoundUndoableEdit();
        int selectionOffset = editorPage.getSelectionStart();
        editorPage.deleteSelection();
        try {
            editorPage.getDocument().insertString(selectionOffset, fragment,
                    javax.swing.text.SimpleAttributeSet.EMPTY);
        } catch (javax.swing.text.BadLocationException e) {
            LOGGER.warn("Error: insertTemplatedData()", e);
        }
        editorPage.endCompoundUndoableEdit();
    }

}
