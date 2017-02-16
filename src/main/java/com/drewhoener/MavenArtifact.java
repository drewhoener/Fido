package com.drewhoener;

import com.drewhoener.util.YamlWrapper;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class MavenArtifact {

	private String groupId;
	private String artifactId;
	private String version;
	private String extension;

	public MavenArtifact(YamlWrapper wrapper) {

		if (wrapper.hasKey("qualified_name")) {
			MavenArtifact artifact = parse(wrapper.getString("qualified_name"));
			this.groupId = artifact.groupId;
			this.artifactId = artifact.artifactId;
			this.version = artifact.version;
			this.extension = artifact.extension;

		} else {

			Validate.isTrue(wrapper.hasKey("group"));
			Validate.isTrue(wrapper.hasKey("artifact"));
			Validate.isTrue(wrapper.hasKey("version"));

			this.groupId = wrapper.getString("group");
			this.artifactId = wrapper.getString("artifact");
			this.version = wrapper.getString("version");

			if (wrapper.hasKey("extension"))
				this.extension = wrapper.getString("extension");
		}
	}

	private MavenArtifact(String groupId, String artifactId, String version, String extension) {
		Validate.noNullElements(new Object[]{groupId, artifactId, version});
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.extension = extension == null ? "jar" : extension;
	}

	public static MavenArtifact parse(String str) {
		String[] components = str.split(":");
		if (components.length < 3)
			throw new IllegalArgumentException("Insufficient data!");
		String groupId = components[0];
		String artifactId = components[1];
		String versionNumber = components[components.length - 1];
		String extension = null;
		if (components.length == 4)
			extension = components[2];

		return new MavenArtifact(groupId, artifactId, versionNumber, extension);
	}

	public boolean isSnapshot() {
		return this.version.endsWith("-SNAPSHOT");
	}

	public String getPath(boolean includeVersion) {

		String path = groupId.replaceAll("\\.", "/") + "/" + this.artifactId;
		if (includeVersion) {
			return "/" + path + "/" + this.version;
		}

		return "/" + path.trim();
	}

	public String getURLPortion(String latestVersion) {
		Validate.notNull(latestVersion);
		if (!isSnapshot())
			latestVersion = this.version;
		return this.getPath(true) + "/" + this.artifactId + "-" + latestVersion + "." + this.extension;
	}

	public String getFilename(String fileName) {

		if (fileName == null) {
			fileName = generateFileName();
		}
		File file = new File(fileName);
		if (file.isDirectory())
			return fileName + File.separator + generateFileName();

		return fileName;
	}

	public String getXPathVersion(Document xmlDoc) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		if (isSnapshot()) {
			String timeStamp = xpath.compile("/metadata/versioning/snapshot/timestamp/text()").evaluate(xmlDoc);
			String buildNum = xpath.compile("/metadata/versioning/snapshot/buildNumber/text()").evaluate(xmlDoc);
			return this.version.replace("-SNAPSHOT", "").trim() + "-" + timeStamp + "-" + buildNum;
		}
		return xpath.compile("/metadata/versioning/versions/version[last()]/text()").evaluate(xmlDoc);
	}

	public String generateFileName() {
		return this.artifactId.toLowerCase() + "." + this.extension.toLowerCase();
	}

	@Override
	public String toString() {
		return "MavenArtifact{" +
				"groupId='" + groupId + '\'' +
				", artifactId='" + artifactId + '\'' +
				", version='" + version + '\'' +
				", extension='" + extension + '\'' +
				'}';
	}
}
