package utility;

public class PageObjectModel {

    public static String FeelingLucky = "//div[3]/center/input[contains(@value, 'Feeling Lucky')]";
    public static String SearchBox = "//textarea[@title='Search']";
    public static String SearchResults = "//*[@id='search']";
    public static String Nifty50 = "//*[@id='knowledge-finance-wholepage__entity-summary']/div[3]/g-card-section/div/g-card-section/div[2]/div[1]/span[1]/span/span";
    public static String Translation ="//textarea[@id='textarea1']";
    public static String TranslateLanguage= "//*[@id='translation-form']/div[1]/div[2]/select";
    public static String Textcopied = "//*[@id='translation-form']/div[2]/div[2]/div[1]/button[2]/i";
   /*   WebDriver driver;

    // Constructor to initialize the elements
    public PageObjectModel(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Define WebElements using @FindBy annotation
    @FindBy(xpath = "//textarea[@title='Search']")
    private WebElement searchBox;

    @FindBy(xpath = "//div[3]/center/input[contains(@value, 'Feeling Lucky')]")
    private WebElement feelingLuckyButton;

    @FindBy(xpath = "//*[@id='knowledge-finance-wholepage__entity-summary']/div[3]/g-card-section/div/g-card-section/div[2]/div[1]/span[1]/span/span")
    private WebElement nifty50;

    // Methods to interact with the elements
    public void enterSearchText(String text) {
        searchBox.sendKeys(text);
    }

    public void clickFeelingLucky() {
        feelingLuckyButton.click();
    }

    public String getNifty50Text() {
        return nifty50.getText();
    }
*/
}
