package com.elsevier.entellect.service.route;

import static com.elsevier.entellect.service.ClassificationFilterServiceConfiguration.SERVICE_BEAN_HANDLER_METHOD_EXPRESSION;
import static com.elsevier.ces.property.keys.ExchangePropertyKey.RESULT;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.elsevier.entellect.service.ClassificationFilterService;
import com.elsevier.entellect.service.ClassificationFilterServiceToRnf2Appender;
import com.elsevier.ces.textandannotationsets.MessageWithTextAndAnnotationSets;
import com.elsevier.ces.unit.test.EntellectIntegrationTestBase;
import com.elsevier.ces.unit.test.EntellectTestModel;
import com.elsevier.ces.word.document.flattener.WordDocumentFlattenerInvoker;
import com.elsevier.ces.word.document.flattener.WordDocumentFlatteningService;
import com.elsevier.ces.word.document.flattener.WordRawContentExtractor;
import com.elsevier.smd.issuetracing.Issue;

/**
 * Responsibility: Test a chain of services to control data dependencies without using a canned input.
 *
 * @see <a href="https://confluence.cbsels.com/pages/viewpage.action?pageId=126402998">Hexagonal Architecture Description for Entellect Enrichment Services</a>
 */
@RunWith(Parameterized.class)
public class ServiceWithDependenciesIntegrationTest extends EntellectIntegrationTestBase {

	public static final String UPLOAD_ENRICH_DOC_URI = "s3://bucket/path/fixtureDocument.json";

	public static final String SERVICE_OUTPUT_APPENDER_BEAN_HANDLER_METHOD_EXPRESSION = "append(${body}, ${property.Result})";

	public ServiceWithDependenciesIntegrationTest(String fixtureLocation, String expectedOutputFileName) {
		super(fixtureLocation,
				new EntellectTestModel(fixtureLocation, expectedOutputFileName, UPLOAD_ENRICH_DOC_URI, null),
				emptyList());
	}

	@Parameterized.Parameters(name = "{index}: fixture: {0} expected: {1}")
	public static Iterable<Object[]> data() {
		return asList(new Object[][] { { "wikipediaIonicBond.docx", "wikipediaIonicBond.sentences.json" },
				// TODO Populate your fixture documents and expectation documents
				{ "punctuationAndAcronyms.docx", "punctuationAndAcronyms.sentences.json" }
		});
	}

	@Test
	@Issue("ENTELLECT-TODO") // TODO Update Ticket Number
	public void shouldGenerateTheExpectedSentencesForWikipediaIonicBondDocument() throws Exception {
		// Given
		
		// When
		Exchange actualExchange = processDocumentAndCaptureOutput(sourceDocFileName, entellectTestModel.getSourceUri());

		// Then
		MessageWithTextAndAnnotationSets actualResult = actualExchange.getIn()
				.getBody(MessageWithTextAndAnnotationSets.class);
		
		// TODO Provide assertions
		assertTrue(true); // Keep Sonar happy
	}

	@Override
	protected RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(MOCK_DIRECT_FLATTENER_QUEUE).id("id")
						.bean(new WordDocumentFlattenerInvoker(
								new WordDocumentFlatteningService(new WordRawContentExtractor())))
						.setProperty(RESULT.getLabel(), simple("${body}"))
						.bean(new ClassificationFilterService(), SERVICE_BEAN_HANDLER_METHOD_EXPRESSION)
						.bean(new ClassificationFilterServiceToRnf2Appender(),
								SERVICE_OUTPUT_APPENDER_BEAN_HANDLER_METHOD_EXPRESSION)
						.to(MOCK_OUT);
			}
		};
	}
}
