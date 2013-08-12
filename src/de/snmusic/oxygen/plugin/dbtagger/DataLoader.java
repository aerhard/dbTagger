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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Requests HTML data via http and return them as an array.
 */
public class DataLoader {

	private StandalonePluginWorkspace workspace;

	DataLoader (StandalonePluginWorkspace workspace) {
		this.workspace = workspace;
	};
	
/**
 * Calls the getHtmlString method and parses the results.
 * @param prefsSet
 * @param searchString
 * @return
 */
	public String[][] getArray(String[] prefsSet, String searchString) {
		try {
			// get request
			String html = DataLoader.getHtmlString(prefsSet[PrefsStore.USER],
					prefsSet[PrefsStore.PASSWORD],
					prefsSet[PrefsStore.URL], searchString);
			if (html != null) {
				// transform html response
				Document doc = Jsoup.parseBodyFragment(html);
				Element table = doc.select("table").first();
				Elements rows = table.select("tr");
				String[][] resultArray = new String[rows.size()][];
				for (int i = 0; i < rows.size(); i++) {
					Elements cols = rows.get(i).select("td");
					resultArray[i] = new String[cols.size()];
					for (int j = 0; j < cols.size(); j++) {
						resultArray[i][j] = cols.get(j).text();
					}
				}
				return resultArray;
			}
		} catch (Exception e) {
			workspace.showErrorMessage("Fehler bei der Anfrage:\n" + prefsSet[PrefsStore.URL]
					+ searchString + "\n" + e.toString());
		}
		return null;
	}
	
	/**
	 * Performs an http get request and returns the results string.
	 * 
	 * @param user
	 *            Username
	 * @param password
	 *            Password
	 * @param urlStatic
	 *            The static part of the url (without the search string).
	 * @param searchString
	 *            The string to be passed to the server as last part of the url.
	 * @return
	 * @throws Exception
	 */
	public static String getHtmlString(String user, String password,
			String urlStatic, String searchString) throws Exception {

		URL url = new URL(urlStatic
				+ URLEncoder.encode(searchString, "UTF-8"));

		String authString = user + ":" + password;

		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);

		URLConnection urlConnection;
		urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic "
				+ authStringEnc);
		InputStream is = urlConnection.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		int numCharsRead;
		char[] charArray = new char[1024];
		StringBuffer sb = new StringBuffer();
		while ((numCharsRead = isr.read(charArray)) > 0) {
			sb.append(charArray, 0, numCharsRead);
		}
		return sb.toString();

	};
}
