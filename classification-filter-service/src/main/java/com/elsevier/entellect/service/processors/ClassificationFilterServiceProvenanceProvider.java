package com.elsevier.entellect.service.processors;

import com.elsevier.ces.adapters.jsonld.AbstractProvenanceProvider;
import com.elsevier.ces.rnf2.Provenance;
import com.elsevier.ces.textandannotationsets.provenance.DefaultExternalResourceProvenance;
import com.elsevier.ces.textandannotationsets.provenance.DefaultServiceProvenance;
import com.elsevier.ces.textandannotationsets.provenance.ExternalResourceProvenance;
import com.elsevier.ces.textandannotationsets.provenance.ServiceProvenance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component("ClassificationFilterServiceProvenanceProvider")
public class ClassificationFilterServiceProvenanceProvider extends AbstractProvenanceProvider {

	private String buildNumber;

	private String modelVersion;

	private String serviceName;

	@Autowired
	public ClassificationFilterServiceProvenanceProvider(@Value("${build.number}") String buildNumber,
			@Value("${classification-filter.service.model.version}") final String modelVersion,
			@Value("${service.name}") String serviceName) {
		this.buildNumber = buildNumber;
		this.modelVersion = modelVersion;
		this.serviceName = serviceName;
	}

	protected Provenance generateProvenance() {
		return new Provenance().withServiceVersion(getVersionedServiceUri(serviceName, buildNumber))
			.withExternalResourceVersion(getVersionedModelUri());
	}

	private String getVersionedModelUri() {
		return "http://opennlp.sourceforge.net/models-" + modelVersion + "/en-sent.bin";
	}

	@Override
	public ServiceProvenance getServiceProvenace() {
		return new DefaultServiceProvenance(serviceName, buildNumber);
	}

	@Override
	public List<ExternalResourceProvenance> getExternalResourceProvenance() {
		return Arrays.asList(new DefaultExternalResourceProvenance("http://opennlp.sourceforge.net/models-" + modelVersion + "/en-sent.bin", modelVersion));
	}
}
