package com.elsevier.entellect.service.route;

import static com.elsevier.cef.common.utils.JsonUtils.toJson;
import static com.elsevier.entellect.TestUtils.createTestLdfConversion;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static org.apache.commons.io.IOUtils.toInputStream;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.elsevier.ces.adapters.jsonld.ProvenanceApplier;
import com.elsevier.ces.logging.EndTimeSetter;
import com.elsevier.ces.logging.StartTimeSetter;
import com.elsevier.entellect.service.*;
import com.elsevier.entellect.service.processors.ClassificationFilterServiceProvenanceProvider;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.sqs.AmazonSQS;
import com.elsevier.cef.common.uri.UriHandler;
import com.elsevier.cef.common.uri.UriHandlers;
import com.elsevier.ces.adapters.claimcheck.ClaimGranter;
import com.elsevier.ces.adapters.claimcheck.ClaimAsByteArrayClaimRetriever;
import com.elsevier.ces.adapters.claimcheck.SqsQueueNameToCamelEndpointUri;
import com.elsevier.ces.claimcheck.ClaimChecks;
import com.elsevier.ces.impl.DefaultNonBlockingServiceClient;
import com.elsevier.ces.rnf2.Provenance;
import com.elsevier.ces.rnf2.ServicePayload;
import com.elsevier.ces.route.test.AdviceWithCamelTestSupport;
import com.elsevier.ces.textandannotationsets.MessageWithTextAndAnnotationSets;
import com.elsevier.ces.uri.local.server.LocalServerUriTransformers;
import com.elsevier.smd.issuetracing.Issue;
import com.elsevier.unstructured.ingest.format.conversion.api.InputAdapter;
import com.elsevier.unstructured.ingest.format.conversion.api.OutputAdapter;
import com.elsevier.unstructured.ingest.format.conversion.tsv.configuration.FormatConversionTsvConfiguration;
import com.elsevier.unstructured.ingest.format.conversion.ldf.configuration.FormatConversionLdfConfiguration;
import com.elsevier.unstructured.ingest.format.conversion.ldf.impl.LdfConversion;

/**
 * Responsibility: Tests service route configuration to ensure that service management logging and other features are
 * present.
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceRouteTest extends AdviceWithCamelTestSupport {
	private static final String MOCK_DIRECT_SENTENCE_SERVICE_QUEUE = "direct:service-queue";
	public static final String DOCUMENT_TO_ENRICH_URI = "s3://service/input.json";
	public static final String UPLOAD_ENRICH_DOC_URI = "s3://service/output.json";

	@Produce(uri = MOCK_DIRECT_SENTENCE_SERVICE_QUEUE)
	protected ProducerTemplate producerTemplate;

	@Mock
	private UriHandler mockUriHandler;

	private UriHandlers uriHandlers;

	@Mock
	private AmazonSQS amazonSQS;

	private ClaimChecks claimChecks;

	@Before
    public void setup() throws Exception {
        // Mock the route under test to replace the "from" sqs queue with the Producer defined above
/*        AdviceWithRouteBuilder mockSentenceServiceRoute = new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith(MOCK_DIRECT_SENTENCE_SERVICE_QUEUE);
            }
        };
*/
		ServiceManagementLoggingConfiguration serviceManagementLoggingConfiguration = new ServiceManagementLoggingConfiguration(
				new StartTimeSetter(), new EndTimeSetter());

		ClassificationFilterServiceProvenanceProvider provenanceProvider = new ClassificationFilterServiceProvenanceProvider("0acc34-a22132-356aec-6743bd", "1.5",
				"entellect-classification-filter-service");

		ProvenanceConfiguration provenanceConfiguration = new ProvenanceConfiguration(provenanceProvider, new ProvenanceApplier(null));

		context.addRoutes(new ClassificationFilterServiceConfiguration().serviceInvokerRoute(
				serviceManagementLoggingConfiguration, provenanceConfiguration));
