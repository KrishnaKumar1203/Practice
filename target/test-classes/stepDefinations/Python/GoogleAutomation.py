from Selenium import SeleniumPython  # Import your Selenium utility
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import time

class GoogleAutomation:
    def __init__(self):
        # Use the driver from your Selenium utility
        self.driver = Selenium.get_driver("chrome")  # or pass browser name as needed
        url = self.get_property("url")
        self.driver.get(url)

        try:
            # Wait for the page to load
            time.sleep(2)

            # Locate and interact with the "I'm Feeling Lucky" button
            feeling_lucky = self.wait_for_element(By.NAME, "btnI")
            print("Text =", feeling_lucky.get_attribute("value"))
            # feeling_lucky.click()  # Uncomment if needed

            # Locate the search box and perform a search
            searchbox = self.wait_for_element(By.NAME, "q")
            searchbox.send_keys("NIFTY 50 today Value")
            searchbox.send_keys(Keys.RETURN)

            # Wait for results to load
            time.sleep(2)

            # Locate the Nifty50 value and validate it
            nifty50 = self.wait_for_element(By.XPATH, "//span[contains(text(),'NIFTY 50')]/following::span[1]")
            nifty50_text = nifty50.text.replace(",", "")
            amount = float(nifty50_text)

            print("Nifty 50 value =", nifty50.text)
            if amount >= 23000:
                print("Test Passed", amount)
            else:
                print("Test Failed", amount)
        except Exception as e:
            print("Error during automation:", e)
        finally:
            self.driver.quit()

    def wait_for_element(self, by, value, timeout=20):
        from selenium.webdriver.support.ui import WebDriverWait
        from selenium.webdriver.support import expected_conditions as EC
        return WebDriverWait(self.driver, timeout).until(
            EC.presence_of_element_located((by, value))
        )

    def get_property(self, key):
        # Replace this with your actual config reader logic
        # For demo, just return Google's URL
        return "https://www.google.com"

if __name__ == "__main__":
    GoogleAutomation()