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

package com.aerhard.oxygen.plugin.dbtagger.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aerhard.oxygen.plugin.dbtagger.TableData;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Container for JSON utility functions.
 */
public class JsonUtil {

    /** oXygen's workspace object.. */
    private StandalonePluginWorkspace workspace;

    /** The localization resource bundle. */
    private ResourceBundle i18n;

    /**
     * Instantiates a new JsonUtil object.
     * 
     * @param workspace
     *            oXygen's workspace object.
     */
    public JsonUtil(StandalonePluginWorkspace workspace) {
        this.workspace = workspace;
        i18n = ResourceBundle.getBundle("Tagger");
    };

    /**
     * Transforms a JSON string to a TableData object.
     * 
     * @param input
     *            the input data
     * @return the table data object or null if data could not be tranformed for
     *         further processing.
     */
    public TableData transform(String input) {

        try {
            JSONObject responseJSON = new JSONObject(input);
            String[] headers = readTableHeaders(responseJSON);
            if (headers != null) {
                if (!responseJSON.has("data")) {
                    return new TableData(headers, null);
                }
                String[][] data = readContentData(responseJSON, headers.length);
                return (data == null) ? null : new TableData(headers, data);
            }
        } catch (Exception e) {
            workspace.showErrorMessage(i18n
                    .getString("jsonUtil.transformError"));
        }
        return null;
    }

    /**
     * Reads table headers from the JSON server response.
     * 
     * @param responseJSON
     *            the JSON response
     * @return the headers
     */
    private String[] readTableHeaders(JSONObject responseJSON) {
        String[] cols = null;

        try {
            if (responseJSON.optJSONArray("cols") == null) {
                workspace.showErrorMessage(i18n
                        .getString("jsonUtil.columnNameError"));
                return null;
            }
            JSONArray colsArray = responseJSON.getJSONArray("cols");

            cols = new String[colsArray.length()];
            JSONObject fieldObj = new JSONObject();

            for (int i = 0; i < colsArray.length(); i++) {
                fieldObj = (JSONObject) colsArray.get(i);
                cols[i] = fieldObj.optString("name");
            }
        } catch (JSONException e) {
            workspace.showErrorMessage(i18n
                    .getString("jsonUtil.columnNameError"));
        }
        return cols;
    }

    /**
     * Reads the table body content from the JSON server response.
     * 
     * @param responseJSON
     *            the JSON response
     * @return the body content
     */
    private String[][] readContentData(JSONObject responseJSON, int cols) {
        try {
            JSONArray dataArray = responseJSON.getJSONArray("data");
            if (dataArray != null) {
                int dataRows = dataArray.length();
                if (dataRows > 0) {
                    return convertArray(cols, dataArray, dataRows);
                }
            }
        } catch (JSONException e) {
            workspace.showErrorMessage(i18n.getString("jsonUtil.dataError")
                    + "\n" + e.toString());
        } catch (ArrayStoreException e) {
            workspace.showErrorMessage(e.toString());
        }
        return null;
    }

    private String[][] convertArray(int cols, JSONArray dataArray, int dataRows) {
        String[][] resultTable = new String[dataRows][];
        for (int i = 0; i < dataRows; i++) {
            JSONArray arr = dataArray.getJSONArray(i);
            List<String> list = new ArrayList<String>();
            if (arr.length() < cols) {
                throw new ArrayStoreException(
                        i18n.getString("jsonUtil.dataColumnError"));
            }
            for (int j = 0; j < cols; j++) {
                list.add(arr.isNull(j) ? "" : arr.getString(j));
            }
            resultTable[i] = list.toArray(new String[cols]);
        }
        return resultTable;
    }
}