/*
        claimChecks = new ClaimChecks(asList(new SqsClaimCheckNotificationReceiver(amazonSQS)), asList(new SqsClaimCheckNotificationSender(amazonSQS)));
        
        context.addRoutes(new ExceptionHandlerRoute().exceptionHandlingRoute(claimGranter(), new ExceptionHandler(), new HydraJsonLdMarshaller()));

        context.getRouteDefinition(ASYNC_SERVICE_ROUTE).adviceWith(context, mockSentenceServiceRoute);*/
    }

	@Issue("ENTELLECT-TODO") // TODO Update Ticket Number
	@Test
	@Ignore("A RNF2 converter is not yet available")
	public void shouldUploadTheDocumentWithSentenceInformationToTheTargetUriWhenARequestArrives() throws Exception {
		context.start();

		givenUriHandlerHandles(DOCUMENT_TO_ENRICH_URI, UPLOAD_ENRICH_DOC_URI);
		givenDocumentToEnrichIs("SimpleDoc.docx");

//		whenEnrichRequestIsSentToSentenceServiceRouteWith(DOCUMENT_TO_ENRICH_URI, UPLOAD_ENRICH_DOC_URI);

		thenTheResultIs();

		context.stop();
	}

	@Override
    protected RouteBuilder createRouteBuilder() {
        // CamelTestSupport initialises the routes even before
        // the mocks are initialised. Hence the explicit initMocks
        // here and the initialisation of uriHandlers
        MockitoAnnotations.initMocks(this);
        uriHandlers = new UriHandlers(asList(mockUriHandler));

        return new ClassificationFilterServiceConfiguration().asynchronousServiceRoute("mySqsQueue",0,
                new ClaimAsByteArrayClaimRetriever(uriHandlers),
                claimGranter(),
                new SqsQueueNameToCamelEndpointUri("1234"),
                true
        );
    }

	private void givenUriHandlerHandles(String... uris) {
		stream(uris).forEach(uri -> given(mockUriHandler.handles(uri)).willReturn(true));
	}

/*	private void whenEnrichRequestIsSentToSentenceServiceRouteWith(String docToEnrichUri, String uploadEnrichToDocUri)
			throws Exception {
		String enrichRequest = toJson(new EnrichRequest(docToEnrichUri, uploadEnrichToDocUri, null, null, emptyMap()));
		producerTemplate.requestBody(MOCK_DIRECT_SENTENCE_SERVICE_QUEUE, enrichRequest);
	}*/

	private void thenTheResultIs() throws Exception {
		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
		verify(mockUriHandler).writeFromString(anyString(), argumentCaptor.capture());
		ServicePayload output = ServicePayload.fromJsonString(argumentCaptor.getValue());
		MessageWithTextAndAnnotationSets parent = output.getPayload();

		Provenance actualProvenance = output.getProvenance().get(0);
		// TODO Assert some entities in the output here
		assertEquals("http://elsevier.com/service/test/1234", actualProvenance.getServiceVersion());
		assertEquals(asList("http://elsevier.com/model/test/2345"), actualProvenance.getExternalResourceVersion());
	}

	private void givenDocumentToEnrichIs(String fileName) {
		MessageWithTextAndAnnotationSets result = new MessageWithTextAndAnnotationSets();
		ServicePayload input = new ServicePayload("CorrelationId", result, emptyList(), null, null);
		given(mockUriHandler.readAsStream(DOCUMENT_TO_ENRICH_URI)).willReturn(toInputStream(input.toJsonString()));
	}

	private ClaimGranter claimGranter() {
		return new ClaimGranter(uriHandlers, claimChecks, new DefaultNonBlockingServiceClient(uriHandlers, claimChecks),
				new LocalServerUriTransformers(asList()));
	}

	private OutputAdapter outputAdapters() {
		return new OutputAdapter(asList(new FormatConversionTsvConfiguration().atLeastOneTsvOutputFormatConverter(),
				new FormatConversionLdfConfiguration().atLeastOneLdfOutputFormatConverter(ldfConversion())));
	}

	private InputAdapter inputAdapters() {
		return new InputAdapter(asList(new FormatConversionTsvConfiguration().atLeastOneTsvInputFormatConverter(),
				new FormatConversionLdfConfiguration().atLeastOneLdfInputFormatConverter(ldfConversion())));
	}

	private LdfConversion ldfConversion() {
		return createTestLdfConversion();
	}
}
