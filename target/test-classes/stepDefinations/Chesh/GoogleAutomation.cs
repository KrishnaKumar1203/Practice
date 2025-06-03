using System;
using OpenQA.Selenium;
using OpenQA.Selenium.Support.UI;
using SeleniumExtras.WaitHelpers;

namespace StepDefinitions
{
    public class GoogleAutomation
    {
        private IWebDriver driver;

        public GoogleAutomation()
        {
            try
            {
                // Initialize the WebDriver
                driver = Selenium.GetDriver(); // Replace with your WebDriver initialization logic
                string url = ConfigReader.GetProperty("url"); // Replace with your configuration logic
                // Navigate to Google
                driver.Navigate().GoToUrl(url);

                // Locate and interact with the "I'm Feeling Lucky" button
                WebDriverWait wait = new WebDriverWait(driver, TimeSpan.FromSeconds(20));
                IWebElement feelingLucky = wait.Until(ExpectedConditions.ElementIsVisible(By.XPath(PageObjectModel.FeelingLucky)));
                Console.WriteLine("Text = " + feelingLucky.GetAttribute("value"));
                // feelingLucky.Click(); // Uncomment if needed

                // Locate the search box and perform a search
                IWebElement searchBox = wait.Until(ExpectedConditions.ElementIsVisible(By.XPath(PageObjectModel.SearchBox)));
                searchBox.SendKeys("NIFTY 50 today Value");
                searchBox.Submit();

                // Locate the Nifty50 value and validate it
                IWebElement nifty50 = wait.Until(ExpectedConditions.ElementIsVisible(By.XPath(PageObjectModel.Nifty50)));
                string nifty50Text = nifty50.Text.Replace(",", ""); // Remove commas if any
                double amount = double.Parse(nifty50Text);

                Console.WriteLine("Nifty 50 value = " + nifty50.Text);
                if (amount >= 23000)
                {
                    Console.WriteLine("Test Passed " + amount);
                }
                else
                {
                    Console.WriteLine("Test Failed " + amount);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Error during automation: " + e.Message);
            }
            finally
            {
                // Ensure the browser is closed
                driver.Quit();
            }
        }

        [Given("I open the Google homepage and read text and check for nifty50")]
        public void OpenGoogleAndCheckNifty50()
        {
            // Create an instance of GoogleAutomation to run the constructor
            new GoogleAutomation();
        }

        public static void Main(string[] args)
        {
            // Create an instance of GoogleAutomation to run the constructor
            new GoogleAutomation();
        }
    }

    // Mock classes for ConfigReader, Selenium, and PageObjectModel
    public static class ConfigReader
    {
        public static string GetProperty(string key)
        {
            // Replace with your logic to fetch configuration properties
            return "https://www.google.com";
        }
    }

    public static class Selenium
    {
        public static IWebDriver GetDriver()
        {
            // Replace with your WebDriver initialization logic
            return new OpenQA.Selenium.Chrome.ChromeDriver();
        }
    }

    public static class PageObjectModel
    {
        public static string FeelingLucky = "//input[@name='btnI']"; // Replace with the actual XPath
        public static string SearchBox = "//input[@name='q']"; // Replace with the actual XPath
        public static string Nifty50 = "//div[@id='nifty50']"; // Replace with the actual XPath
    }
}