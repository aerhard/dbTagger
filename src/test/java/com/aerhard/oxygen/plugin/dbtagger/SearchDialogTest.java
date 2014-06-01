package com.aerhard.oxygen.plugin.dbtagger;

import static org.mockito.Mockito.mock;

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

@RunWith(MockitoJUnitRunner.class)
public class SearchDialogTest {

    @BeforeClass
    public static void initClass() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.ERROR);
        rootLogger.addAppender(new ConsoleAppender(new PatternLayout(
                "%-6r [%p] %c - %m%n")));
    }

    private StandalonePluginWorkspace workspace;

    @Before
    public void initTC() {
        workspace = mock(StandalonePluginWorkspace.class);
    }

    /**
     * Test Initialization of the search dialog.
     */
    @Test
    public void testSearchDialog() {
        SearchDialog dialog = new SearchDialog(workspace);
        dialog.setConfig("", "", "", "", "");
    }

}
