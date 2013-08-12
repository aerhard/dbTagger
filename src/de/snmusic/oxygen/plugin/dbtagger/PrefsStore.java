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

package de.snmusic.oxygen.plugin.dbtagger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.util.UtilAccess;

/**
 * Manages storage and retrieval of the plugin's user preferences.
 */
public class PrefsStore {

	private static String defaultUser = "guest";
	private static String defaultPassword = "guest";

	private String pluginPrefsFile = "dbTagger.dat";

	public static final int NAME = 0;
	public static final int URL = 1;
	public static final int USER = 2;
	public static final int PASSWORD = 3;
	public static final int TEXT_TPL = 4;
	public static final int AUTHOR_TPL = 5;
	public static final int SHORTCUT = 6;

	public String[] prefsNames = { "Name", "Request URL", "User", "Pass",
			"Template (Text)", "Template (Author)", "Shortcut" };

	/**
	 * Default user preferences
	 */
	private String[][] defaultPrefsSets = {
			{
					"Personen (surround selected text)",
					"http://localhost:8080/exist/rest/db/dbtagger/dbtagger.xql?coll=per&q=",
					defaultUser,
					defaultPassword,
					"<rs type=\"person\" key=\"${key}\">${selection}</rs>",
					"<rs type=\"person\" key=\"${key}\" xmlns=\"http://www.tei-c.org/ns/1.0\">${selection}</rs>",
					"ctrl shift P" },
			{
					"Quellenbesitzer (replace selected text)",
					"http://localhost:8080/exist/rest/db/dbtagger/dbtagger.xql?coll=repo&q=",
					defaultUser,
					defaultPassword,
					"<repository key=\"${key}\">${text}</repository>",
					"<repository key=\"${key}\" xmlns=\"http://www.tei-c.org/ns/1.0\">${text}</repository>",
					"ctrl shift M" },
			{
					"Werke",
					"http://localhost:8080/exist/rest/db/dbtagger/dbtagger.xql?coll=work&q=",
					defaultUser,
					defaultPassword,
					"<rs type=\"work\" key=\"${key}\">${selection}</rs>",
					"<rs type=\"work\" key=\"${key}\" xmlns=\"http://www.tei-c.org/ns/1.0\">${selection}</rs>",
					"ctrl shift N" },
			{
					"Literatur",
					"http://localhost:8080/exist/rest/db/dbtagger/dbtagger.xql?coll=lit&q=",
					defaultUser,
					defaultPassword,
					"<rs type=\"lit\" key=\"${key}\">${selection}</rs>",
					"<rs type=\"lit\" key=\"${key}\" xmlns=\"http://www.tei-c.org/ns/1.0\">${selection}</rs>",
					"ctrl shift B" }

	};

	private String[][] currentPrefsSets;

	private StandalonePluginWorkspace workspace;

	/**
	 * Constructor of the preferences store, calls the loadPrefs() function.
	 * 
	 * @param workspace
	 */
	public PrefsStore(StandalonePluginWorkspace workspace) {
		this.workspace = workspace;
		loadPrefsSets();
	};

	/**
	 * Loads and decodes the plugin's user preferences from oXygen's preferences
	 * directory. If there is preferences file or any error occurs, default
	 * values will be used instead.
	 */
	private void loadPrefsSets() {
		UtilAccess utilAccess = workspace.getUtilAccess();
		String filename = workspace.getPreferencesDirectory() + "/"
				+ pluginPrefsFile;
		String[][] data = null;
		FileInputStream fis;
		try {
			fis = new FileInputStream(filename);
			ObjectInputStream iis = new ObjectInputStream(fis);
			data = (String[][]) iis.readObject();
			iis.close();
		} catch (FileNotFoundException e) {
			data = null;
		} catch (IOException e) {
			data = null;
		} catch (ClassNotFoundException e) {
			data = null;
		}

		if (data == null) {
			setDefaultPrefsSets();
			return;
		}
		;

		for (int i = 0; i < data.length; i++) {
			String decrypted = utilAccess.decrypt(data[i][PASSWORD]);
			data[i][PASSWORD] = (decrypted == null) ? "" : decrypted;
		}
		this.currentPrefsSets = data;
	};

	/**
	 * Stores the plugin's user preferences to oXygen's preferences directory
	 * and encrypts the passwords.
	 * 
	 * @param prefs
	 *            all user preferences.
	 */
	private void storePrefsSets(String[][] prefs) {
		UtilAccess utilAccess = workspace.getUtilAccess();
		String[][] encryptedData = null;
		try {
			encryptedData = new String[prefs.length][];
			System.arraycopy(prefs, 0, encryptedData, 0, prefs.length);
			for (int i = 0; i < prefs.length; i++) {
				encryptedData[i] = Arrays.copyOfRange(prefs[i], 0,
						prefs[i].length);
				encryptedData[i][PASSWORD] = new String(
						utilAccess.encrypt(encryptedData[i][PASSWORD]));
			}
		} catch (Exception e1) {
			workspace
					.showErrorMessage("Fehler beim Clonen und Chiffrieren des "
							+ "Benutzereinstellungen-Arrays.");
			return;
		}
		String filename = workspace.getPreferencesDirectory() + "/"
				+ pluginPrefsFile;
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(encryptedData);
			oos.close();
		} catch (FileNotFoundException e) {
			workspace.showErrorMessage("Benutzereinstellungen-Datei "
					+ filename + " nicht gefunden.");
		} catch (IOException e) {
			workspace.showErrorMessage("Fehler beim Schreiben der Datei "
					+ filename);
			e.printStackTrace();
		}
	};

	/**
	 * Sets the default user preferences.
	 */
	public void setDefaultPrefsSets() {
		this.currentPrefsSets = this.defaultPrefsSets;
	}

	/**
	 * 
	 * @return Array of empty strings, to be used as template for new rows in
	 *         the preferences editor table.
	 */
	public String[] getEmptyPrefsSet() {
		String[] s = new String[this.getDefaultPrefsSets()[0].length];
		for (int i = 0; i < s.length; i++)
			s[i] = "";
		return s;
	};

	public String[][] getCurrentPrefsSets() {
		return this.currentPrefsSets;
	};

	public String[][] getDefaultPrefsSets() {
		return this.defaultPrefsSets;
	};

	/**
	 * Updates the current preferences with new values.
	 * 
	 * @param newPrefsSets
	 *            the preferences array to be set.
	 */
	public void setCurrentPrefsSets(String[][] newPrefsSets) {
		this.currentPrefsSets = newPrefsSets;
		storePrefsSets(newPrefsSets);
	};

}
