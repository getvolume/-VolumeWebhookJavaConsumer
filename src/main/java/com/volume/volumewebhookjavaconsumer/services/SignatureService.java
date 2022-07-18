package com.volume.volumewebhookjavaconsumer.services;


import com.volume.volumewebhookjavaconsumer.config.ApplicationProperties;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

/*
This service is solely responsible for fetching current public key from Volume domain and verifying
if a signature string corresponds to some data. There are two possibilities of passing the data
 - as byte array
 - as a json string
As the signature in Volumes backend is made by using the json string this is also a recommended method to choose.
Providing a modified string (or different bytes), other than the one passed in the request body will not pass verification.
*/
@Service
public class SignatureService {

    private final RestTemplate restTemplate;
    private final ApplicationProperties applicationProperties;
    private PublicKey publicKey;

    public SignatureService(RestTemplate restTemplate,
                            ApplicationProperties applicationProperties) {
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    private void init() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var volumePublicKey = fetchVolumePublicKey(applicationProperties.pemUrl());
        publicKey = KeyFactory.getInstance("RSA")
            .generatePublic(
                new X509EncodedKeySpec(
                    Base64.getMimeDecoder().decode(volumePublicKey)
                )
            );
    }

    public boolean verify(String jsonString, String signature) {
        var bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        return verify(bytes, signature);
    }

    public boolean verify(byte[] data, String signature) {
        if (ObjectUtils.isEmpty(signature)) {
            return false;
        }

        var signatureElements = signature.split(" ");
        if (signatureElements.length != 2) {
            return false;
        }

        try {
            var publicSignature = Signature.getInstance(signatureElements[0]);
            publicSignature.initVerify(publicKey);

            var signatureBytes = Base64.getDecoder().decode(signatureElements[1]);
            publicSignature.update(data);
            if (!publicSignature.verify(signatureBytes)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private String fetchVolumePublicKey(String pemUrl) {
        return restTemplate.getForObject(pemUrl, String.class);
    }
}