package no.dusken.momus.service.remotedocument.sharepoint;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
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
import org.springframework.core.io.ClassPathResource;
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

    @Value("${sharepoint.certfile}")
    private String CERT_FILE;

    @Value("${sharepoint.key}")
    private String KEY_FILE;

    private AuthenticationContext context;

    private PrivateKey getPrivate(File file) throws Exception {
        byte[] keyBytes = Files.readAllBytes(file.toPath());

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private X509Certificate getCertificate(File file) throws Exception {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");

        FileInputStream f = new FileInputStream(file);

        X509Certificate cer = (X509Certificate) fact.generateCertificate(f);

        return cer;
    }

    private AsymmetricKeyCredential getCredentials() throws Exception {
        PrivateKey priv = getPrivate(new ClassPathResource(KEY_FILE).getFile());
        X509Certificate cert = getCertificate(new ClassPathResource(CERT_FILE).getFile());        
        return AsymmetricKeyCredential.create(CLIENT_ID, priv, cert);
    }

    public AuthenticationResult getAccessToken() throws Exception {
        AuthenticationContext context;
        AuthenticationResult result;
        ExecutorService service;
        
        service = Executors.newFixedThreadPool(1);
        context = new AuthenticationContext(AUTHORITY, true, service);
        this.context = context;
        AsymmetricKeyCredential credentials = getCredentials();
        Future<AuthenticationResult> future = context.acquireToken(RESOURCE, credentials, null);
        result = future.get();

        log.info("Sharepoint authentication successful! Token expires at {}", result.getExpiresOnDate());

        return result;
    }

    public AuthenticationResult refreshToken(AuthenticationResult prev) throws Exception {
        log.info("Refreshing Sharepoint access token");
        if(context == null) {
            throw new IllegalStateException("Auth context not initialized. Cannot refresh");
        }

        Future<AuthenticationResult> future = context.acquireTokenByRefreshToken(prev.getRefreshToken(), getCredentials(), null);
        AuthenticationResult res = future.get();
        log.info("Got new token, expires at {}", res.getExpiresOnDate());
        return res;
    }
}