package com.elsevier.entellect.service;

import java.util.*;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elsevier.ces.textandannotationsets.Annotation;
import com.elsevier.ces.textandannotationsets.EnrichService;
import com.elsevier.ces.textandannotationsets.MessageWithTextAndAnnotationSets;

/**
 * {@link EnrichService} implementation for the service.
 */
@Service("ClassificationFilterService")
public class ClassificationFilterService implements EnrichService {
	
	private static final String ENTITIES_ANNOTATION_SET_NAME = "entities";
	    
	private static final String PARAMETERS_KEY_FOR_ANNOTATION_SET_NAME = "annotationSet";
	
	@Autowired
	public ClassificationFilterService() {
		// TODO populate the dependencies for the service
	}

	/**
	 * @param input Input {@link MessageWithTextAndAnnotationSets document}
	 * @param parameters {@link Map} of additional parameters required for enrichment.
	 * @return the enriched {@link MessageWithTextAndAnnotationSets}
	 */

	@Override
	public MessageWithTextAndAnnotationSets enrich(MessageWithTextAndAnnotationSets input, Map<String, Object> parameters) {


		final String annotationSetName =
				ObjectUtils.defaultIfNull((String)Optional.ofNullable(parameters)
												  		  .orElse(Collections.emptyMap())
												  		  .get(PARAMETERS_KEY_FOR_ANNOTATION_SET_NAME), ENTITIES_ANNOTATION_SET_NAME);

/*		final MessageWithTextAndAnnotationSets enriched =
				MessageWithTextAndAnnotationSets.fromAnnotationSets(input.getAnnotationSets());
		final List<Annotation> wrapper = enriched.createNewAnnotationSetIfAbsent(annotationSetName);*/

		final List<Annotation> wrapper = new ArrayList<>();

		if (!wrapper.isEmpty()) {

			MessageWithTextAndAnnotationSets enrichedMessage = null;

			// TODO Populate service logic and set enrichedMessage object

			Optional.ofNullable(enrichedMessage.getUnmodifiableAnnotationSet(annotationSetName)).ifPresent(wrapper::addAll);
		}

		return new MessageWithTextAndAnnotationSets();

	}
}
