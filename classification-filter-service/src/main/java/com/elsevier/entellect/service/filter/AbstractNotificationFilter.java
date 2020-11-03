package com.elsevier.entellect.service.filter;

import com.elsevier.entellect.service.codesloader.IClassificationCodesLoader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.camel.*;

import java.io.IOException;
import java.util.*;

import static org.springframework.util.CollectionUtils.containsAny;

public abstract class AbstractNotificationFilter {

    public static final String JSON_CLASSIFICATION_FIELD = "patent_classification";

    private IClassificationCodesLoader classificationCodesLoader;

    public void setClassificationCodesLoader(IClassificationCodesLoader classificationCodesLoader) {
        this.classificationCodesLoader = classificationCodesLoader;
    }

    public abstract void filter(@Body String body, @ExchangeProperty("parameters") Map<String,Object> parameters) throws IOException ;

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
