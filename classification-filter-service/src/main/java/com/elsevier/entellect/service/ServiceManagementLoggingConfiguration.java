package com.elsevier.entellect.service;

import org.apache.camel.Processor;

/**
 * Responsibility: Carry service management logging configuration into route configuration methods.
 */
public class ServiceManagementLoggingConfiguration {

    private Processor startTimeSetter;

    private Processor endTimeSetter;

    public ServiceManagementLoggingConfiguration(final Processor startTimeSetter, final Processor endTimeSetter) {
        this.startTimeSetter = startTimeSetter;
        this.endTimeSetter = endTimeSetter;
    }

    public Processor getStartTimeSetter() {
        return startTimeSetter;
    }

    public Processor getEndTimeSetter() {
        return endTimeSetter;
    }
}
