package com.aerhard.oxygen.plugin.dbtagger;

import java.util.Arrays;

/**
 * Data object to contain headers and body of a table.
 */
public class TableData {

    /** The table headers. */
    private String[] headers;

    /** The table body. */
    private String[][] body;

    /**
     * Instantiates a new table data object.
     * 
     * @param headers
     *            the table headers
     * @param body
     *            the table body
     */
    public TableData(String[] headers, String[][] body) {
        this.headers = Arrays.copyOf(headers, headers.length);
        this.body = (body == null) ? null : Arrays.copyOf(body, body.length);
    }

    /**
     * Gets the table titles.
     * 
     * @return the table headers
     */
    public String[] getHeaders() {
        return Arrays.copyOf(headers, headers.length);
    }

    /**
     * Gets the table body.
     * 
     * @return the table body.
     */
    public String[][] getBody() {
        return (body == null) ? null : Arrays.copyOf(body, body.length);
    }

}
