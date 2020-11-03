package com.elsevier.entellect.service.filter;

import com.elsevier.entellect.service.codesloader.BibliographyCodesLoader;
import org.apache.camel.Body;
import org.apache.camel.ExchangeProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component("BibliographyFilter")
public class BibliographyFilter extends AbstractNotificationFilter {

    private BibliographyCodesLoader bibliographyCodesLoader;

    @Autowired
    public BibliographyFilter(BibliographyCodesLoader bibliographyCodesLoader){
        this.bibliographyCodesLoader = bibliographyCodesLoader;
    }

    public void filter(@Body String body, @ExchangeProperty("parameters") Map<String,Object> parameters) throws IOException {
        setClassificationCodesLoader(bibliographyCodesLoader);
        parameters.put("filter", filterByClassification(body));
    }

}


