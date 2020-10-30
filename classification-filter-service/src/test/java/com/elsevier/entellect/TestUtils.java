package com.elsevier.entellect;

import com.elsevier.cef.common.uri.TextUriHandler;
import com.elsevier.cef.common.uri.UriHandlers;
import com.elsevier.unstructured.ingest.format.conversion.ldf.general.AddNodeRepresentingXmlStructure;
import com.elsevier.unstructured.ingest.format.conversion.ldf.impl.*;
import com.elsevier.unstructured.ingest.format.conversion.ldf.patent.*;
import net.sf.saxon.lib.ExtensionFunctionDefinition;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Function;

import static com.elsevier.unstructured.ingest.format.conversion.ldf.impl.ClasspathFrameSupplier.frameSupplierWithClasspathResource;
import static com.elsevier.unstructured.ingest.format.conversion.ldf.impl.ConversionParameterConstants.*;
import static java.util.Arrays.asList;

public final class TestUtils {

    private TestUtils() {
        // Prevent construction of util class
    }

    public static List<ExtensionFunctionDefinition> patentExtensionFunctions() {
        GraphMutator graphMutator = new GraphMutatorImpl();
        return asList(new AddApplicationRefFunction(graphMutator), new AddNodeRepresentingXmlStructure(graphMutator),
                new CreatePatentEntityFunction(graphMutator), new AddXmlLiteralPropertyFunction(graphMutator),
                new AddClaimsFunction(graphMutator), new AddInventorsFunction(graphMutator),
                new AddAssigneesFunction(graphMutator), new AddIpcClassificationFunction(graphMutator),
                new AddPatentFamilyFunction(graphMutator), new AddPatentFamilyMemberFunction(graphMutator));
    }

    public static LdfConversion createTestLdfConversion() {
        Function<DocIdTransformerParameters, URI> docIdTransformer = docIdTransformerParameters -> {
            String docIdAsString = "http://elsevier.com";
            URI docId = null;
            try {
                docId = new URI(docIdAsString);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(docIdAsString + " is not a valid URI", e);
            }
            String patentIdentifier =
                    docIdTransformerParameters.getTypesafeParameter(String.class, PATENT_IDENTIFIER_PROPERTY);
            if (patentIdentifier == null) {
                return docId;
            }
            if (docId.getPath().endsWith("/")) {
                return docId.resolve(patentIdentifier);
            }
            return docId.resolve("/" + patentIdentifier);
        };

        GraphMutator graphMutator = new GraphMutatorImpl();
        ClasspathFrameSupplier frameSupplier = frameSupplierWithClasspathResource("/frame.json");
        UriHandlers uriHandlers = new UriHandlers(asList(new TextUriHandler()));
        AnnotationConversion annotationConversion = new AnnotationConversion(graphMutator,
                new PatentAnnotationAttributePopulator(graphMutator), docIdTransformer);

        return new LdfConversion(frameSupplier, patentExtensionFunctions(),
                docIdTransformer, uriHandlers, annotationConversion);
    }
}
