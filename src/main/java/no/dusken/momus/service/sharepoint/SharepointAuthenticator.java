package no.dusken.momus.service.sharepoint;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.microsoft.aad.adal4j.AsymmetricKeyCredential;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SharepointAuthenticator {


    @Value("${sharepoint.authority}")
    private String AUTHORITY;

    @Value("${sharepoint.clientid}")
    private String CLIENT_ID;

    @Value("${sharepoint.resource}")
    private String RESOURCE;

    private PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private X509Certificate getCertificate(String filename) throws Exception {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");

        FileInputStream f = new FileInputStream(new File(filename));

        X509Certificate cer = (X509Certificate) fact.generateCertificate(f);

        return cer;
    }

    private AsymmetricKeyCredential getCredentials() throws Exception {
        PrivateKey priv = getPrivate("/home/egrimstad/studentmediene/Momus/src/main/resources/key.der");
        X509Certificate cert = getCertificate("/home/egrimstad/studentmediene/Momus/src/main/resources/cert.pem");        
        return AsymmetricKeyCredential.create(CLIENT_ID, priv, cert);
    }

    public AuthenticationResult getAccessToken() throws Exception {
        AuthenticationContext context;
        AuthenticationResult result;
        ExecutorService service;
        
        service = Executors.newFixedThreadPool(1);
        context = new AuthenticationContext(AUTHORITY, true, service);
        AsymmetricKeyCredential credentials = getCredentials();
        Future<AuthenticationResult> future = context.acquireToken(RESOURCE, credentials, null);
        result = future.get();

        log.info("SharePoint authentication successful!");

        return result;
    }
}