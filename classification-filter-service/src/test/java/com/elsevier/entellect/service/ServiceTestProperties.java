package com.elsevier.entellect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.fail;

/**
 * Helper class to set the service test properties before test execution and reset them at the end.
 * <br/>
 * Service test classes should use {@code BeforeClass} and {@code AfterClass} annotated methods to invoke this class.
 * e.g.
 * <pre>{@code
 * @BeforeClass
 * public static void ensureTestSystemPropertiesAreSet() {
 * 	serviceTestProperties = new ServiceTestProperties();
 * 	serviceTestProperties.ensureTestSystemPropertiesAreSet();
 * }
 *
 * @AfterClass
 * public static void restoreTestSystemProperties() {
 * 	serviceTestProperties.resetTestSystemProperties();
 * 	serviceTestProperties = null;
 * }
 * }</pre>
 */
public class ServiceTestProperties {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceTestProperties.class);

    private static final String TEST_PROPERTIES_FILE = "test.properties";

    private Properties properties;

    private Map<String, String> oldSystemProperties;

    public ServiceTestProperties() {
        properties = loadProperties();
        oldSystemProperties = new HashMap<>();
    }

    public void ensureTestSystemPropertiesAreSet() {
        properties.forEach(this::setProperty);
    }

    public void resetTestSystemProperties() {
        properties.forEach((key, value) -> {
            if (oldSystemProperties.containsKey(key)) {
                System.setProperty((String) key, oldSystemProperties.get(key));
            }
        });

        oldSystemProperties.clear();
    }

    private Properties loadProperties() {
        final Properties properties = new Properties();
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(TEST_PROPERTIES_FILE);
            if (resourceAsStream == null) {
                fail("The test system properties file does not exist, " + TEST_PROPERTIES_FILE);
            }
            properties.load(resourceAsStream);
        } catch (IOException e) {
            LOG.error("Failed to load the test system properties file.", e);
            fail("Could not load the test system properties file " + TEST_PROPERTIES_FILE);
        }

        return properties;
    }

    private void setProperty(final Object key, final Object value) {
        final String oldPropertyValue = System.setProperty((String) key, (String) value);

        if (oldPropertyValue != null) {
            oldSystemProperties.put((String) key, oldPropertyValue);
        }
    }
}
