package com.drewhoener.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class YamlWrapper {

	public Map<String, Object> base;

	public YamlWrapper(InputStream stream) {
		base = ((Map<String, Object>) new Yaml().load(stream));
	}

	private YamlWrapper(Map<String, Object> base) {
		this.base = base;
	}

	public Set<String> getKeys() {
		return base.keySet();
	}

	public boolean hasKey(String string) {
		return this.base.containsKey(string);
	}

	public YamlWrapper getSection(String string) {
		if (!hasKey(string) || !(this.base.get(string) instanceof Map))
			return null;
		return new YamlWrapper((Map<String, Object>) this.base.get(string));
	}

	public boolean isSection(String path) {
		return this.base.get(path) instanceof Map;
	}

	public String getString(String path) {
		return this.hasKey(path) ? this.base.get(path).toString() : null;
	}

	public String getString(String path, String defaultStr) {
		return this.hasKey(path) ? this.base.get(path).toString() : defaultStr;
	}

	public boolean getBoolean(String path, boolean def) {
		if (!hasKey(path))
			return def;
		return (this.base.get(path) instanceof Boolean) ? (Boolean) this.base.get(path) : def;
	}
}
