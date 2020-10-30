package com.elsevier.entellect.service;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ClassificationFilterServiceConfiguration.class, TestConfiguration.class})
@Configuration
@ComponentScan(basePackages = {"com.elsevier.entellect.classification-filter.services"})
@ActiveProfiles("patent")
public class SpringContextIsViableIntegrationTest {

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

	@SuppressWarnings("squid:S2699")
	@Test
	public void springContextIsViable() {
	}
}
