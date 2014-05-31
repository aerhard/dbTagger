package com.aerhard.oxygen.plugin.dbtagger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aerhard.oxygen.plugin.dbtagger.TaggerPluginExtension;

import com.aerhard.oxygen.plugin.dbtagger.config.ConfigStore;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class ConfigStoreTest {

    private StandalonePluginWorkspace workspace;

    @Before
    public void initTC() {
        workspace = mock(StandalonePluginWorkspace.class);
    }

    @BeforeClass
    public static void initClass() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(new ConsoleAppender(new PatternLayout(
                "%-6r [%p] %c - %m%n")));
    }

    /**
     * Initializes the config store and checks if the config items loaded from
     * the properties file have the right length.
     */
    @Test
    public void testConfigStore() {
        TaggerPluginExtension ex = new TaggerPluginExtension();
        ex.applicationStarted(workspace);
        ConfigStore configStore = new ConfigStore(workspace);

        String[][] defaults = configStore.getDefaults();

        int defaultsLength = defaults.length;

        for (int i = 0; i < defaultsLength; i++) {
            assertEquals(ConfigStore.ITEM_LENGTH, defaults[i].length);
        }
    }

}
