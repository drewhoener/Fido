package com.drewhoener;

import com.drewhoener.artifact.Artifact;
import com.drewhoener.artifact.FileArtifact;
import com.drewhoener.artifact.JenkinsArtifact;
import com.drewhoener.artifact.MavenArtifact;
import com.drewhoener.util.YamlWrapper;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class ArtifactRequest {

	private String repositoryUrl = "https://repo.maven.apache.org/maven2";
	private String user = null;
	private String password = null;
	private Artifact artifact = null;
	private String outputFolderName = "";
	private String outputFilename = "";
	private boolean replaceExisting = true;

	private String name;

	public ArtifactRequest(String name, YamlWrapper wrapper) throws Exception {

		this.name = name;

		Validate.isTrue(wrapper.hasKey("repository_url") && wrapper.hasKey("file_data") && wrapper.isSection("file_data"));

		this.repositoryUrl = wrapper.getString("repository_url").trim();

		this.user = wrapper.getString("user");
		this.password = wrapper.getString("password");
		this.outputFolderName = wrapper.getString("output_folder", "").trim();
		this.outputFilename = wrapper.getString("output_name", "");

		this.replaceExisting = wrapper.getBoolean("replace_existing", true);

		if (repositoryUrl.endsWith("/"))
			repositoryUrl = repositoryUrl.substring(0, repositoryUrl.length() - 1);
		if (outputFolderName.endsWith(File.separator))
			outputFolderName = outputFolderName.substring(0, outputFolderName.length() - 1);

		YamlWrapper artifactData = wrapper.getSection("file_data");

		if(artifactData.hasKey("jenkins"))
			this.artifact = new JenkinsArtifact(artifactData.getSection("jenkins"));
		else if(artifactData.hasKey("maven")) {
			this.artifact = new MavenArtifact(artifactData.getSection("maven"));
			YamlWrapper mavenSection = artifactData.getSection("maven");
			if(mavenSection.hasKey("version_type")){
				if(mavenSection.getString("version_type").trim().equalsIgnoreCase("latest_all")){
					((MavenArtifact) this.artifact).parseNonNumericVersion(this.repositoryUrl, this.user, this.password);
				}
			}
		}
		else
			this.artifact = new FileArtifact();
	}

	public void downloadArtifact() throws Exception {

		HttpURLConnection connection = this.artifact.getCompleteConnection(repositoryUrl, user, password);

		ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());

		File folder = new File(outputFolderName);
		folder.mkdirs();
		File jar = new File(folder, outputFilename);
		if(jar.exists() && !replaceExisting){
			Fido.log("\tRequested Archive from Request \'" + this.name + "\' already exists and has requested not to replace the file. *This file will be skipped*");
			rbc.close();
			return;
		}
		FileOutputStream outputStream = new FileOutputStream(jar, false);

		outputStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		outputStream.close();
		rbc.close();
	}

	public String getOutputFilename() {
		return outputFilename;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "ArtifactRequest{" +
				"repositoryUrl='" + repositoryUrl + '\'' +
				", artifact=" + artifact +
				", outputFolderName='" + outputFolderName + '\'' +
				", outputFilename='" + outputFilename + '\'' +
				", replaceExisting=" + replaceExisting +
				'}';
	}
}
