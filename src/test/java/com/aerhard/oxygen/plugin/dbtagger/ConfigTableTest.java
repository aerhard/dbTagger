package com.aerhard.oxygen.plugin.dbtagger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

import com.aerhard.oxygen.plugin.dbtagger.config.ConfigStore;
import com.aerhard.oxygen.plugin.dbtagger.config.ConfigTable;

@RunWith(Parameterized.class)
public class ConfigTableTest {

    @BeforeClass
    public static void initClass() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(new ConsoleAppender(new PatternLayout(
                "%-6r [%p] %c - %m%n")));
    }

    private StandalonePluginWorkspace workspace;
    private ConfigTable table;
    private ConfigStore configStore;
    private String[][] testData;
    private int initialLength;

    public ConfigTableTest(String[][] testData) {
        this.testData = testData;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testParameters() {
        String[][] twoRows = { { "11", "12", "13", "14", "15", "16", "17" },
                { "21", "22", "23", "24", "25", "26", "27" } };
        String[][] oneRow = { { "21", "22", "23", "24", "25", "26", "27" } };
        return Arrays.asList(new Object[][] { { twoRows },
                { oneRow } });
    }

    @Before
    public void initTC() {
        workspace = mock(StandalonePluginWorkspace.class);
        configStore = new ConfigStore(workspace);
        configStore.setAll(testData);
        table = new ConfigTable(configStore);
        initialLength = configStore.getAll().length;
        table.setData();
    }

    /**
     * Tests adding rows to the config table.
     */
    @Test
    public void testAddRow() {
        table.addRow();
        table.addRow();
        int currentLength = table.getData().length;
        assertEquals(initialLength + 2, currentLength);
    }

    /**
     * Tests deleting rows in the config table.
     */
    @Test
    public void testDeleteRow() {
        table.deleteRow();
        table.deleteRow();
        int currentLength = table.getData().length;
        assertTrue(currentLength < 1 || initialLength - 2 == currentLength);
    }

    /**
     * Tests duplicating rows in the config table.
     */
    @Test
    public void testDuplicateRowFirst() {
        String s = table.getData()[0][0];
        table.setRowSelectionInterval(0, 0);
        table.duplicateRow();

        int currentLength = table.getData().length;
        assertEquals(initialLength + 1, currentLength);
        assertEquals(s, table.getData()[currentLength - 1][0]);
    }

    @Test
    public void testDuplicateRowLast() {
        String s = table.getData()[initialLength - 1][0];
        table.setRowSelectionInterval(initialLength - 1, initialLength - 1);
        table.duplicateRow();

        int currentLength = table.getData().length;
        assertEquals(initialLength + 1, currentLength);
        assertEquals(s, table.getData()[currentLength - 1][0]);
    }

}
