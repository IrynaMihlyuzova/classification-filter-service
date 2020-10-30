package com.elsevier.entellect.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component("ClassificationCodesLoader")
public class ClassificationCodesLoader {

/*    private final Set<String> classificationCodes = new HashSet<>();

    @Scheduled(cron = "* * * ? * *")
    public void scheduleTaskUsingCronExpression() {
        long now = System.currentTimeMillis() / 1000;
        System.out.println("schedule tasks using cron jobs - " + now);

        AmazonS3 s3Client = ClassificationFilterServiceConfiguration.s3Client();

        String bucketName = "entellect-enrichment-services-mihlyuzovai";
        String key = "classification-codes/classification_codes.properties";

        System.out.println("Downloading an object");

        try (final S3Object fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
        final S3ObjectInputStream inputStream = fullObject.getObjectContent()){

            String propertiesContent = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            propertiesContent = buffer.toString();
            Collections.addAll(classificationCodes, propertiesContent.split(","));

            System.out.println("properties = " + propertiesContent);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public Set<String> getClassificationCodes() {
        return classificationCodes;
    }*/
}
