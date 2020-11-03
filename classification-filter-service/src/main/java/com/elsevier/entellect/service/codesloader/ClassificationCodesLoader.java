package com.elsevier.entellect.service.codesloader;

import com.elsevier.cef.common.uri.UriHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.addAll;

@Component("ClassificationCodesLoader")
public class ClassificationCodesLoader {

    private final String bucketName;

    private final String key;

    private Set<String> classificationCodes;

    private UriHandlers uriHandlers;

    private Object lock = new Object();

    @PostConstruct
    public void onStartup() {
        reloadClassificationCodes();
    }
    //TODO this property could be move to application.properties file
    @Scheduled(cron="0 0 6 * * ?")
    public void onSchedule() {
        reloadClassificationCodes();
    }

    @Autowired
    public ClassificationCodesLoader(UriHandlers uriHandlers, @Value("entellect-enrichment-services-mihlyuzovai") String bucketName, @Value("bibliography-classification-codes/classification_codes.properties") String key) {
        this.uriHandlers  = uriHandlers;
        this.bucketName = bucketName;
        this.key  = key;
    }

    private void reloadClassificationCodes() {
        String classificationCodesReferenceData = uriHandlers.readUriAsString(format("s3://%s/%s", bucketName, key));

        String classificationCodesReferenceDataWithoutSpace = classificationCodesReferenceData.replaceAll("\\s+","");
        Set classificationCodes = new HashSet<>();
        addAll(classificationCodes, classificationCodesReferenceDataWithoutSpace.split(","));

        System.out.println("Downloading an object = " +classificationCodesReferenceDataWithoutSpace);

        synchronized(lock) {
            this.classificationCodes = classificationCodes;
        }
    }

    public Set<String> getClassificationCodes() {
        synchronized(lock) {
            return classificationCodes;
        }
    }

}
