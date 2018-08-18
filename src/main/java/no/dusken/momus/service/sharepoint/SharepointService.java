package no.dusken.momus.service.sharepoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import com.microsoft.aad.adal4j.AsymmetricKeyCredential;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SharepointService {

    private final static String AUTHORITY = "https://login.microsoftonline.com/studentmediene.onmicrosoft.com";
    private final static String CLIENT_ID = "a5e54487-6604-4d06-8085-1898b53aebdb";
    private final static String RESOURCE = "https://studentmediene.onmicrosoft.com/595e473b-6688-4fe5-a2dc-24506734f207";

    private static PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private static X509Certificate getCertificate(String filename) throws Exception {    
        CertificateFactory fact = CertificateFactory.getInstance("X.509");

        FileInputStream f = new FileInputStream(new File(filename));

        X509Certificate cer = (X509Certificate) fact.generateCertificate(f);

        return cer;
    }

    private static AsymmetricKeyCredential getCredentials() throws Exception {
        PrivateKey priv = getPrivate("/home/egrimstad/studentmediene/Momus/src/main/resources/key.der");
        X509Certificate cert = getCertificate("/home/egrimstad/studentmediene/Momus/src/main/resources/cert.pem");        
        return AsymmetricKeyCredential.create(CLIENT_ID, priv, cert);
    }

    @PostConstruct
    public void setup() {
        log.info("Setting up Sharepoint integration");

        AuthenticationContext context;
        AuthenticationResult result;
        ExecutorService service;
        
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(AUTHORITY, true, service);
            AsymmetricKeyCredential credentials = getCredentials();
            Future<AuthenticationResult> future = context.acquireToken(RESOURCE, credentials, null);

            result = future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            URL url = new URL("https://studentmediene.sharepoint.com/site/_api/web/lists");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + result.getAccessToken());

            System.out.println(con.getRepon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}