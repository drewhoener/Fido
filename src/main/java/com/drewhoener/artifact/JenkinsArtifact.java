package com.drewhoener.artifact;

import com.drewhoener.util.YamlWrapper;
import org.apache.commons.lang3.Validate;

import java.net.URL;

public class JenkinsArtifact extends FileArtifact {

	private String buildNum;
	private String artifactPath;

	public JenkinsArtifact(YamlWrapper yamlWrapper){
		Validate.isTrue(yamlWrapper.hasKey("artifact_path"));
		this.buildNum = yamlWrapper.getString("build", "lastStableBuild");
		this.artifactPath = yamlWrapper.getString("artifact_path");
	}

	@Override
	public URL getURLExtension(String repositoryUrl, String user, String password) throws Exception {
		return new URL(repositoryUrl + "/" + this.buildNum + "/" + this.artifactPath);
	}
}
