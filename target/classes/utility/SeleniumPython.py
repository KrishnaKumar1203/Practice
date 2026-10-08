import subprocess
from selenium import webdriver
from selenium.webdriver.chrome.options import Options as ChromeOptions
from selenium.webdriver.edge.options import Options as EdgeOptions
from selenium.webdriver.firefox.options import Options as FirefoxOptions
import platform
import tempfile

class Selenium:

    @staticmethod
    def kill_chrome():
        try:
            if platform.system() == "Windows":
                subprocess.run(["taskkill", "/IM", "chrome.exe", "/F"], check=True)
            else:
                subprocess.run(["pkill", "chrome"], check=True)
        except Exception as e:
            print("Error killing Chrome:", e)

    @staticmethod
    def kill_edge():
        try:
            if platform.system() == "Windows":
                subprocess.run(["taskkill", "/IM", "msedge.exe", "/F"], check=True)
            else:
                subprocess.run(["pkill", "msedge"], check=True)
        except Exception as e:
            print("Error killing Edge:", e)

    @staticmethod
    def get_driver(browser="chrome"):
        browser = browser.lower()
        driver = None

        if browser == "chrome":
            Selenium.kill_chrome()
            options = ChromeOptions()
            options.add_argument("--disable-blink-features=AutomationControlled")
            options.add_argument("start-maximized")
            options.add_argument("--disable-infobars")
            options.add_argument("--disable-extensions")
            options.add_argument("--disable-gpu")
            options.add_argument("--no-sandbox")
            options.add_argument("--disable-dev-shm-usage")
            options.add_experimental_option("useAutomationExtension", False)
            options.add_experimental_option("excludeSwitches", ["enable-automation"])
            options.add_argument("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            options.add_experimental_option("prefs", {
                "credentials_enable_service": False,
                "profile.password_manager_enabled": False
            })
            # Add unique user data directory
            user_data_dir = tempfile.mkdtemp(prefix="selenium-user-data-")
            options.add_argument(f"--user-data-dir={user_data_dir}")
            # Run in headless mode
            options.add_argument("--headless")
            driver = webdriver.Chrome(options=options)

        elif browser == "firefox":
            options = FirefoxOptions()
            options.add_argument("--headless")
            driver = webdriver.Firefox(options=options)

        elif browser == "edge":
            Selenium.kill_edge()
            options = EdgeOptions()
            options.add_argument("--disable-blink-features=AutomationControlled")
            options.add_argument("start-maximized")
            options.add_argument("--disable-infobars")
            options.add_argument("--disable-extensions")
            options.add_argument("--disable-gpu")
            options.add_argument("--no-sandbox")
            options.add_argument("--disable-dev-shm-usage")
            options.add_experimental_option("useAutomationExtension", False)
            options.add_experimental_option("excludeSwitches", ["enable-automation"])
            options.add_argument("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            options.add_experimental_option("prefs", {
                "credentials_enable_service": False,
                "profile.password_manager_enabled": False
            })
            options.add_argument("--headless")
            driver = webdriver.Edge(options=options)

        elif browser == "safari":
            driver = webdriver.Safari()

        else:
            raise ValueError(f"Invalid browser: {browser}")

        driver.maximize_window()
        return driver

# Example usage:
if __name__ == "__main__":
    driver = Selenium.get_driver("chrome")
    driver.get("https://www.google.com")
    print(driver.title)
    driver.quit()