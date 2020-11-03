package com.elsevier.entellect.service;

import static com.elsevier.ces.adapters.rest.EnrichText.URN_SOURCE_URI_UNKNOWN;
import static com.elsevier.ces.property.keys.SourceDataFormatValues.RNF2;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.io.IOException;
import java.util.HashMap;

import org.apache.camel.Produce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elsevier.ces.adapters.rest.EnrichText;
import com.elsevier.ces.exception.ErrorCodeFatalProcessingException;

@RestController
@Validated
public class ClassificationFilterServiceSynchronousController {

    @Produce(uri = "direct:syncServiceRoute")
    private EnrichText service;

    private static final Logger LOG = LoggerFactory.getLogger(ClassificationFilterServiceSynchronousController.class);

    @PostMapping(path = "/classificationFilter",
            headers=("content-type=multipart/*"),
            consumes = MULTIPART_FORM_DATA_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public String classificationFilterService(@RequestParam("file") MultipartFile inputFile,
                               @RequestParam(value = "correlationId", required = false) String messageCorrelationId) {
        try {
            return service.enrich(inputFile.getBytes(), URN_SOURCE_URI_UNKNOWN, RNF2.getLabel(), RNF2.getLabel(), messageCorrelationId, new HashMap<>());
        } catch (IOException e) {
            String error = format("Unable to read the uploaded file %s", inputFile.getName());
            LOG.error(error);
            throw new ErrorCodeFatalProcessingException(error, e, 404);
        }
    }

}
