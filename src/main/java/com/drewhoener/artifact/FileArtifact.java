package com.drewhoener.artifact;

import java.net.URL;

public class FileArtifact extends Artifact {

	@Override
	public URL getURLExtension(String repositoryUrl, String user, String password) throws Exception {
		return new URL(repositoryUrl);
	}
}
