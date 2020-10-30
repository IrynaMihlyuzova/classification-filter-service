package com.elsevier.entellect.service;

import com.elsevier.ces.annotation.domain.model.Structure;
import com.elsevier.ces.rnf2.BaseServiceOutputToRnf2Appender;
import com.elsevier.ces.textandannotationsets.Location;
import com.elsevier.ces.textandannotationsets.MessageWithTextAndAnnotationSets;

import org.apache.camel.Body;
import org.apache.camel.ExchangeProperty;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import static com.elsevier.ces.annotation.domain.model.EntellectAnnotationSetTypeConstants.SENTENCES;

import java.util.List;

@Component("ClassificationFilterServiceToRnf2Appender")
public class ClassificationFilterServiceToRnf2Appender extends
    BaseServiceOutputToRnf2Appender<MessageWithTextAndAnnotationSets> {
    @Handler
    public MessageWithTextAndAnnotationSets append(@Body
				MessageWithTextAndAnnotationSets serviceOutput, @ExchangeProperty("Result")
				MessageWithTextAndAnnotationSets serviceInput) {
        return super.append(serviceOutput, serviceInput, SENTENCES);
    }

    @Override
    protected void appendServiceOutputToDocument(MessageWithTextAndAnnotationSets serviceOutput, MessageWithTextAndAnnotationSets serviceInput) {
        List<Location> sentences = new Structure(serviceOutput).getSentences();
        new Structure(serviceInput).withSentences(sentences);
    }
}
