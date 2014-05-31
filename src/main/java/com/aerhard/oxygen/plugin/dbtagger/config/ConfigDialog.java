/*
 * Copyright 2013 Alexander Erhard

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.aerhard.oxygen.plugin.dbtagger.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Manages the config dialog component.
 */
public class ConfigDialog {

    /** The preferred height of the config window */
    private static final int PREFERRED_WIDTH = 800;

    /** The preferred heigth of the config window */
    private static final int PREFERRED_HEIGHT = 400;

    /** The default border width of the content items */
    private static final int BORDER_W = 5;

    /** The config table component. */
    private ConfigTable table;

    /** oXygen's workspace object.. */
    private StandalonePluginWorkspace workspace;

    /** The localization resource bundle. */
    private ResourceBundle i18n;

    /** The frame title. */
    private String title;

    /**
     * Instantiates a new config window.
     * 
     * @param workspace
     *            oXygen's workspace object.
     * @param title
     *            the frame title
     */
    public ConfigDialog(StandalonePluginWorkspace workspace,
            ConfigStore configStore, String title) {
        this.workspace = workspace;
        this.title = title;
        i18n = ResourceBundle.getBundle("Tagger");
        table = new ConfigTable(configStore);
        table.setData();
    };

    /**
     * Shows and evaluates the config dialog.
     * 
     * @return the new config or null if there are no new config to store
     */
    public String[][] show() {
        int result = JOptionPane.showConfirmDialog(
                (java.awt.Frame) workspace.getParentFrame(),
                createDialogContent(),
                title + " - " + i18n.getString("configDialog.configure"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            return table.getData();
        } else {
            return null;
        }
    }

    /**
     * Creates all components in the config dialog.
     * 
     * @return the content pane
     */
    private JPanel createDialogContent() {
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(BORDER_W, BORDER_W, BORDER_W, BORDER_W),
                BorderFactory.createLineBorder(Color.GRAY)));
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.setPreferredSize(new Dimension(PREFERRED_WIDTH,
                PREFERRED_HEIGHT));
        contentPane.setBorder(new EtchedBorder());
        contentPane.add(createTableActionButtons(), BorderLayout.SOUTH);
        contentPane.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                Window window = SwingUtilities.getWindowAncestor(contentPane);
                if (window instanceof Dialog) {
                    Dialog dialog = (Dialog) window;
                    if (!dialog.isResizable()) {
                        dialog.setResizable(true);
                    }
                }
            }
        });
        return contentPane;
    }

    /**
     * Creates and initializes the table action buttons.
     * 
     * @return a JPanel containing the buttons
     */
    private JPanel createTableActionButtons() {
        JButton newButton = new JButton(i18n.getString("configDialog.new"));
        JButton deleteButton = new JButton(i18n.getString("configDialog.delete"));
        JButton duplicateButton = new JButton(i18n.getString("configDialog.duplicate"));
        JButton resetButton = new JButton(i18n.getString("configDialog.reset"));

        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.addRow();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.deleteRow();
            }
        });
        duplicateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.duplicateRow();
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.setData();
            }
        });

        JPanel box = new JPanel(new GridLayout(1, 4, 6, 8));
        box.add(newButton);
        box.add(deleteButton);
        box.add(duplicateButton);
        box.add(resetButton);

        JPanel boxContainer = new JPanel(new GridBagLayout());
        boxContainer.add(box);
        boxContainer.setBorder(BorderFactory.createEmptyBorder(0, BORDER_W,
                BORDER_W, BORDER_W));

        return boxContainer;
    }
}
