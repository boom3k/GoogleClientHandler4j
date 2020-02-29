package com.github.boom3k.googleclienthandler4j;

import boom3k.Zip3k;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServiceAccount {
    static GoogleCredential.Builder clientBuilder;
    static GoogleCredential credential;
    static String userName = "NULL";
    static List<String> scopes;
    static ServiceAccount instance;

    private ServiceAccount() {

    }

    static synchronized public ServiceAccount getInstance() {
        if (instance == null) {
            instance = new ServiceAccount();
        }
        return instance;
    }

    /**
     * @param inputStream InputStream of a ServiceAccount json file
     * @return this
     */
    public ServiceAccount setCredentials(InputStream inputStream) {
        try {
            credential = GoogleCredential.fromStream(inputStream);
            clientBuilder = credential.toBuilder();
            return getInstance();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param credentialFilePath path to service account json
     * @return this
     */
    public ServiceAccount setCredentials(String credentialFilePath) {
        try {
            return setCredentials(new FileInputStream(new File(credentialFilePath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method uses the Zip3k library from https://www.github.com/boom3k/zip3k
     *
     * @param zipFilePath path to zipped credentials
     * @param zipPassword password to zipped credentials
     */
    public ServiceAccount setCredentials(String zipFilePath, String zipPassword) {
        try {
            Map<String, InputStream> allZippedFiles = Zip3k.getAllZippedFiles(zipFilePath, zipPassword);
            for (String fileName : allZippedFiles.keySet()) {
                if (!fileName.contains(".json")) {
                    continue;
                }
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(new InputStreamReader(Zip3k.getAllZippedFiles(zipFilePath, zipPassword).get(fileName)));
                if (jsonObject.has("private_key")) {
                    return setCredentials(allZippedFiles.get(fileName));
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @param newScopes list of scopes used for this account
     * @return getInstance()
     */
    public ServiceAccount setScopes(List<String> newScopes) {
        scopes = newScopes;
        clientBuilder.setServiceAccountScopes(scopes);
        return getInstance();
    }

    /**
     * @param scope
     */
    public ServiceAccount setScopes(String scope) {
        List<String> scopes = new ArrayList<>();
        scopes.add(scope);
        setScopes(scopes);
        return getInstance();
    }

    /**
     * @param scopesFile path to a json file with comma separated scopes
     * @return this
     */
    public ServiceAccount setScopes(File scopesFile) {
        try {
            List<String> SCOPES = new ArrayList<>();
            String line = new String(Files.readAllBytes(Paths.get(scopesFile.getAbsolutePath())));
            for (String scope : line.split(",\r\n")) {
                SCOPES.add(scope);
            }
            return setScopes(SCOPES);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @param scopesSet Set of scopes provided directly form the library
     * @return this
     */
    public ServiceAccount setScopes(Set<String> scopesSet) {
        clientBuilder.setServiceAccountScopes(scopesSet);
        return getInstance();
    }

    /**
     * @param newUserName for subject to act as
     * @return Returns a Google Credential acting as the provided userName
     */
    public GoogleCredential getHttpClient(String newUserName) throws Exception {
        if (clientBuilder == null) {
            throw new Exception("ServiceAccount object has not been initialized");
        }
        if (clientBuilder.getServiceAccountScopes() == null || clientBuilder.getServiceAccountScopes().isEmpty()) {
            throw new Exception("Scopes have not been set for serviceAccount object.");
        }
        if (newUserName != userName) {
            System.out.println("ServiceAccountUser ->(" + userName + ") is now ->(" + newUserName + ")");
            userName = newUserName;
        }
        return clientBuilder.setServiceAccountUser(userName).build();
    }
}
