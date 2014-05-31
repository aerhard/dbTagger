package com.aerhard.oxygen.plugin.dbtagger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.aerhard.oxygen.plugin.dbtagger.util.JsonUtil;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

@RunWith(MockitoJUnitRunner.class)
public class JsonUtilTest {

    private StandalonePluginWorkspace workspace;
    private JsonUtil jsonUtil;

    private String validString = "{ "
            + "\"cols\" : [{ \"name\" : \"Key\" }, "
            + "{ \"name\" : \"Name\" }, "
            + "{ \"name\" : \"Description\" }], "
            + "\"data\" : [[\"1\", \"v12\", \"v13\"], [\"2\", \"\", \"v23\"], [\"3\", \"v32\", null]] }";
    private String unparsableString = "[[";
    private String missingCols = "{ "
            + "\"data\" : [[\"1\", \"v12\", \"v13\"], [\"2\", \"\", \"v23\"], [\"3\", \"v32\", null]] }";
    private String missingData = "{ " + "\"cols\" : [{ \"name\" : \"Key\" }, "
            + "{ \"name\" : \"Name\" }, " + "{ \"name\" : \"Description\" }] }";
    private String notEnoughDataRows = "{ "
            + "\"cols\" : [{ \"name\" : \"Key\" }, "
            + "{ \"name\" : \"Name\" }, "
            + "{ \"name\" : \"Description\" }], "
            + "\"data\" : [[\"1\", \"v12\", \"v13\"], [\"2\", \"\", \"v23\"], [\"3\", \"v32\"]] }";

    @Before
    public void initTC() {
        workspace = mock(StandalonePluginWorkspace.class);
        jsonUtil = new JsonUtil(workspace);
    }

    /**
     * Tests JSON utility function with a valid string.
     */
    @Test
    public void testValidString() {
        TableData data = jsonUtil.getTableData(validString);
        verify(workspace, never()).showErrorMessage(anyString());
        assertEquals("Name", data.getHeaders()[1]);
        assertEquals("1", data.getBody()[0][0]);
        assertEquals("", data.getBody()[2][2]);
    }

    /**
     * Tests JSON utility function with an unparsable string.
     */
    @Test
    public void testUnparsableString() {
        @SuppressWarnings("unused")
        TableData data = jsonUtil.getTableData(unparsableString);
        verify(workspace).showErrorMessage(anyString());
    }

    /**
     * Tests JSON utility function with data in which the column titles are missing.
     */
    @Test
    public void testMissingCols() {
        @SuppressWarnings("unused")
        TableData data = jsonUtil.getTableData(missingCols);
        verify(workspace).showErrorMessage(anyString());
    }

    /**
     * Tests JSON utility function with a string input which lacks the table body data.
     */
    @Test
    public void testMissingData() {
        @SuppressWarnings("unused")
        TableData data = jsonUtil.getTableData(missingData);
        verify(workspace, never()).showErrorMessage(anyString());
    }

    /**
     * Tests JSON utility function with data where rows are missing in the table body object.
     */
    @Test
    public void testEnoughDataRowsCols() {
        @SuppressWarnings("unused")
        TableData data = jsonUtil.getTableData(notEnoughDataRows);
        verify(workspace).showErrorMessage(anyString());
    }
}
