package com.elsevier.entellect.service.processors;


import com.elsevier.entellect.service.codesloader.ClassificationCodesLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeProperty;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static org.springframework.util.CollectionUtils.containsAny;

@Component("NotificationFilterProcessor")
public class NotificationFilterProcessor implements Processor {

    public static final String JSON_CLASSIFICATION_FIELD = "patent_classification";

    private ClassificationCodesLoader classificationCodesLoader;

    public NotificationFilterProcessor(ClassificationCodesLoader classificationCodesLoader) {
        this.classificationCodesLoader = classificationCodesLoader;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
/*        String notificationAsString = exchange.getIn().getBody(String.class);
        Map<String,Object> parameters = exchange.getProperty(ExchangePropertyKey.PARAMETERS.getName(), Map.class);
        parameters.put("filter",filterByClassification(notificationAsString));*/
    }

    public void process(String body, @ExchangeProperty("parameters") Map<String,Object> parameters) throws IOException {
        parameters.put("filter", filterByClassification(body));
    }

    public boolean filterByClassification(String notificationAsString) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeClassification = objectMapper.readTree(notificationAsString);
        ArrayNode jsonNodes = (ArrayNode) jsonNodeClassification.get(JSON_CLASSIFICATION_FIELD);

        Set<String> subjectList = new HashSet<>();
        jsonNodes.forEach(jsonNode -> subjectList.add(jsonNode.asText().substring(0,4)));

        Set<String> classificationCodes = classificationCodesLoader.getClassificationCodes();

        return containsAny(subjectList, classificationCodes);

    }
}
