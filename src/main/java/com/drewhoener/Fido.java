package com.drewhoener;

import com.drewhoener.util.YamlWrapper;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Fido {

	public static long time;

	public static void main(String[] args) {

		time = System.currentTimeMillis();

		log("ARF!");

		Validate.isTrue(hasArg(args, "--path"));

		String filePath = getArg(args, "--path");
		File yamlFile = new File(filePath);

		if (!yamlFile.exists() || !yamlFile.getAbsolutePath().toLowerCase().endsWith("yml")) {
			log("Download file not provided!");
			log("File: " + yamlFile.getAbsolutePath());
			System.exit(1);
		}

		try {
			YamlWrapper yamlWrapper = new YamlWrapper(new FileInputStream(yamlFile));
			log("Starting Download for files specified in Wrapper...");
			new Fido().parseAndDownload(yamlWrapper);

		} catch (Exception e) {
			e.printStackTrace();
		}

		log("Finished Process in " + (System.currentTimeMillis() - time) + " millisecond(s)");

	}

	private static boolean hasArg(String args[], String arg) {
		for (String s : args) {
			if (s.equals(arg)) {
				return true;
			}
		}
		return false;
	}

	private static String getArg(String[] args, String arg) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(arg) && i < args.length - 1) {
				return args[i + 1];
			}
		}
		return ""; // Probably shouldn't return null?
	}

	public static void log(String s) {
		System.out.println("[Fido]:\t" + s);
	}

	public void parseAndDownload(YamlWrapper yamlWrapper) throws Exception {
		List<ArtifactRequest> requestList = new ArrayList<>();
		log("");
		for (String s : yamlWrapper.getKeys()) {
			if (yamlWrapper.isSection(s))
				try {
					requestList.add(new ArtifactRequest(s, yamlWrapper.getSection(s)));
					log("Found Key \'" + s + "\' in wrapper");

				} catch (Exception e) {
					e.printStackTrace();
				}
		}

		log("");

		for (ArtifactRequest request : requestList) {
			try {
				log("Attempting to download file \'" + request.getOutputFilename() + "\' from Wrapper Request \'" + request.getName() + "\'");
				request.downloadArtifact();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
