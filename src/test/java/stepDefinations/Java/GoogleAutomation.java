package stepDefinations.Java;
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
       
       

    try {
            // Locate and interact with the "I'm Feeling Lucky" button
       WebElement  FeelingLucky = CommonFunctions.FluentWait(driver,20,PageObjectModel.FeelingLucky);
        // FeelingLucky.click();
       
        System.out.println("Text ="+ FeelingLucky.getAttribute("value"));
        // FeelingLucky.click(); // Uncomment if needed

	// Locate the search box and perform a search
        WebElement searchbox = CommonFunctions.FluentWait(driver,20,PageObjectModel.SearchBox);
         
         searchbox.sendKeys("NIFTY 50 today Value");
            searchbox.submit();
           

	// Locate the Nifty50 value and validate it
          WebElement  Nifty50= CommonFunctions.FluentWait(driver,20,PageObjectModel.Nifty50);
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
} catch (Exception e) {
            System.err.println("Error during automation: " + e.getMessage());
        } finally {
            // Ensure the browser is closed
            driver.quit();
        }
    }
    
    //     public static void main(String[] args) {
    //     // Create an instance of GoogleAutomation to run the constructor
    //     new GoogleAutomation();
    // }
  
    @Given("I open the Google homepage and read text and check for nifty50")  
    public void i_open_the_google_homepage_and_read_text_and_check_for_nifty50() {
        // Create an instance of GoogleAutomation to run the constructor 
        new GoogleAutomation();
       
    }
    public static void main(String[] args) {
        // Create an instance of GoogleAutomation to run the constructor
        new GoogleAutomation();
    }
   
}