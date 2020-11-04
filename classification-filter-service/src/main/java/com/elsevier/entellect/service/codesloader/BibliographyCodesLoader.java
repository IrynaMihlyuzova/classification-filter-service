package com.elsevier.entellect.service.codesloader;

import com.elsevier.cef.common.uri.UriHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.addAll;

@Component
public class BibliographyCodesLoader extends AbstractClassificationCodesLoader {

    public static final Logger LOG = LoggerFactory.getLogger(BibliographyCodesLoader.class);

    public BibliographyCodesLoader(UriHandlers uriHandlers, @Value("${bucket.name}") String bucketName,
                                   @Value("${bibliographic.key.name}") String key) {
        super(uriHandlers, bucketName, key);
    }

    @Override
    public void reloadClassificationCodes() {
        String classificationCodesReferenceData = getUriHandlers().readUriAsString(format("s3://%s/%s", getBucketName(), getKey()));

        String classificationCodesReferenceDataWithoutSpace = classificationCodesReferenceData.replaceAll("\\s+","");
        Set<String> classificationCodes = new HashSet<>();
        addAll(classificationCodes, classificationCodesReferenceDataWithoutSpace.split(","));

        LOG.info("Bibliographic codes were reloaded = " + classificationCodesReferenceDataWithoutSpace);

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

}
