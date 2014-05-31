package com.aerhard.oxygen.plugin.dbtagger;

import java.util.Arrays;

/**
 * Data object for the search results data.
 */
public class TableData {

    /** The table headers. */
    private String[] headers;

    /** The table body. */
    private String[][] body;

    /**
     * Instantiates a new table data object.
     * 
     * @param columnTitles
     *            the table column titles
     * @param data
     *            the table data
     */
    public TableData(String[] columnTitles, String[][] data) {
        headers = Arrays.copyOf(columnTitles, columnTitles.length);
        body = (data == null) ? null : Arrays.copyOf(data, data.length);
    }

    /**
     * Gets the table titles.
     * 
     * @return the table headers
     */
    public String[] getHeaders() {
        return headers;
    }

    /**
     * Gets the table body.
     * 
     * @return the table body.
     */
    public String[][] getBody() {
        return body;
    }

}
