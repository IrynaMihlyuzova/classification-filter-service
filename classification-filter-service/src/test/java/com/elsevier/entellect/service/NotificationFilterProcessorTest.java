package com.elsevier.entellect.service;

import com.elsevier.ces.unit.test.FixtureUtil;
import com.elsevier.entellect.service.processors.NotificationFilterProcessor;
import com.elsevier.smd.issuetracing.Issue;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.elsevier.ces.unit.test.FixtureUtil.externalFileAsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationFilterProcessorTest {

    @Mock
    public ClassificationCodesLoader classificationCodesLoader;

    @Issue("ENTELLECT-9355")
    @Test
    public void testNotificationWhichHasCodeIsSuccessful() {
        // Given
        Set<String> result = new HashSet<>();
        result.add("A61K");
        when(classificationCodesLoader.getClassificationCodes()).thenReturn(result);
        NotificationFilterProcessor notificationFilterProcessor = new NotificationFilterProcessor(classificationCodesLoader);
        String notificationAsString = externalFileAsString("/notifications/simple.json");

        // When
        boolean actual = false;
        try {
            actual = notificationFilterProcessor.filterByClassification(notificationAsString);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // Then
        assertThat(actual, is(equalTo(true)));
    }
}
