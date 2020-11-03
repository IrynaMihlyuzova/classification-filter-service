package com.elsevier.entellect.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.elsevier.cef.common.uri.UriHandlers;
import com.elsevier.ces.uri.S3UriHandler;
import com.elsevier.entellect.service.codesloader.BibliographyCodesLoader;
import com.elsevier.smd.issuetracing.Issue;
import com.elsevier.smd.junit.rules.s3.LocalS3;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static com.elsevier.smd.junit.rules.s3.LocalS3.createWithAutomaticSettings;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class BibliographyCodesLoaderTest {

    @ClassRule
    @Rule
    public static LocalS3 localS3 = createWithAutomaticSettings();

    @Issue("ENTELLECT-9355")
    @Test
    public void testOnStartUp() {
        // Given
        AmazonS3 s3 = localS3.getClient();
        UriHandlers uriHandlers = getUriHandlers(s3);
        BibliographyCodesLoader bibliographyCodesLoader = getClassificationCodesLoader(uriHandlers);

        // When
        bibliographyCodesLoader.onStartup();

        // Then
        assertThat(bibliographyCodesLoader.getClassificationCodes(), is(notNullValue()));
        assertThat(bibliographyCodesLoader.getClassificationCodes().size(), is(equalTo(2)));
    }

    @Issue("ENTELLECT-9355")
    @Test
    public void testReloadWithMoreItems() {
        // Given
        AmazonS3 s3 = localS3.getClient();
        UriHandlers uriHandlers = getUriHandlers(s3);
        BibliographyCodesLoader bibliographyCodesLoader = getClassificationCodesLoader(uriHandlers);
        bibliographyCodesLoader.onStartup();
        s3.putObject("entellect-enrichment-services-mihlyuzovai",
                "bibliography-classification-codes/classification_codes.properties", "A61K,G01N,EXTRA_IPC");

        // When
        bibliographyCodesLoader.onStartup();

        // Then
        assertThat(bibliographyCodesLoader.getClassificationCodes(), is(notNullValue()));
        assertThat(bibliographyCodesLoader.getClassificationCodes().size(), is(equalTo(3)));
    }

    @Issue("ENTELLECT-9355")
    @Test
    public void testReloadWithLessItems() {
        // Given
        AmazonS3 s3 = localS3.getClient();
        UriHandlers uriHandlers = getUriHandlers(s3);
        BibliographyCodesLoader bibliographyCodesLoader = getClassificationCodesLoader(uriHandlers);
        bibliographyCodesLoader.onStartup();
        s3.putObject("entellect-enrichment-services-mihlyuzovai",
                "bibliography-classification-codes/classification_codes.properties", "A61K");

        // When
        bibliographyCodesLoader.onStartup();

        // Then
        assertThat(bibliographyCodesLoader.getClassificationCodes(), is(notNullValue()));
        assertThat(bibliographyCodesLoader.getClassificationCodes().size(), is(equalTo(1)));
    }

    @Issue("ENTELLECT-9355")
    @Test
    public void testReloadAfterDeletion() {
        // Given
        AmazonS3 s3 = localS3.getClient();
        UriHandlers uriHandlers = getUriHandlers(s3);
        BibliographyCodesLoader bibliographyCodesLoader = getClassificationCodesLoader(uriHandlers);
        bibliographyCodesLoader.onStartup();
        s3.deleteObject("entellect-enrichment-services-mihlyuzovai",
                "bibliography-classification-codes/classification_codes.properties");

        // When
        try {
            bibliographyCodesLoader.onStartup();
        }
        catch(AmazonS3Exception ase) {
            // Ignore exception for test
        }
        // Then
        assertThat(bibliographyCodesLoader.getClassificationCodes(), is(notNullValue()));
        assertThat(bibliographyCodesLoader.getClassificationCodes().size(), is(equalTo(2)));
    }

    @Issue("ENTELLECT-9355")
    @Test
    public void testReloadWithSameItems() {

         // Given
        Set<String> expResult = new HashSet<>();
        expResult.add("A61K");
        expResult.add("G01N");

        AmazonS3 s3 = localS3.getClient();
        UriHandlers uriHandlers = getUriHandlers(s3);
        BibliographyCodesLoader bibliographyCodesLoader = getClassificationCodesLoader(uriHandlers);
        bibliographyCodesLoader.onStartup();
        s3.putObject("entellect-enrichment-services-mihlyuzovai",
                "bibliography-classification-codes/classification_codes.properties", "A61K,G01N");

        // When
        bibliographyCodesLoader.onStartup();

        // Then
        assertThat(bibliographyCodesLoader.getClassificationCodes(), is(notNullValue()));
        assertThat(bibliographyCodesLoader.getClassificationCodes().size(), is(equalTo(2)));
        assertEquals(expResult,bibliographyCodesLoader.getClassificationCodes());
    }

    @Issue("ENTELLECT-9355")
    @Test
    public void testReloadItemsWithSpaces() {

        // Given
        Set<String> expResultWithoutSpaces = new HashSet<>();
        expResultWithoutSpaces.add("Y10S");
        expResultWithoutSpaces.add("C22C");

        AmazonS3 s3 = localS3.getClient();
        UriHandlers uriHandlers = getUriHandlers(s3);
        BibliographyCodesLoader bibliographyCodesLoader = getClassificationCodesLoader(uriHandlers);
        bibliographyCodesLoader.onStartup();
        s3.putObject("entellect-enrichment-services-mihlyuzovai",
                "bibliography-classification-codes/classification_codes.properties", " Y10S, C22C ");

        // When
        bibliographyCodesLoader.onStartup();

        // Then
        assertThat(bibliographyCodesLoader.getClassificationCodes(), is(notNullValue()));
        assertThat(bibliographyCodesLoader.getClassificationCodes().size(), is(equalTo(2)));
        assertEquals(expResultWithoutSpaces,bibliographyCodesLoader.getClassificationCodes());
    }


    private UriHandlers getUriHandlers(AmazonS3 s3) {
        s3.createBucket("entellect-enrichment-services-mihlyuzovai");
        s3.putObject("entellect-enrichment-services-mihlyuzovai",
                "bibliography-classification-codes/classification_codes.properties", "A61K,G01N");
        return new UriHandlers(singletonList(new S3UriHandler(s3)));
    }

    private BibliographyCodesLoader getClassificationCodesLoader(UriHandlers uriHandlers) {
        return new BibliographyCodesLoader(uriHandlers, "entellect-enrichment-services-mihlyuzovai",
                "bibliography-classification-codes/classification_codes.properties");
    }
}
