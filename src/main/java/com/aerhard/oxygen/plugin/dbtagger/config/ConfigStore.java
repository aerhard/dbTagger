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

package com.aerhard.oxygen.plugin.dbtagger.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.util.UtilAccess;

/**
 * Manages storage and retrieval of the plugin's config data.
 */
public class ConfigStore {

    /** The index of the name column. */
    public static final int ITEM_TITLE = 0;
    /** The index of the URL column. */
    public static final int ITEM_URL = 1;
    /** The index of the user name column. */
    public static final int ITEM_USER = 2;
    /** The index of the password column. */
    public static final int ITEM_PASSWORD = 3;
    /** The index of the source code editor template column. */
    public static final int ITEM_TEXT_PAGE_TEMPLATE = 4;
    /** The index of the author template column. */
    public static final int ITEM_AUTHOR_PAGE_TEMPLATE = 5;
    /** The index of the shortcut column. */
    public static final int ITEM_SHORTCUT = 6;

    /** The length of the config items */
    public static final int ITEM_LENGTH = 7;

    /** The name of the config file used to store the user's config. */
    private String configFilename;

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ConfigStore.class
            .getName());

    /**
     * Default config items (only in use when there is no dbTagger config file
     * in oXygen's preferences folder).
     */
    private String[][] defaultConfigItems;

    /** The current config items. */
    private String[][] configItems;

    /** oXygen's workspace object.. */
    private StandalonePluginWorkspace workspace;

    /** The localization resource bundle. */
    private ResourceBundle i18n;

    /**
     * Instantiates the config store.
     * 
     * @param workspace
     *            oXygen's workspace object.
     * @param properties
     *            the properties loaded from the properties file
     */
    public ConfigStore(StandalonePluginWorkspace workspace,
            Properties properties) {
        this.workspace = workspace;
        configFilename = properties.getProperty("config.filename");
        i18n = ResourceBundle.getBundle("Tagger");

        loadDefaults(properties.getProperty("config.defaultData"));
        loadUserConfig();
    }

    /**
     * Loads the default config from the properties file and writes it to
     * {@link #defaultConfigItems}
     * 
     * @param defaultData
     *            the default config data used when there is no user defined
     *            data.
     */
    private void loadDefaults(String defaultData) {
        try {
            String[] rows = defaultData.split(";");
            int rowsLength = rows.length;
            defaultConfigItems = new String[rowsLength][];
            for (int i = 0; i < rowsLength; i++) {
                defaultConfigItems[i] = rows[i].split(",");
            }
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info(
                        "Default data could not be processed. Setting empty array.",
                        e);
            }
            defaultConfigItems = new String[][] { createEmptyConfigItem() };
        }
    }

    /**
     * Loads and decodes the plugin's config from oXygen's properties directory.
     * If there is no config file or any error occurs, default values from the
     * plugin config will be used.
     */
    private void loadUserConfig() {
        UtilAccess utilAccess = workspace.getUtilAccess();
        String pathToFile = getConfigPath();
        String[][] data;
        FileInputStream fis;
        try {
            fis = new FileInputStream(pathToFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (String[][]) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            data = null;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Config file not found. Setting default config.", e);
            }
        } catch (IOException e) {
            data = null;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info(
                        "Config file IO exception. Setting default config.", e);
            }
        } catch (ClassNotFoundException e) {
            data = null;
            LOGGER.warn("Class not found. Setting default config.", e);
        }
        if (data == null) {
            configItems = defaultConfigItems;
        } else {
            for (int i = 0; i < data.length; i++) {
                String decrypted = utilAccess
                        .decrypt(data[i][ConfigStore.ITEM_PASSWORD]);
                data[i][ConfigStore.ITEM_PASSWORD] = (decrypted == null) ? ""
                        : decrypted;
            }
            configItems = data;
        }
    }

    /**
     * Gets the path to the config file in oXygen's preferences directory.
     * 
     * @return the path to the config file
     */
    private String getConfigPath() {
        return workspace.getPreferencesDirectory() + "/" + configFilename;
    }

    /**
     * Stores the config to oXygen's preferences directory.
     * 
     * @param configItems
     *            the config items
     */
    private void storeUserConfig(String[][] configItems) {
        UtilAccess utilAccess = workspace.getUtilAccess();
        String[][] encryptedData;
        try {
            encryptedData = encryptPasswordFields(configItems, utilAccess);
        } catch (Exception e) {
            workspace.showErrorMessage(i18n
                    .getString("configStore.encryptionError"));
            return;
        }
        String path = getConfigPath();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(encryptedData);
            oos.close();
        } catch (FileNotFoundException e) {
            workspace.showErrorMessage(String.format(
                    i18n.getString("configStore.fileNotFoundError"), path));
        } catch (IOException e) {
            workspace.showErrorMessage(i18n.getString("configStore.saveError")
                    + path);
        }
    }

    /**
     * Encrypts the password fields of the current config items.
     * 
     * @param configItems
     *            the config items
     * @param utilAccess
     *            oXygen's util access
     */
    private String[][] encryptPasswordFields(String[][] configItems,
            UtilAccess utilAccess) {
        String[][] encryptedData;
        encryptedData = new String[configItems.length][];
        System.arraycopy(configItems, 0, encryptedData, 0, configItems.length);
        for (int i = 0; i < configItems.length; i++) {
            encryptedData[i] = Arrays.copyOfRange(configItems[i], 0,
                    configItems[i].length);
            encryptedData[i][ConfigStore.ITEM_PASSWORD] = utilAccess
                    .encrypt(encryptedData[i][ConfigStore.ITEM_PASSWORD]);
        }
        return encryptedData;
    }

    /**
     * Sets the current config items.
     * 
     * @param newConfigItems
     *            the new config items
     */
    public void setAll(String[][] newConfigItems) {
        configItems = Arrays.copyOf(newConfigItems, newConfigItems.length);
        storeUserConfig(newConfigItems);
    }

    /**
     * Gets the current config items.
     * 
     * @return String[][]
     */
    public String[][] getAll() {
        return Arrays.copyOf(configItems, configItems.length);
    }

    /**
     * Creates an empty config item.
     * 
     * @return an array of zero-length strings.
     */
    public static String[] createEmptyConfigItem() {
        String[] newItem = new String[ConfigStore.ITEM_LENGTH];
        Arrays.fill(newItem, "");
        return newItem;
    }

}
