package org.myProject;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/toolkit.feature",
        glue = "stepDefinations.toolkit",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/toolkit-bdd.html",
                "json:target/cucumber-reports/toolkit-bdd.json"
        },
        monochrome = true)
public final class ToolkitCucumberTest {
}
