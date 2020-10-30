package com.elsevier.entellect.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.elsevier.ces.adapters.claimcheck.ClaimGranter;
import com.elsevier.ces.adapters.jsonld.ProvenanceApplier;
import com.elsevier.ces.logging.LoggingBean;
import com.elsevier.ces.logging.LoggingBeanPropertyPopulator;
import com.elsevier.ces.property.keys.ExchangePropertyKey;
import com.elsevier.unstructured.ingest.format.conversion.api.InputAdapter;
import com.elsevier.unstructured.ingest.format.conversion.api.OutputAdapter;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static com.amazonaws.regions.Regions.EU_WEST_1;
import static com.elsevier.ces.logging.Synchronicity.ASYNCHRONOUS;
import static com.elsevier.ces.logging.Synchronicity.SYNCHRONOUS;
import static com.elsevier.ces.property.keys.ExchangePropertyKey.*;
import static com.elsevier.ces.service.routes.ExceptionHandlerRoute.DIRECT_ERROR_HANDLING;
import static java.lang.String.format;

@Configuration
@EnableScheduling
@ComponentScan({ "com.elsevier.cef.configuration.impl", "com.elsevier.common.caching",
		"com.elsevier.common.monitoring.spring", "com.elsevier.cef.aws.spring",
		"com.elsevier.cef.service.queue.deadletter.impl", "com.elsevier.cef.utils",
		"com.elsevier.cef.notification.service.impl.sns", "com.elsevier.cef.notification.queue",
		"com.elsevier.cef.exception.service.impl", "com.elsevier.cef.file", "com.elsevier.cef.file.aop",
		"com.elsevier.entellect.service", "com.elsevier.cef.common.uri", "com.elsevier.ces.service.config",
		/* TODO This should be an @Import annotation */"com.elsevier.ces.uri",
		"com.elsevier.entellect.service"})
@Import(value = {
		com.elsevier.ces.adapters.claimcheck.configuration.CamelClaimCheckAdapterComponentsConfiguration.class,
		com.elsevier.ces.configuration.ServiceClientConfiguration.class, 
		com.elsevier.ces.uri.configuration.S3UriHandlersConfiguration.class,
		com.elsevier.unstructured.ingest.format.conversion.api.configuration.FormatConversionApiConfiguration.class,
		com.elsevier.unstructured.ingest.format.conversion.tsv.configuration.FormatConversionTsvConfiguration.class,
		com.elsevier.unstructured.ingest.format.conversion.ldf.configuration.FormatConversionLdfConfiguration.class})
public class ClassificationFilterServiceConfiguration {

	public static final String ASYNC_SERVICE_ROUTE = "AsyncServiceRoute";

	public static final String SYNC_SERVICE_ROUTE = "SyncServiceRoute";

	public static final String DIRECT_SERVICE_INVOKER_ROUTE = "direct:serviceInvokerRoute";

	public static final String SERVICE_NAME = "classification-filter-service";

//	public static final String SERVICE_BEAN_HANDLER_METHOD_EXPRESSION = format("enrich(${body}, ${property.%s})", PARAMETERS.getLabel());

	public static final String SERVICE_BEAN_HANDLER_METHOD_EXPRESSION = format("notificationParser(${body})");

	private static final String DIRECT_SYNC_SERVICE_ROUTE = "direct:syncServiceRoute";

	private static final String bucketName = "entellect-enrichment-services-mihlyuzovai";

	private static final String key = "classification-codes/classification_codes.properties";

	private static final Set<String> classificationCodes = new HashSet<>();

