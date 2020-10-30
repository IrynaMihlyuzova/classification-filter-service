package com.elsevier.entellect.apitests;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.Ignore;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"src/test/resources/features"},
		glue = {"com.elsevier.smd.http.stepdefs.steps", "com.elsevier.smd.placeholder.stepdef", "com.elsevier.ces.s3.stepdefs.steps", "com.elsevier.ces.sqs.stepdefs", "com.elsevier.ces.http.stepdefs.steps", "com.elsevier.ces.urihandler.assertion.stepdefs"},
		tags = {"~@wip"},
		strict=true)
@Ignore("Ignored because BDD tests do not yet currently work. This code and the feature files remain here for when the BDD tests are updated.")
public class ApiIntegrationTest {

}
