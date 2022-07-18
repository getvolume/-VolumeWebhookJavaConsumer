package com.volume.volumewebhookjavaconsumer.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volume.volumewebhookjavaconsumer.config.VersionHeaders;
import com.volume.volumewebhookjavaconsumer.rest.data.PaymentWebhookNotificationDto;
import com.volume.volumewebhookjavaconsumer.services.SignatureService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WebhookController {

    private final SignatureService signatureService;
    private final ObjectMapper objectMapper;

    public WebhookController(SignatureService signatureService, ObjectMapper objectMapper) {
        this.signatureService = signatureService;
        this.objectMapper = objectMapper;
    }

    /**
     * This example method takes body as a byte array and hence it can be easily verified.
     * This is the safest method possible as byte array for sure be the same as the one
     * used while creating the signature in the Volume backend.
     **/
    @PutMapping(value = "/webhookBytes",
        consumes = {VersionHeaders.ContentTypeV0_7, APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> webhookBytes(@RequestBody byte[] body,
                                             @RequestHeader(value = HttpHeaders.AUTHORIZATION) String signature) {
        boolean verify = signatureService.verify(body, signature);
        return verify ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * This example method takes body as a json string hence it can be easily converted to byte array and verified.
     **/
    @PutMapping(value = "/webhookJson",
        consumes = {VersionHeaders.ContentTypeV0_7, APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> webhookJson(@RequestBody String json,
                                            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String signature) {
        boolean verify = signatureService.verify(json, signature);
        return verify ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * This example method takes a deserialized Dto. But developer needs to make sure that when it is converted to JSON
     * it will be the same as the one which was used to generate a signature in the Volume backend.
     * Very often a locally created DTO can introduce changes (f.ex. ignore fields, complex wrapper classes) and then
     * fail validation. A different JSON representation will lead to a  different signature.
     **/
    @PutMapping(value = "/webhookDto",
        consumes = {VersionHeaders.ContentTypeV0_7, APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> webhookPrimitiveDto(@RequestBody PaymentWebhookNotificationDto dto,
                                                    @RequestHeader(value = HttpHeaders.AUTHORIZATION) String signature)
        throws JsonProcessingException {
        var stringValue = objectMapper.writeValueAsString(dto);
        boolean verify = signatureService.verify(stringValue, signature);
        return verify ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
