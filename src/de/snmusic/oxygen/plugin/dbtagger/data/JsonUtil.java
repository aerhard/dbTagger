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

package de.snmusic.oxygen.plugin.dbtagger.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.snmusic.oxygen.plugin.dbtagger.Tagger;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Requests json data via http and returns them as a SearchResult object.
 */
public class JsonUtil {

	private Logger logger = Logger.getLogger(Tagger.LOGGER);
	private StandalonePluginWorkspace workspace;

	public JsonUtil(StandalonePluginWorkspace workspace) {
		this.workspace = workspace;
	};

	public TableData transform(String input) {

		try {
			JSONObject responseJSON = new JSONObject(input);
			String[] cols = readColumnNames(responseJSON);
			if (cols != null) {
				String[][] data = readContentData(responseJSON);
				return (data == null) ? null : new TableData(cols, data);
			}
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
			workspace
					.showErrorMessage("Fehler beim Verarbeiten der JSON-Daten:\n"
							+ e.toString());
		}
		return null;
	}

	private String[] readColumnNames(JSONObject responseJSON) {
		String[] cols = null;

		try {
			if (responseJSON.optJSONArray("cols") == null) {
				workspace
						.showErrorMessage("Fehler: In der Serverantwort wurde das Objekt 'cols' nicht gefunden.");
				return null;
			}
			JSONArray colsArray = (JSONArray) responseJSON.getJSONArray("cols");

			cols = new String[colsArray.length()];
			JSONObject fieldObj = new JSONObject();

			for (int i = 0; i < colsArray.length(); i++) {
				fieldObj = (JSONObject) colsArray.get(i);
				cols[i] = fieldObj.getString("name");
			}
		} catch (JSONException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
		}
		return cols;
	}

	private String[][] readContentData(JSONObject responseJSON) {
		JSONArray dataArray;
		try {
			dataArray = (JSONArray) responseJSON.getJSONArray("data");
			int dataRows = dataArray.length();
			if (dataRows > 0) {
				String[][] resultTable = new String[dataRows][];
				for (int i = 0; i < dataRows; i++) {
					JSONArray arr = dataArray.getJSONArray(i);
					List<String> list = new ArrayList<String>();
					for (int j = 0; j < arr.length(); j++) {

						list.add(arr.getString(j));
					}
					resultTable[i] = list.toArray(new String[list.size()]);
				}
				return resultTable;
			}
		} catch (JSONException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
		}
		return null;
	}

}
