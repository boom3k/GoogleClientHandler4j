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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OAuth2 {
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JacksonFactory JSON_FACTORY = new JacksonFactory();
    static GoogleCredential.Builder clientBuilder;
    static String ACCESS_TOKEN;
    static String REFRESH_TOKEN;

    public OAuth2(String clientSecret, String clientId, boolean n) {
        /*GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientSecret(clientSecret);
        details.setClientId(clientId);
        details.setTokenUri("https://oauth2.googleapis.com/token");
        details.setAuthUri("https://accounts.google.com/o/oauth2/auth");
        List<String> redirectUris = new ArrayList<>();
        redirectUris.add("urn:ietf:wg:oauth:2.0:oob");
        redirectUris.add("http://localhost");
        details.setRedirectUris(redirectUris);
        GoogleClientSecrets googleClientSecrets = new GoogleClientSecrets();
        googleClientSecrets.setInstalled(details);*/
        clientBuilder = new GoogleCredential.Builder()
                .setClientSecrets(clientSecret, clientId)
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY);

    }

    /**
     * @param zipFilePath     path to the zip holding the client secrets json file
     * @param zipFilePassword password of zip holding the client secrets json file
     */
    public OAuth2(String zipFilePath, String zipFilePassword) {
        try {
            Map<String, InputStream> allZippedFiles = Zip3k.getAllZippedFiles(zipFilePath, zipFilePassword);
            for (String fileName : allZippedFiles.keySet()) {
                if (!fileName.contains(".json")) {
                    continue;
                }
                JsonObject jsonObject = new Gson().fromJson(new InputStreamReader(Zip3k.getAllZippedFiles(zipFilePath, zipFilePassword).get(fileName)), JsonObject.class);
                if (jsonObject.has("installed")) {
                    new OAuth2(new InputStreamReader(allZippedFiles.get(fileName)));
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param zipFileReader inputStreamReader of Files
     */
    public OAuth2(InputStreamReader zipFileReader) {
        try {
            GoogleClientSecrets.Details details = GoogleClientSecrets.load(JSON_FACTORY, zipFileReader).getInstalled();
            new OAuth2(details.getClientSecret(), details.getClientId(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param filePath path to client secrets file
     */
    public OAuth2(File filePath) {
        try {
            new OAuth2(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param credentialFilePath path to client secret json
     */
    public OAuth2(String credentialFilePath) {
        new OAuth2(new File((credentialFilePath)));
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
        return clientBuilder.build()
                .setAccessToken(ACCESS_TOKEN)
                .setRefreshToken(REFRESH_TOKEN);
    }
}
