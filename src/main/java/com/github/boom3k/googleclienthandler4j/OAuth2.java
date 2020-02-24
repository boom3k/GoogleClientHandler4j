package com.github.boom3k.googleclienthandler4j;

import boom3k.Zip3k;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.lingala.zip4j.exception.ZipException;

import java.io.*;
import java.util.Map;

public class OAuth2 {
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JacksonFactory JSON_FACTORY = new JacksonFactory();
    static GoogleClientSecrets googleClientSecrets;
    static GoogleCredential.Builder clientBuilder;
    static GoogleClientSecrets.Details details;
    static String ACCESS_TOKEN;
    static String REFRESH_TOKEN;
    static OAuth2 instance;

    private OAuth2() {
    }

    static synchronized public OAuth2 getInstance() {
        if (instance == null) {
            instance = new OAuth2();
        }
        return instance;
    }

    /**
     * @param inputStreamReader inputStreamReader of Files
     */
    public OAuth2 setCredentialsFromStream(InputStreamReader inputStreamReader) {
        try {
            details = GoogleClientSecrets.load(JSON_FACTORY, inputStreamReader).getInstalled();
            googleClientSecrets = new GoogleClientSecrets().setInstalled(details);
            return this;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OAuth2 setCredentialsClientSecrets(String clientSecret, String clientId) {
        details.setClientSecret(clientSecret);
        details.setClientId(clientId);
        googleClientSecrets = new GoogleClientSecrets().setInstalled(details);
        return this;

    }

    /**
     * @param zipFilePath     path to the zip holding the client secrets json file
     * @param zipFilePassword password of zip holding the client secrets json file
     */
    public OAuth2 setCredentialsFromZip(String zipFilePath, String zipFilePassword) {
        try {
            Map<String, InputStream> allZippedFiles = Zip3k.getAllZippedFiles(zipFilePath, zipFilePassword);
            for (String fileName : allZippedFiles.keySet()) {
                if (!fileName.contains(".json")) {
                    continue;
                }
                JsonObject jsonObject = new Gson().fromJson(new InputStreamReader(Zip3k.getAllZippedFiles(zipFilePath, zipFilePassword).get(fileName)), JsonObject.class);
                if (jsonObject.has("installed")) {
                    return setCredentialsFromStream(new InputStreamReader(allZippedFiles.get(fileName)));
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param credentialFilePath path to client secret json
     */
    public OAuth2 setCredentialsFromFilePath(String credentialFilePath) {
        try {
            return setCredentialsFromStream(new FileReader(new File(credentialFilePath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param accessToken  access token from authorization
     * @param refreshToken refresh token from authorization
     * @return
     */
    public OAuth2 setTokens(String accessToken, String refreshToken) {
        this.ACCESS_TOKEN = accessToken;
        this.REFRESH_TOKEN = refreshToken;
        return this;
    }

    /**
     * @return A Google credential object that is built using the Access and Refresh Token
     */
    public static GoogleCredential getClient() {
        if (ACCESS_TOKEN == null || REFRESH_TOKEN == null) {
            System.out.println("Please initialize OAuth2 client with tokens before calling!");
            return null;
        }
        return new GoogleCredential.Builder()
                .setClientSecrets(googleClientSecrets)
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY).build()
                .setAccessToken(ACCESS_TOKEN)
                .setRefreshToken(REFRESH_TOKEN);
    }
}
