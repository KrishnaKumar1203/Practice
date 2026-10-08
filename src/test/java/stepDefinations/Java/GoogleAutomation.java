package stepDefinations.Java;

import com.example.ConfigReader;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.CommonFunctions;
import utility.PageObjectModel;
import utility.Selenium;

import java.time.Duration;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoogleAutomation {
    private static final int WAIT_SECONDS = 20;

    private WebDriver driver;
    private String searchResultsText;

    @Given("I open the Google homepage")
    public void openGoogleHomepage() {
        String url = ConfigReader.getProperty("url");
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("The Google homepage URL is not configured.");
        }

        driver = Selenium.getDriver();
        driver.get(url);
        CommonFunctions.ExplicitWait(driver, WAIT_SECONDS, PageObjectModel.SearchBox);
    }

    @When("I search Google for {string}")
    public void searchGoogle(String query) {
        WebElement searchBox = CommonFunctions.ExplicitWait(
                driver,
                WAIT_SECONDS,
                PageObjectModel.SearchBox);
        searchBox.clear();
        searchBox.sendKeys(query);
        searchBox.submit();

        By resultsLocator = By.xpath(PageObjectModel.SearchResults);
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SECONDS));
        WebElement results = wait.until(browser -> {
            WebElement candidate = browser.findElement(resultsLocator);
            String text = candidate.getText().toLowerCase(Locale.ROOT);
            return text.contains(normalizedQuery) ? candidate : null;
        });
        searchResultsText = results.getText();
    }

    @Then("the Google results mention {string}")
    public void verifyGoogleResultsMention(String expectedText) {
        assertTrue(
                searchResultsText != null
                        && searchResultsText.toLowerCase(Locale.ROOT)
                                .contains(expectedText.toLowerCase(Locale.ROOT)),
                () -> "Google results did not contain: " + expectedText);
    }

    @After
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
