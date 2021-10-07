package org.auto.test;




import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "json:target/cucumber.json", "pretty",
        "html:target/cucumber-reports.html" },
features = "target/features/", glue = "org/auto/test/step_defs", tags = "@smoke_test")
public class SmokeSuiteRunner
{
}
