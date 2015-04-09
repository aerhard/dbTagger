package com.aerhard.oxygen.plugin.dbtagger;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SearchDialogUITest {

    private static final Logger LOGGER = Logger.getLogger(SearchDialogUITest.class
            .getName());

    public static void openDialog(StandalonePluginWorkspace workspace) {
        SearchDialog dialog = new SearchDialog(workspace);

        String url = "https://raw.githubusercontent.com/aerhard/dbTagger/master/src/test/json/persons.json?property=value";
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

        JButton openDialogButton = new JButton("Open Dialog");
        openDialogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDialog(workspace);
            }
        });
        frame.add (openDialogButton);

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
