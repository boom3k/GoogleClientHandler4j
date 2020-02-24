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
    static GoogleCredential.Builder clientBuilder = new GoogleCredential.Builder();
    static GoogleCredential credential = new GoogleCredential();
    static String userName = "NULL";
    static List<String> scopes = new ArrayList<>();
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
    public ServiceAccount setCredentialsFromInputStream(InputStream inputStream) {
        try {
            credential = GoogleCredential.fromStream(inputStream);
            clientBuilder = credential.toBuilder();
            return this;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param credentialFilePath path to service account json
     * @return this
     */
    public ServiceAccount setCredentialsFromPath(String credentialFilePath) {
        try {
            return setCredentialsFromInputStream(new FileInputStream(new File(credentialFilePath)));
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
    public ServiceAccount setCredentialsFromZip(String zipFilePath, String zipPassword) {
        try {
            Map<String, InputStream> allZippedFiles = Zip3k.getAllZippedFiles(zipFilePath, zipPassword);
            for (String fileName : allZippedFiles.keySet()) {
                if (!fileName.contains(".json")) {
                    continue;
                }
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(new InputStreamReader(Zip3k.getAllZippedFiles(zipFilePath, zipPassword).get(fileName)));
                if (jsonObject.has("private_key")) {
                    return setCredentialsFromInputStream(allZippedFiles.get(fileName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * @param newScopes list of scopes used for this account
     * @return this
     */
    public ServiceAccount setScopes(List<String> newScopes) {
        scopes = newScopes;
        clientBuilder.setServiceAccountScopes(scopes);
        return this;
    }

    /**
     * @param scope
     */
    public ServiceAccount setScopes(String scope) {
        List<String> scopes = new ArrayList<>();
        scopes.add(scope);
        setScopes(scopes);
        return this;
    }

    /**
     * @param scopesJsonFile path to a json file with comma separated scopes
     * @return this
     */
    public ServiceAccount setScopes(File scopesJsonFile) {
        try {
            List<String> SCOPES = new ArrayList<>();
            String line = new String(Files.readAllBytes(Paths.get(scopesJsonFile.getAbsolutePath())));
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
        return this;
    }

    /**
     * @param newUserName for subject to act as
     * @return Returns a Google Credential acting as the user provided
     */
    public static GoogleCredential getClient(String newUserName) {
        if (newUserName != userName) {
            System.out.println("ServiceAccountUser is ->(" + newUserName + "), was ->(" + userName + ")");
            userName = newUserName;
        }
        return clientBuilder.setServiceAccountUser(userName).build();
    }

    public static GoogleCredential.Builder getClientBuilder() {
        return clientBuilder;
    }

    public static GoogleCredential getCredential() {
        return credential;
    }

    public static String getUserName() {
        return userName;
    }

    public static List<String> getScopes() {
        return scopes;
    }

}