	@Bean
	public RouteBuilder asynchronousServiceRoute(
			@Value("${sqs.environment.aware.queue}") String serviceQueueName,
			@Value("${sqs.queue.listener.number.of.threads}") int numberOfThreads,
			@Qualifier("ClaimAsStringClaimRetriever") Processor claimAsStringClaimRetriever,
			ClaimGranter claimGranter,
			@Qualifier("NotificationFilterProcessor") Processor filterProcessor,
			@Qualifier("sqsQueueNameToCamelEndpointUri") Function<String, String> sqsQueueNameToCamelEndpointUri,
			@Value("${autostart.asynchronous.camel.routes}") boolean autoStartAsyncRoutes) {
		return new RouteBuilder() {
			@Override
			public void configure() {
				String bodyExpression = format("${property.%s})", SOURCE_CONTENT.getLabel());

				onException(Exception.class).to(DIRECT_ERROR_HANDLING).handled(true);

				from(serviceInputQueue()).routeId(ASYNC_SERVICE_ROUTE)
						.autoStartup(autoStartAsyncRoutes)
						.threads(numberOfThreads)
						.process(new LoggingBeanPropertyPopulator(SERVICE_NAME, ASYNCHRONOUS))
						.process(filterProcessor)
						.bean(filterProcessor,"filterByClassification")
						// todo send valid notification to the queue
//						.process(claimAsStringClaimRetriever).setBody(simple(bodyExpression))
//						.to(DIRECT_SERVICE_INVOKER_ROUTE).bean(claimGranter)
						.bean(new LoggingBean());
			}

			private String serviceInputQueue() {
				return sqsQueueNameToCamelEndpointUri.apply(serviceQueueName);
			}
		};
	}

/*	@Bean
	public RouteBuilder synchronousServiceRoute(
			@Value("${autostart.synchronous.camel.routes}") final boolean autoStartSynchronousRoutes,
			@Qualifier("ExtractDocumentFromInput") final Processor extractDocumentFromInput) {
		return new RouteBuilder() {
			@Override
			public void configure() {
				onException(Exception.class).to(DIRECT_ERROR_HANDLING).handled(true);
				from(DIRECT_SYNC_SERVICE_ROUTE).id(SYNC_SERVICE_ROUTE)
						.autoStartup(autoStartSynchronousRoutes)
						.process(new LoggingBeanPropertyPopulator(SERVICE_NAME, SYNCHRONOUS))
						.process(extractDocumentFromInput).to(DIRECT_SERVICE_INVOKER_ROUTE)
						.bean(new LoggingBean());
			}
		};
	}*/

	@Bean
	public RouteBuilder serviceInvokerRoute(
			ServiceManagementLoggingConfiguration serviceManagementLoggingConfiguration,
			ProvenanceConfiguration provenanceConfiguration,
//			@Qualifier("ClassificationFilterService") EnrichService service,
			ClassificationFilterService classificationFilterService,
			final InputAdapter inputAdapter, final OutputAdapter outputAdapter) {
		return new RouteBuilder() {
			@Override
			public void configure() {
				onException(Exception.class).to(DIRECT_ERROR_HANDLING).handled(true);
				from(DIRECT_SERVICE_INVOKER_ROUTE)
						.process(serviceManagementLoggingConfiguration.getStartTimeSetter())
						.bean(inputAdapter, InputAdapter.CAMEL_BEAN_METHOD_EXPRESSION)
						.setProperty(SOURCE_CONTENT.getLabel(), simple("${body}"))
						.process(provenanceConfiguration.getProvenanceProvider())
//						.bean(service, SERVICE_BEAN_HANDLER_METHOD_EXPRESSION)
						.bean(classificationFilterService, SERVICE_BEAN_HANDLER_METHOD_EXPRESSION)
						.bean(provenanceConfiguration.getProvenanceApplier(), "applyTo(${body}, java.util.Collections#singletonList('entities'))")
						.bean(outputAdapter, OutputAdapter.CAMEL_BEAN_METHOD_EXPRESSION)
						.process(serviceManagementLoggingConfiguration.getEndTimeSetter());
			}
		};
	}

	/**
	 * @return A standard AWS S3 client.
	 */
	@Bean
	public static AmazonS3 s3Client() {
		return AmazonS3ClientBuilder
				.standard()
				.withRegion(EU_WEST_1)
				.withForceGlobalBucketAccessEnabled(Boolean.TRUE)
				.build();
	}

	@Bean
	public ServiceManagementLoggingConfiguration serviceManagementLoggingConfiguration(
		@Qualifier("StartTimeSetter") final Processor startTimeSetter,
		@Qualifier("EndTimeSetter") final Processor endTimeSetter) {

		return new ServiceManagementLoggingConfiguration(startTimeSetter, endTimeSetter);
	}

	@Bean
	public ProvenanceConfiguration provenanceConfiguration(
		@Qualifier("ClassificationFilterServiceProvenanceProvider") final Processor provenanceProvider,
		ProvenanceApplier provenanceApplier) {

		return new ProvenanceConfiguration(provenanceProvider, provenanceApplier);
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@PostConstruct
	public void onStartup() {
		reloadClassificationCodes();
	}

	@Scheduled(cron="0 0 6 * * ?")
	public void onSchedule() {
		reloadClassificationCodes();
	}

	public void reloadClassificationCodes() {
		AmazonS3 s3Client = ClassificationFilterServiceConfiguration.s3Client();

		System.out.println("Downloading an object");

		try (final S3Object fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
			 final S3ObjectInputStream inputStream = fullObject.getObjectContent()){

			String propertiesContent;
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder buffer = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			propertiesContent = buffer.toString().replaceAll("\\s+","");
			Collections.addAll(classificationCodes, propertiesContent.split(","));

			System.out.println("properties = " + propertiesContent);

		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public static Set<String> getClassificationCodes() {
		return classificationCodes;
	}
}
