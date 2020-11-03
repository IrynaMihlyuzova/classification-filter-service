package com.elsevier.entellect.service;

import com.elsevier.entellect.service.codesloader.BibliographyCodesLoader;
import com.elsevier.entellect.service.filter.BibliographyFilter;
import com.elsevier.smd.issuetracing.Issue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
public class NotificationFilterTest {

    @Mock
    public BibliographyCodesLoader bibliographyCodesLoader;

    @Issue("ENTELLECT-9355")
    @Test
    public void testBibliographyNotificationWhichHasCodeIsSuccessful() {
        // Given
        Set<String> result = new HashSet<>();
        result.add("A61K");
        when(bibliographyCodesLoader.getClassificationCodes()).thenReturn(result);
        BibliographyFilter bibliographyFilter = new BibliographyFilter(bibliographyCodesLoader);
        bibliographyFilter.setClassificationCodesLoader(bibliographyCodesLoader);
        String notificationAsString = externalFileAsString("/notifications/simple.json");

        // When
        boolean actual = false;
        try {
            actual = bibliographyFilter.filterByClassification(notificationAsString);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // Then
        assertThat(actual, is(equalTo(true)));
    }
}
