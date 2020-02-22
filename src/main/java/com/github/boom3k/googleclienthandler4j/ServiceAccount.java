package com.github.boom3k.googleclienthandler4j;

import boom3k.Zip3k;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lingala.zip4j.exception.ZipException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceAccount {

    static GoogleCredential.Builder clientBuilder;

    /**
     * @param inputStream InputStream of a ServiceAccount json file
     */
    public ServiceAccount(InputStream inputStream) {
        try {
            clientBuilder = GoogleCredential.fromStream(inputStream).toBuilder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param credentialFilePath path to service account json
     */
    public ServiceAccount(String credentialFilePath) {
        try {
            new ServiceAccount(new FileInputStream(new File(credentialFilePath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method uses the Zip3k library from https://www.github.com/boom3k/zip3k
     *
     * @param zipFilePath path to zipped credentials
     * @param zipPassword password to zipped credentials
     */
    public ServiceAccount(String zipFilePath, String zipPassword) throws ZipException {
        Map<String, InputStream> allZippedFiles = Zip3k.getAllZippedFiles(zipFilePath, zipPassword);
        for (String fileName : allZippedFiles.keySet()) {
            if (!fileName.contains(".json")) {
                continue;
            }
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(new InputStreamReader(Zip3k.getAllZippedFiles(zipFilePath, zipPassword).get(fileName)));
            if (jsonObject.has("private_key")) {
                new ServiceAccount(allZippedFiles.get(fileName));
            }
        }
    }

    /**
     * @param scopes list of scopes used for this account
     * @return this
     */
    public ServiceAccount setMultipleScopes(List<String> scopes) {
        this.clientBuilder.setServiceAccountScopes(scopes);
        return this;
    }

    public ServiceAccount setSingleScope(String scope){
        List<String> scopes = new ArrayList<>();
        scopes.add(scope);
        setMultipleScopes(scopes);
        return this;
    }

    /**
     * @param ScopesFilePath path to a file with comma separated scopes
     * @return this
     */
    public ServiceAccount setMultipleScopes(String ScopesFilePath) {
        try {
            List<String> SCOPES = new ArrayList<>();
            String line = new String(Files.readAllBytes(Paths.get(ScopesFilePath)));
            for (String scope : line.split(",\r\n")) {
                SCOPES.add(scope);
            }
            return this.setMultipleScopes(SCOPES);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param userName username for subject to act as
     * @return Returns a Google Credential acting as the user provided
     */
    public static GoogleCredential getSubjectClient(String userName) {
        return clientBuilder.setServiceAccountUser(userName).build();
    }

}
