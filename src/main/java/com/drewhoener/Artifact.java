package com.drewhoener;

import org.apache.commons.codec.binary.Base64;

import java.net.HttpURLConnection;
import java.net.URL;

public abstract class Artifact {

	public abstract URL getURLExtension(String repositoryUrl, String user, String password) throws Exception;

	public HttpURLConnection getCompleteConnection(String repo, String user, String password) throws Exception {

		return getCompleteConnection(this.getURLExtension(repo, user, password), user, password);
	}

	public HttpURLConnection getCompleteConnection(URL url, String user, String password) throws Exception {

		HttpURLConnection connection =
				(HttpURLConnection) url.openConnection();
		if (user != null && password != null)
			connection.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String((user + ":" + password).getBytes()));

		return connection;
	}

}
