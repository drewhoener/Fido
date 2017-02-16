package com.drewhoener;

import com.drewhoener.util.YamlWrapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class ArtifactRequest {

	private String repositoryUrl = "https://repo.maven.apache.org/maven2";
	private String user = null;
	private String password = null;
	private MavenArtifact artifact = null;
	private String outputFolderName = "";
	private String outputFilename = "";
	private boolean replaceExisting = true;

	private String name;

	public ArtifactRequest(String name, YamlWrapper wrapper) {

		this.name = name;

		Validate.isTrue(wrapper.hasKey("file_data") && wrapper.isSection("file_data"));

		this.artifact = new MavenArtifact(wrapper.getSection("file_data"));

		if (wrapper.hasKey("repository_url"))
			this.repositoryUrl = wrapper.getString("repository_url");

		this.user = wrapper.getString("user");
		this.password = wrapper.getString("password");
		this.outputFolderName = wrapper.getString("output_folder", "");
		this.outputFilename = wrapper.getString("output_name", "");

		this.replaceExisting = wrapper.getBoolean("replace_existing", true);

		if (repositoryUrl.endsWith("/"))
			repositoryUrl = repositoryUrl.substring(0, repositoryUrl.length() - 1);
		if (outputFolderName.endsWith(File.separator))
			outputFolderName = outputFolderName.substring(0, outputFolderName.length() - 1);
	}

	public void downloadArtifact() throws Exception {

		HttpURLConnection connection = getCompleteConnection(new URL(this.repositoryUrl + this.artifact.getURLPortion(getLatestVersion())));

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

	public String getLatestVersion() throws Exception {

		URL url = new URL(this.repositoryUrl + this.artifact.getPath(true) + "/maven-metadata.xml");
		HttpURLConnection connection = getCompleteConnection(url);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");

		InputStream xml = connection.getInputStream();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document xmlDoc = db.parse(xml);

		String version = this.artifact.getXPathVersion(xmlDoc);
		xml.close();

		return version;
	}

	public String getOutputFilename() {
		return outputFilename;
	}

	public String getName() {
		return name;
	}

	public HttpURLConnection getCompleteConnection(URL url) throws IOException {

		HttpURLConnection connection =
				(HttpURLConnection) url.openConnection();
		if (user != null && password != null)
			connection.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String((user + ":" + password).getBytes()));

		return connection;
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
