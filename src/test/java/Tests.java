import com.github.boom3k.googleclienthandler4j.OAuth2;
import com.github.boom3k.googleclienthandler4j.ServiceAccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import net.lingala.zip4j.exception.ZipException;
import org.junit.Test;

public class Tests {
    final String dummyServiceAccountFilePath = "DUMMY_CREDENTIALS\\DUMMY_serviceAccount.json";
    final String dummyOAuth2FilePath = "DUMMY_CREDENTIALS\\DUMMY_client_secrets.json";
    final String dummyScopesFilePath = "DUMMY_CREDENTIALS\\DUMMY_scopes.txt";
    final String dummyZipPath = "DUMMY_CREDENTIALS\\DUMMY_credentials.zip";
    final String dummyPassword = "p@$$word";
    final String dummyScope = "https://www.googleapis.com/auth/drive";

    @Test
    public void zippedServiceAccountCredentialsTest() throws ZipException {
        GoogleCredential googleCredential = new ServiceAccount(dummyZipPath, dummyPassword)
                .setMultipleScopes(dummyScopesFilePath)
                .getSubjectClient("user@test.com");
        System.out.println(googleCredential.getServiceAccountProjectId());
        System.out.println(googleCredential.getServiceAccountPrivateKeyId());
        System.out.println(googleCredential.getServiceAccountPrivateKey());
        System.out.println(googleCredential.getServiceAccountId());
        System.out.println(googleCredential.getServiceAccountUser());
        System.out.println(googleCredential.getTokenServerEncodedUrl());
        System.out.println(googleCredential.getServiceAccountScopesAsString());
    }

    @Test
    public void serviceAccountClientTest() {
        GoogleCredential googleCredential = new ServiceAccount(dummyServiceAccountFilePath)
                .setSingleScope(dummyScope)
                .getSubjectClient("user@test.com");
        System.out.println(googleCredential.getServiceAccountProjectId());
        System.out.println(googleCredential.getServiceAccountPrivateKeyId());
        System.out.println(googleCredential.getServiceAccountPrivateKey());
        System.out.println(googleCredential.getServiceAccountId());
        System.out.println(googleCredential.getServiceAccountUser());
        System.out.println(googleCredential.getTokenServerEncodedUrl());
        System.out.println(googleCredential.getServiceAccountScopesAsString());
    }

    @Test
    public void oAuth2ClientTest() {
        final GoogleCredential oAuth2 = new OAuth2(dummyOAuth2FilePath).setTokens("OIEJ", "oiafjeij").getClient();
        System.out.println(oAuth2.getAccessToken());
        System.out.println(oAuth2.getRefreshToken());
        System.out.println(oAuth2.getTokenServerEncodedUrl());
    }

    @Test
    public void zippedOAuth2ClientTest(){
        final GoogleCredential oAuth2 = new OAuth2(dummyZipPath, dummyPassword)
                .setTokens("OIEJ", "oiafjeij")
                .getClient();
        System.out.println(oAuth2.getAccessToken());
        System.out.println(oAuth2.getRefreshToken());
        System.out.println(oAuth2.getTokenServerEncodedUrl());
    }

}
