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

import java.net.URLEncoder;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
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
	 */
	public static String getHtmlString(String user, String password,
			String urlStatic, String searchString) throws Exception {
		String response = "";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpGet httpPost = new HttpGet(urlStatic
					+ URLEncoder.encode(searchString, "UTF-8"));
			httpPost.addHeader(BasicScheme.authenticate(
					 new UsernamePasswordCredentials(user, password),
					 "UTF-8", false));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
	        response = httpclient.execute(httpPost, responseHandler);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return response;
	};
	}
