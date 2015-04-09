package com.aerhard.oxygen.plugin.dbtagger;

import com.aerhard.oxygen.plugin.dbtagger.ui.Table;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TableTest {

    /**
     * Test Initialization of the table model.
     */
    @Test
    public void testInitTableModel() {
        
        // test with no data
        String[] headers = {"a", "b"};
        TableData tableData = new TableData(headers, null);
        
        Table table = new Table();
        table.initTableModel(tableData);
    }

}
