package com.elsevier.entellect.service.codesloader;

import com.elsevier.cef.common.uri.UriHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;
@Component
public abstract class AbstractClassificationCodesLoader implements IClassificationCodesLoader{

    private final String bucketName;

    private final String key;

    private UriHandlers uriHandlers;

    private Object lock = new Object();

    public Set<String> classificationCodes;

    @Autowired
    public AbstractClassificationCodesLoader(UriHandlers uriHandlers, String bucketName, String key) {
        this.uriHandlers  = uriHandlers;
        this.bucketName = bucketName;
        this.key  = key;
    }

    @PostConstruct
    @Override
    public void onStartup() {
        reloadClassificationCodes();
    }

    @Scheduled(cron="0 0 6 * * ?")
    @Override
    public void onSchedule() {
        reloadClassificationCodes();
    }

    @Override
    public abstract void reloadClassificationCodes();

    @Override
    public abstract Set<String> getClassificationCodes();

    public String getBucketName() {
        return bucketName;
    }

    public String getKey() {
        return key;
    }

    public UriHandlers getUriHandlers() {
        return uriHandlers;
    }

    public void setUriHandlers(UriHandlers uriHandlers) {
        this.uriHandlers = uriHandlers;
    }

    public Object getLock() {
        return lock;
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

}
