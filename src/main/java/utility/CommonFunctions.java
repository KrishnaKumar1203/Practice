package utility;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CommonFunctions {

    public static  WebElement ExplicitWait(WebDriver driver, int time, String xpath) {
        WebElement element;
        element = new WebDriverWait(driver, Duration.ofSeconds(time))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    
            return element;    
            }

    public static WebElement FluentWait(WebDriver driver, int time, String xpath) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(time))  // Total timeout
                .pollingEvery(Duration.ofSeconds(2))  // Check every 2 seconds
                .ignoring(NoSuchElementException.class);  // Ignore specific exceptions

        WebElement element = fluentWait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(By.xpath(xpath));
            }
        });
        return element;
    }
    public static WebElement ImplicitWait(WebDriver driver, int time, String xpath) 
    {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(time));
        WebElement element = driver.findElement(By.xpath(xpath));
    return element;

    }

}