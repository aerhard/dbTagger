/**
 * Copyright 2013 Alexander Erhard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aerhard.oxygen.plugin.dbtagger;

import java.io.IOException;
import java.util.Properties;

import javax.swing.JMenuBar;

import org.apache.log4j.Logger;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * The oXygen plugin extension.
 */
public class TaggerPluginExtension implements WorkspaceAccessPluginExtension {

    /** The logger. */
    private static final Logger LOGGER = Logger
            .getLogger(TaggerPluginExtension.class.getName());

    /** The plugin properties loaded from the properties file. */
    private static Properties properties = new Properties();

    /**
     * Gets the properties.
     * 
     * @return the {@link #properties}
     */
    public static Properties getProperties() {
        return properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#
     * applicationStarted
     * (ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
     */
    @Override
    public void applicationStarted(final StandalonePluginWorkspace workspace) {
        loadPluginProperties();
        TaggerMenu taggerMenu = new TaggerMenu(workspace,
                properties.getProperty("plugin.name"));
        taggerMenu.createMenuItems();
        addMenuToToolbar(workspace, taggerMenu);
    }

    /**
     * loads the plugin properties from "plugin.properties"
     */
    private static void loadPluginProperties() {

        try {
            properties.load(TaggerPluginExtension.class
                    .getResourceAsStream("/plugin.properties"));
        } catch (IOException e) {
            LOGGER.error("Could not read \"plugin.properties\".");
        }
    }

    /**
     * Adds the tagger menu to the oXygen tool bar.
     * 
     * @param workspace
     *            oXygen's workspace object.
     * @param taggerMenu
     *            the tagger menu
     */
    private void addMenuToToolbar(final StandalonePluginWorkspace workspace,
            final TaggerMenu taggerMenu) {
        workspace.addMenuBarCustomizer(new MenuBarCustomizer() {
            @Override
            public void customizeMainMenu(JMenuBar mainMenu) {
                mainMenu.add(taggerMenu,
                        Math.max(mainMenu.getMenuCount() - 2, -1));
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#
     * applicationClosing()
     */
    @Override
    public boolean applicationClosing() {
        return true;
    };

}
