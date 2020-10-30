package com.elsevier.entellect;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.Ignore;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"src/test/resources/features"},
		glue = {"com.elsevier.smd.http.stepdefs.steps", "com.elsevier.smd.placeholder.stepdef", "com.elsevier.ces.http.stepdefs.steps"},
		tags = {"@healthcheck", "~@wip"},
		strict=true)
@Ignore("Ignored because BDD tests do not yet currently work. This code and the feature files remain here for when the BDD tests are updated.")
public class RunSmokeTest {

}
