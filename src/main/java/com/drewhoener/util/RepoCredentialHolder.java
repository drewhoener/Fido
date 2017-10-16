package com.drewhoener.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;

import static com.drewhoener.Fido.log;

public class RepoCredentialHolder implements Iterable<RepoCredentialHolder.CredentialPair> {

    private HashMap<String, CredentialPair> credentialMap = new HashMap<>();

    public void loadCredentials(File file) throws FileNotFoundException {
        this.loadCredentials(new FileInputStream(file));
    }

    public void loadCredentials(FileInputStream stream) {
        log("Loading Credentials...");
        YamlWrapper authWrapper = new YamlWrapper(stream);
        for (String key : authWrapper.getKeys()) {
            YamlWrapper section = authWrapper.getSection(key);
            String repo = section.getString("repo");
            String user = section.getString("user");
            String pass = section.getString("pass");

            if (repo == null || user == null || pass == null) {
                log("Entry \'" + key + "\' is missing elements and has been discarded.");
                continue;
            }

            if (credentialMap.containsKey(key)) {
                log("Key for entry \'" + key + "\' already exists, the new entry has been discarded.");
                continue;
            }

            this.credentialMap.put(key, new CredentialPair(repo, user, pass));
            log("Loaded credential entry \'" + key + "\'");
        }

        log("-------------------------------------------------------------");
    }

    @Override
    public Iterator<CredentialPair> iterator() {
        return credentialMap.values().iterator();
    }

    public static class CredentialPair {

        private String repo;
        private String username;
        private String password;

        public CredentialPair(String repo, String username, String password) {
            this.repo = repo;
            this.username = username;
            this.password = password;

            if (repo.endsWith("/"))
                this.repo = this.repo.substring(0, repo.length() - 1);
        }

        public String getRepo() {
            return repo;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
