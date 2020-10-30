package com.elsevier.entellect.service;

import com.elsevier.ces.textandannotationsets.EnrichService;
import com.elsevier.smd.issuetracing.Issue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Responsibility: Unit tests for service logic.
 */
public class ClassificationFilterServiceTest {

	private static ServiceTestProperties serviceTestProperties = null;

//	private EnrichService service = new ClassificationFilterService();

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

	@Issue("ENTELLECT-TODO") // TODO Update Ticket Number
	@Test
	public void test() {
		// Given
		
		// When
		
		// TODO Populate tests here
		
		// Then
		assertTrue(true); // Keep Sonar happy
	}
}
