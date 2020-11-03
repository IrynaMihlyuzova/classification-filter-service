package com.elsevier.entellect.service.codesloader;

import com.elsevier.cef.common.uri.UriHandlers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.addAll;

@Component
public class DataScienceCodesLoader extends AbstractClassificationCodesLoader {

    public DataScienceCodesLoader(UriHandlers uriHandlers, @Value("entellect-enrichment-services-mihlyuzovai") String bucketName,
                                   @Value("datascience-classification-codes/classification_codes.properties") String key) {
        super(uriHandlers, bucketName, key);
    }

    @Override
    public void reloadClassificationCodes() {
        String classificationCodesReferenceData = getUriHandlers().readUriAsString(format("s3://%s/%s", getBucketName(), getKey()));

        String classificationCodesReferenceDataWithoutSpace = classificationCodesReferenceData.replaceAll("\\s+","");
        Set<String> classificationCodes = new HashSet<>();
        addAll(classificationCodes, classificationCodesReferenceDataWithoutSpace.split(","));
// TODO add log
        System.out.println("Downloading DC an object = " +classificationCodesReferenceDataWithoutSpace);

        synchronized(getLock()) {
            this.classificationCodes = classificationCodes;
        }
    }

    @Override
    public Set<String> getClassificationCodes() {
        synchronized(getLock()) {
            return classificationCodes;
        }
    }

/*    private Set<String> classificationCodes;

    public DataScienceCodesLoader(UriHandlers uriHandlers, @Value("entellect-enrichment-services-mihlyuzovai") String bucketName,
                                  @Value("datascience-classification-codes/classification_codes.properties") String key) {
        super(uriHandlers, bucketName, key);
    }

    @Override
    public void reloadClassificationCodes() {
        String classificationCodesReferenceData = getUriHandlers().readUriAsString(format("s3://%s/%s", getBucketName(), getKey()));

        String classificationCodesReferenceDataWithoutSpace = classificationCodesReferenceData.replaceAll("\\s+","");
        Set classificationCodes = new HashSet<>();
        addAll(classificationCodes, classificationCodesReferenceDataWithoutSpace.split(","));

        System.out.println("Downloading DC an object = " +classificationCodesReferenceDataWithoutSpace);

        synchronized(getLock()) {
            this.classificationCodes = classificationCodes;
        }
    }

    @Override
    public Set<String> getClassificationCodes() {
        synchronized(getLock()) {
            return classificationCodes;
        }
    }*/
}
