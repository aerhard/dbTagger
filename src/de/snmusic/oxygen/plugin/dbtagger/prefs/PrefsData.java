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

package de.snmusic.oxygen.plugin.dbtagger.prefs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.apache.log4j.Logger;

import de.snmusic.oxygen.plugin.dbtagger.Tagger;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.util.UtilAccess;

/**
 * Manages storage and retrieval of the plugin's user preferences.
 */
public class PrefsData {

	private static String defaultUser = "guest";
	private static String defaultPassword = "guest";

	public static final int NAME = 0;
	public static final int URL = 1;
	public static final int USER = 2;
	public static final int PASSWORD = 3;
	public static final int TEXT_TPL = 4;
	public static final int AUTHOR_TPL = 5;
	public static final int SHORTCUT = 6;

	public static final String[] PREFS_NAMES = { "Name", "Request URL", "User",
			"Pass", "Template (Text)", "Template (Author)", "Shortcut" };

	private Logger logger = Logger.getLogger(Tagger.LOGGER);
	private String preferencesFile = "dbTagger-1.1.dat";

	/**
	 * Demo user preferences
	 */
	private String[][] demoPreferences = {
			{
					"Personen (surround selected text)",
					"http://localhost:8080/exist/rest/db/dbtagger/dbtagger.xql?coll=per&q=",
					defaultUser,
					defaultPassword,
					"<rs type=\"person\" key=\"${1}\">${selection}</rs>",
					"<rs type=\"person\" key=\"${1}\" xmlns=\"http://www.tei-c.org/ns/1.0\">${selection}</rs>",
					"control shift P" },
			{
					"Quellenbesitzer (replace selected text)",
					"http://localhost:8080/exist/rest/db/dbtagger/dbtagger.xql?coll=repo&q=",
					defaultUser,
					defaultPassword,
					"<repository key=\"${1}\">${2}</repository>",
					"<repository key=\"${1}\" xmlns=\"http://www.tei-c.org/ns/1.0\">${2}</repository>",
					"control shift M" },
			{
					"Werke",
					"http://localhost:8080/exist/rest/db/dbtagger/dbtagger.xql?coll=work&q=",
					defaultUser,
					defaultPassword,
					"<rs type=\"work\" key=\"${1}\">${selection}</rs>",
					"<rs type=\"work\" key=\"${1}\" xmlns=\"http://www.tei-c.org/ns/1.0\">${selection}</rs>",
					"control shift N" },
			{
					"Literatur",
					"http://localhost:8080/exist/rest/db/dbtagger/dbtagger.xql?coll=lit&q=",
					defaultUser,
					defaultPassword,
					"<rs type=\"lit\" key=\"${1}\">${selection}</rs>",
					"<rs type=\"lit\" key=\"${1}\" xmlns=\"http://www.tei-c.org/ns/1.0\">${selection}</rs>",
					"control shift B" }

	};

	private String[][] currentPreferences;

	private StandalonePluginWorkspace workspace;

	public PrefsData(StandalonePluginWorkspace workspace) {
		this.workspace = workspace;
		load();
	};

	/**
	 * Loads and decodes the plugin's user preferences from oXygen's preferences
	 * directory. If there is preferences file or any error occurs, default
	 * values will be used instead.
	 */
	private void load() {
		UtilAccess utilAccess = workspace.getUtilAccess();
		String pathToFile = getPreferencesFile();
		String[][] data = null;
		FileInputStream fis;
		try {
			fis = new FileInputStream(pathToFile);
			ObjectInputStream iis = new ObjectInputStream(fis);
			data = (String[][]) iis.readObject();
			iis.close();
		} catch (FileNotFoundException e) {
			data = null;
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
		} catch (IOException e) {
			data = null;
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
		} catch (ClassNotFoundException e) {
			data = null;
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
		}

		if (data == null) {
			setDefaultPreferences();
		} else {
			for (int i = 0; i < data.length; i++) {
				String decrypted = utilAccess.decrypt(data[i][PASSWORD]);
				data[i][PASSWORD] = (decrypted == null) ? "" : decrypted;
			}
			this.currentPreferences = data;
		}
	}

	private String getPreferencesFile() {
		return workspace.getPreferencesDirectory() + "/" + preferencesFile;
	};

	/**
	 * Stores the plugin's user preferences to oXygen's preferences directory
	 * and encrypts the passwords.
	 * 
	 * @param preferences
	 *            all user preferences.
	 */
	private void save(String[][] preferences) {
		UtilAccess utilAccess = workspace.getUtilAccess();
		String[][] encryptedData = null;
		try {
			encryptedData = new String[preferences.length][];
			System.arraycopy(preferences, 0, encryptedData, 0,
					preferences.length);
			for (int i = 0; i < preferences.length; i++) {
				encryptedData[i] = Arrays.copyOfRange(preferences[i], 0,
						preferences[i].length);
				encryptedData[i][PASSWORD] = new String(
						utilAccess.encrypt(encryptedData[i][PASSWORD]));
			}
		} catch (Exception e1) {
			workspace.showErrorMessage("Fehler beim Verarbeiten des "
					+ "Benutzereinstellungen-Arrays.");
			if (logger.isDebugEnabled()) {
				logger.debug(e1, e1);
			}
			return;
		}
		String path = getPreferencesFile();
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(encryptedData);
			oos.close();
		} catch (FileNotFoundException e) {
			workspace.showErrorMessage("Benutzereinstellungen-Datei " + path
					+ " nicht gefunden.");
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
		} catch (IOException e) {
			workspace.showErrorMessage("Fehler beim Schreiben der Datei "
					+ path);
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
		}
	};

	/**
	 * Sets the default user preferences.
	 */
	private void setDefaultPreferences() {
		this.currentPreferences = this.demoPreferences;
	}

	/**
	 * 
	 * @return Array of empty strings, to be used as template for new rows in
	 *         the preferences editor table.
	 */
	public String[] getEmptyPreference() {
		String[] s = new String[this.getDemoPreferences()[0].length];
		for (int i = 0; i < s.length; i++) {
			s[i] = "";
		}
		return s;
	};

	public String[][] getCurrentPreferences() {
		return this.currentPreferences;
	};

	public String[][] getDemoPreferences() {
		return this.demoPreferences;
	};

	/**
	 * Updates the current preferences with new values.
	 * 
	 * @param newPreferences
	 *            the preferences array to be set.
	 */
	public void setCurrentPreferences(String[][] newPreferences) {
		this.currentPreferences = Arrays.copyOf(newPreferences,
				newPreferences.length);
		save(newPreferences);
	};

}
