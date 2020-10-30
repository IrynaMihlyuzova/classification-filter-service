package com.elsevier.entellect.service;

import com.elsevier.ces.adapters.jsonld.ProvenanceApplier;
import org.apache.camel.Processor;

/**
 * Responsibility: Carry provenance configuration into route configuration methods.
 */
public class ProvenanceConfiguration {

    private Processor provenanceProvider;

    private ProvenanceApplier provenanceApplier;

    public ProvenanceConfiguration(Processor provenanceProvider, ProvenanceApplier provenanceApplier) {
        super();
        this.provenanceProvider = provenanceProvider;
        this.provenanceApplier = provenanceApplier;
    }

    public Processor getProvenanceProvider() {
        return provenanceProvider;
    }

    public ProvenanceApplier getProvenanceApplier() {
        return provenanceApplier;
    }
}
