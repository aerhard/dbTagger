package com.aerhard.oxygen.plugin.dbtagger;

import com.aerhard.oxygen.plugin.dbtagger.config.ConfigDialog;
import com.aerhard.oxygen.plugin.dbtagger.config.ConfigStore;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class UITest {

    private static final Logger LOGGER = Logger.getLogger(UITest.class
            .getName());

    public static void openConfigDialog (StandalonePluginWorkspace workspace) {

        Properties properties = new Properties();

        try {
            properties.load(ConfigTableTest.class
                    .getResourceAsStream("/plugin.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConfigStore configStore = new ConfigStore(workspace, properties);
        
        ConfigDialog configDialog = new ConfigDialog(workspace, configStore,
                properties.getProperty("plugin.name"));

        String[][] newConfig = configDialog.show();

        LOGGER.info(newConfig);
    }
    
    public static void openSearchDialog(StandalonePluginWorkspace workspace) {
        SearchDialog dialog = new SearchDialog(workspace);

        String url = "https://raw.githubusercontent.com/aerhard/dbTagger/master/src/test/json/person.json?property=value";
        String searchString = "initial search string";

        dialog.setConfig("Test Dialog", null, null, url, searchString);

        String[] result = dialog.showDialog();
        LOGGER.info(result);
    }

    @Test
    public void testSearchDialog() {
        LOGGER.info("UI test always passes in JUnit tests.");
    }

    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            LOGGER.error(e);
        } catch (InstantiationException e) {
            LOGGER.error(e);
        } catch (IllegalAccessException e) {
            LOGGER.error(e);
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.error(e);
        }
    }

    private static void runTest() {
        setSystemLookAndFeel();

        Locale.setDefault(Locale.GERMAN);
        //Locale.setDefault(Locale.ENGLISH);

        final StandalonePluginWorkspace workspace = mock(StandalonePluginWorkspace.class);

        JFrame frame = new JFrame("UI Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1,2));

        JButton openSearchDialogButton = new JButton("Search Dialog");
        openSearchDialogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSearchDialog(workspace);
            }
        });
        frame.add(openSearchDialogButton);

        JButton openConfigDialogButton = new JButton("Config Dialog");
        openConfigDialogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openConfigDialog(workspace);
            }
        });
        frame.add (openConfigDialogButton);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                runTest();
            }
        });
    }
    
}
