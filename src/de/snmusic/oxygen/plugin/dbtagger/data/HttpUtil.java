package de.snmusic.oxygen.plugin.dbtagger.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import de.snmusic.oxygen.plugin.dbtagger.Tagger;

public class HttpUtil {

	private Logger logger = Logger.getLogger(Tagger.LOGGER);
	private StandalonePluginWorkspace workspace;

	public HttpUtil(StandalonePluginWorkspace workspace) {
		this.workspace = workspace;
	};

	/**
	 * Performs an http get request and returns the results string.
	 */
	public String get(String user, String password, String urlStatic,
			String searchString) {
		String response = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			String url = urlStatic + URLEncoder.encode(searchString, "UTF-8");
			HttpGet httpGet = new HttpGet(url);
			if (httpGet != null) {
				httpGet.addHeader(BasicScheme.authenticate(
						new UsernamePasswordCredentials(user, password),
						"UTF-8", false));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				response = httpclient.execute(httpGet, responseHandler);
			}
		} catch (UnsupportedEncodingException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
			workspace.showErrorMessage("Fehler bei der Anfrage (Codierung der Suchanfrage):\n" + urlStatic
					+ searchString + "\n" + e.toString());
		} catch (ClientProtocolException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
			workspace.showErrorMessage("Fehler bei der Anfrage (Protokoll):\n" + urlStatic
					+ searchString + "\n" + e.toString());
		} catch (IOException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
			workspace.showErrorMessage("Fehler bei der Anfrage (IO):\n" + urlStatic
					+ searchString + "\n" + e.toString());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return response;
	};
}
