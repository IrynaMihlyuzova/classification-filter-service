package com.elsevier.entellect.service.filter;

import com.elsevier.entellect.service.codesloader.DataScienceCodesLoader;
import org.apache.camel.Body;
import org.apache.camel.ExchangeProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component("DataScienceFilter")
public class DataScienceFilter extends AbstractNotificationFilter {

    private DataScienceCodesLoader dataScienceCodesLoader;

    @Autowired
    public DataScienceFilter(DataScienceCodesLoader dataScienceCodesLoader) {
        this.dataScienceCodesLoader = dataScienceCodesLoader;
    }

    public void filter(@Body String body, @ExchangeProperty("parameters") Map<String,Object> parameters) throws IOException {
        setClassificationCodesLoader(dataScienceCodesLoader);
        parameters.put("filter", filterByClassification(body));
    }
}
