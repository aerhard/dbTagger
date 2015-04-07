package com.aerhard.oxygen.plugin.dbtagger.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ResourceBundle;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import ro.sync.exml.workspace.api.Workspace;

/**
 * Utility functions.
 */
public class HttpUtil {

    private static final String UTF_8 = "UTF-8";

    /** oXygen's workspace object. */
    private Workspace workspace;

    /** The localization resource bundle. */
    private ResourceBundle i18n;

    /**
     * Instantiates a new HTTP utility object.
     * 
     * @param workspace
     *            the workspace
     */
    public HttpUtil(Workspace workspace) {
        this.workspace = workspace;
        i18n = ResourceBundle.getBundle("Tagger");
    };

    /**
     * Performs an HTTP get request and returns the resulting string or null if
     * an error occurred.
     * 
     * @param user
     *            The user name.
     * @param password
     *            The password.
     * @param urlOption
     *            The part of the URL specified in the option dialog
     * @param searchString
     *            The search string.
     * @param isFirst
     *            Specifies if this is the first search in a series.
     * @return The response string.
     */
    public String get(String user, String password, String urlOption,
            String searchString, Boolean isFirst) {
        String response = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            String url = urlOption + URLEncoder.encode(searchString, UTF_8);

            if (isFirst) {
                url += ((searchString.contains("?") || url
                        .contains("?")) ? "&" : "?") + "first=true";
            }

            HttpGet httpGet = new HttpGet(url);
            if (httpGet != null) {
                if (user != null && password != null) {
                    httpGet.addHeader(BasicScheme.authenticate(
                            new UsernamePasswordCredentials(user, password), UTF_8,
                            false));
                }
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                response = httpClient.execute(httpGet, responseHandler);
            }
        } catch (UnsupportedEncodingException e) {
            workspace.showErrorMessage(i18n.getString("httpUtil.encodingError")
                    + ":\n" + urlOption + searchString + "\n" + e.toString());
        } catch (ClientProtocolException e) {
            workspace.showErrorMessage(i18n.getString("httpUtil.protocolError")
                    + ":\n" + urlOption + searchString + "\n" + e.toString());
        } catch (IOException e) {
            workspace.showErrorMessage(i18n.getString("httpUtil.IOError")
                    + ":\n" + urlOption + searchString + "\n" + e.toString());
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return response;
    };
}
