package org.myProject;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/GoogleAutomation.feature",
        glue = "stepDefinations.Java",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/google-automation.html",
                "json:target/cucumber-reports/google-automation.json"
        },
        tags = "@google",
        monochrome = true)
public final class GoogleAutomationCucumberTest {
}
