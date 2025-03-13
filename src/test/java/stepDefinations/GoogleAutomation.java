package stepDefinations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.example.ConfigReader;
import io.cucumber.java.en.Given;
import utility.CommonFunctions;
import utility.PageObjectModel;
import utility.Selenium;

public class GoogleAutomation {

    WebDriver driver;

    public GoogleAutomation() {

        // Initialize the WebDriver
        driver = Selenium.getDriver();
       String url = ConfigReader.getProperty("url");
        // Navigate to Google
        driver.get(url);
       
        // PageObjectModel page = new PageObjectModel(driver);
       
        WebElement FeelingLuckey;

    
        FeelingLuckey = CommonFunctions.FluentWait(driver,20,PageObjectModel.FeelingLucky);
        // FeelingLuckey.click();
       
        System.out.println("Text ="+ FeelingLuckey.getAttribute("value"));
         WebElement searchbox;

         searchbox = CommonFunctions.FluentWait(driver,20,PageObjectModel.SearchBox);
         
         searchbox.sendKeys("NIFTY 50 today Value");
            searchbox.submit();
            WebElement Nifty50;

            Nifty50= CommonFunctions.FluentWait(driver,20,PageObjectModel.Nifty50);
            String nifty50Text = Nifty50.getText().replace(",", ""); // Remove commas if any
            double Amount = Double.parseDouble(nifty50Text);
    
            System.out.println("Nifty 50 value ="+ Nifty50.getText());
            if (Amount>=23000)
            {
                System.out.println("Test Passed "+Amount);
            }
            else
            {
                System.out.println("Test Failed "+Amount);
            }
    }
    
    //     public static void main(String[] args) {
    //     // Create an instance of GoogleAutomation to run the constructor
    //     new GoogleAutomation();
    // }
  
    @Given("I open the Google homepage and read text and check for nifty50")  
    public void i_open_the_google_homepage_and_read_text_and_check_for_nifty50() {
        // Write code here that turns the phrase above into concrete actions  
        new GoogleAutomation();
        // throw new io.cucumber.java.PendingException();
    }
   
}