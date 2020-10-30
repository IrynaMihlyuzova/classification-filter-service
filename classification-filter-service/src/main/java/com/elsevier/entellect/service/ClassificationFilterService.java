package com.elsevier.entellect.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.elsevier.entellect.service.entity.MessageEvent;
import com.elsevier.entellect.service.entity.NotificationJson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elsevier.ces.textandannotationsets.Annotation;
import com.elsevier.ces.textandannotationsets.EnrichService;
import com.elsevier.ces.textandannotationsets.MessageWithTextAndAnnotationSets;

/**
 * {@link EnrichService} implementation for the service.
 */
@Service("ClassificationFilterService")
public class ClassificationFilterService {
	
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

/*	public MessageWithTextAndAnnotationSets enrich(MessageWithTextAndAnnotationSets input, Map<String, Object> parameters) {


		final String annotationSetName =
				ObjectUtils.defaultIfNull((String)Optional.ofNullable(parameters)
												  		  .orElse(Collections.emptyMap())
												  		  .get(PARAMETERS_KEY_FOR_ANNOTATION_SET_NAME), ENTITIES_ANNOTATION_SET_NAME);

		final MessageWithTextAndAnnotationSets enriched =
				MessageWithTextAndAnnotationSets.fromAnnotationSets(input.getAnnotationSets());
		final List<Annotation> wrapper = enriched.createNewAnnotationSetIfAbsent(annotationSetName);

		if (!wrapper.isEmpty()) {

			MessageWithTextAndAnnotationSets enrichedMessage = null;

			// TODO Populate service logic and set enrichedMessage object

			Optional.ofNullable(enrichedMessage.getUnmodifiableAnnotationSet(annotationSetName)).ifPresent(wrapper::addAll);
		}

		return enriched;
	}*/

	public MessageEvent notificationParser(MessageWithTextAndAnnotationSets input){
		NotificationJson notificationJson = NotificationJson.fromJSON(input.getText());
		return notificationJson.getMessageEvent();
	}
}
