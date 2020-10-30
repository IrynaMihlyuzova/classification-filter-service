package com.elsevier.entellect.service;

import static com.amazonaws.regions.Regions.EU_WEST_1;
import static com.amazonaws.regions.Regions.EU_WEST_2;
import static com.amazonaws.regions.Regions.US_EAST_1;
import static com.amazonaws.services.s3.AmazonS3ClientBuilder.standard;
import static java.util.UUID.randomUUID;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.elsevier.cef.common.uri.UriHandlers;
import com.elsevier.smd.issuetracing.Issue;

/**
 * Responsibility: Tests configured {@link UriHandlers} can reach all AWS regions, as default client configuration
 * allows only a single region. This enables Entellect enrichment services to be located in a different region to
 * Entellect applications.
 */
@SuppressWarnings("squid:S2699")
@ContextConfiguration(classes = {ClassificationFilterServiceConfiguration.class, TestConfiguration.class})
@ActiveProfiles("patent")
public class ConfiguredS3ClientCanContactAllRegionsIntegrationTest {

	private static ServiceTestProperties serviceTestProperties = null;

	@BeforeClass
	public static void ensureTestSystemPropertiesAreSet() {
		serviceTestProperties = new ServiceTestProperties();
		serviceTestProperties.ensureTestSystemPropertiesAreSet();
	}

	@AfterClass
	public static void restoreTestSystemProperties() {
		serviceTestProperties.resetTestSystemProperties();
		serviceTestProperties = null;
	}

	@ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
	
    private String bucketName;
    
    @Autowired
    private UriHandlers uriHandlers;
    
    private AmazonS3 s3ForAnotherRegion;
    
	@Before
	public void setUp() throws Exception {
		bucketName = "entellect-test-" + randomUUID();
	}

	@Issue("ENTELLECT-4903")
	@Test
	public void testCanContactRandomRegions() {
		// Given
		s3ForAnotherRegion = standard().withRegion(US_EAST_1).build();
		s3ForAnotherRegion.createBucket(bucketName);
		
		// When
		uriHandlers.getChildren("s3://"+bucketName);
		
		// Then - no exception is thrown
	}
	
	@Issue("ENTELLECT-4903")
	@Test
	public void testCanContactDevRegions() {
		// Given
		s3ForAnotherRegion = standard().withRegion(EU_WEST_1).build();
		s3ForAnotherRegion.createBucket(bucketName);
		
		// When
		uriHandlers.getChildren("s3://"+bucketName);
		
		// Then - no exception is thrown
	}
	
	@Issue("ENTELLECT-4903")
	@Test
	public void testCanContactProdRegions() {
		// Given
		s3ForAnotherRegion = standard().withRegion(EU_WEST_2).build();
		s3ForAnotherRegion.createBucket(bucketName);
		
		// When
		uriHandlers.getChildren("s3://"+bucketName);
		
		// Then - no exception is thrown
	}
	
	@After
	public void tearDown() {
		try {
			s3ForAnotherRegion.deleteBucket(bucketName);
		} catch (AmazonS3Exception as3e) {
			// Ignore
		}
	}
}
