package com.aerhard.oxygen.plugin.dbtagger.pageaccess;

/**
 * An abstract interface to an oxygen author or text editor page.
 */
public abstract class AbstractPageAccess {

    /**
     * Gets the current selection from the page.
     * 
     * @return the selected text
     */
    public abstract String getSelectedText();

    /**
     * Adds template based data to the editor page.
     * 
     * @param data
     *            the data to combine with a template
     * @param configItem
     *            the search config item containing the template.
     */
    public abstract void insertTemplatedData(String[] data, String[] configItem);

    /**
     * Applies a template to a data array.
     * 
     * @param data
     *            the input data
     * @param template
     *            the template
     * @param selection
     *            the text to replace the string "${selection}" in the template
     *            with
     * @return the combined string
     */
    protected String applyTemplate(String[] data, String template,
            String selection) {
        String fragment = template.replaceAll("\\$\\{selection\\}", selection);
        for (int i = 0; i < data.length; i++) {
            fragment = fragment.replaceAll("\\$\\{" + (i + 1) + "\\}", data[i]);
        }
        return fragment;
    }

}
