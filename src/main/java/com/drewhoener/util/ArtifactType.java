package com.drewhoener.util;

import java.util.Arrays;
import java.util.Optional;

public enum ArtifactType {
	MAVEN,
	JENKINS,
	FILE;

	public static ArtifactType fromString(String type){
		Optional<ArtifactType> foundType = Arrays.stream(values()).filter(val -> val.name().equalsIgnoreCase(type)).findAny();
		return foundType.orElse(null);
	}
}
