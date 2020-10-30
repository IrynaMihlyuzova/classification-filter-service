package com.elsevier.entellect.service.processors;

import com.elsevier.ces.property.keys.ExchangePropertyKey;
import com.elsevier.entellect.service.ClassificationFilterServiceConfiguration;
import com.elsevier.entellect.service.entity.NotificationJson;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("NotificationFilterProcessor")
public class NotificationFilterProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.setProperty(ExchangePropertyKey.MESSAGE_CORRELATION_ID.getLabel(), exchange.getIn().getMessageId());
    }

    public boolean filterByClassification(Exchange exchange) throws Exception {
        NotificationJson notificationJson = getNotification(exchange);
        List<String> classificationList = notificationJson.getMessageEvent().getEvtDetails().get(0).getPatentClassification();
        Set<String> subjectList = new HashSet<>();
        classificationList.forEach(classification -> subjectList.add(classification.substring(0,4)));

        Set<String> classificationCodes = ClassificationFilterServiceConfiguration.getClassificationCodes();
        for (String code:classificationCodes){
            if(subjectList.contains(code)){
                return true;
            }
        }
        return false;

    }

    public NotificationJson getNotification(Exchange exchange) throws Exception {
        String bodyIn = exchange.getIn().getBody(String.class);
        return NotificationJson.fromJSON(bodyIn);
    }
}
