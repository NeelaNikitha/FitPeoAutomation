package Assignment;

import java.time.Duration;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class slider {

	public static void main(String[] args) throws InterruptedException {

		// User input for browser choice
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the browser you want to use (chrome/firefox/edge): ");
        String browser = scanner.nextLine().toLowerCase();

        WebDriver driver;

        // Initialize driver based on browser choice
        switch (browser) {
            case "chrome":
                //WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                //WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                //WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            default:
                System.out.println("Invalid browser choice! Defaulting to Chrome.");
                //WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
        }

		
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		
		//1) Navigating to the FitPeo Homepage
		driver.get("https://www.fitpeo.com/");
		driver.manage().window().maximize();
		
		//2) Navigating to the Revenue Calculator Page:
		driver.findElement(By.xpath("//div[text()='Revenue Calculator']")).click();
		
		//3) Scroll Down to the Slider section:
		JavascriptExecutor js=(JavascriptExecutor)driver;
		WebElement ele=driver.findElement(By.xpath("//h4[text()='Medicare Eligible Patients']"));
		js.executeScript("arguments[0].scrollIntoView();", ele);
		
		//4) Adjust the Slider
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement sliderTrack = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".MuiSlider-rail")));
        int sliderWidth = sliderTrack.getSize().getWidth();
        
        WebElement sliderThumb = driver.findElement(By.cssSelector(".MuiSlider-thumb"));
		
		WebElement sliderValueElement = driver.findElement(By.cssSelector(".MuiInputBase-input"));
		int currentValue = Integer.parseInt(sliderValueElement.getAttribute("value"));
		int minValue = Integer.parseInt(sliderValueElement.getAttribute("min"));
		int maxValue = Integer.parseInt(sliderValueElement.getAttribute("max"));
		int targetValue = 820; 
		
		double pixelsPerUnit = (double) sliderWidth / (maxValue - minValue);
		int pixelOffset = (int) ((targetValue - currentValue) * pixelsPerUnit);
		
		Actions actions = new Actions(driver);
		actions.clickAndHold(sliderThumb).moveByOffset(pixelOffset, 0).release().perform();
		
		//5) Update the Text Field:
		WebElement inputField = driver.findElement(By.cssSelector("input[type='number']"));
		inputField.click(); 
		inputField.sendKeys(Keys.CONTROL + "a"); 
		inputField.sendKeys(Keys.BACK_SPACE); 
		inputField.sendKeys("560"); 
		
		//6) Validate Slider Value
		String actualSliderValue=sliderValueElement.getAttribute("value");
		String expectedSliderValue="560";
		if (actualSliderValue.equals(expectedSliderValue)) {
            System.out.println("Validation Passed: Slider value is matched with the Text Field");
        } else {
            System.out.println("Validation Failed: Expected " + expectedSliderValue + " but got " + actualSliderValue);
        }
		
		//7) Select CPT Codes
		driver.findElement(By.xpath("//div[@class='MuiBox-root css-1p19z09']/div[1]//label[1]/span[1]/input")).click();
		driver.findElement(By.xpath("//div[@class='MuiBox-root css-1p19z09']/div[2]//label[1]/span[1]/input")).click();
		driver.findElement(By.xpath("//div[@class='MuiBox-root css-1p19z09']/div[3]//label[1]/span[1]/input")).click();
		driver.findElement(By.xpath("//div[@class='MuiBox-root css-1p19z09']/div[8]//label[1]/span[1]/input")).click();
		
		//8) Validate Total Recurring Reimbursement
		WebElement input = driver.findElement(By.xpath("//input[@type='number']"));
		js.executeScript("arguments[0].click();", input);
		input.sendKeys(Keys.CONTROL+ "a");
		inputField.sendKeys(Keys.BACK_SPACE); 
		inputField.sendKeys("820"); 
		
		WebElement e=driver.findElement(By.xpath("//p[normalize-space()='Selected CPT Codes']"));
		js.executeScript("arguments[0].scrollIntoView();", e);
		
		WebElement reimbursementElement=driver.findElement(By.xpath("//div[@class='MuiToolbar-root MuiToolbar-gutters MuiToolbar-regular css-1lnu3ao']//p[4]//p[1]"));
		String actualValue = reimbursementElement.getText();
		String expectedValue = "$110700";
		assert actualValue.equals(expectedValue) : "Validation failed!";
		System.out.println("Assertion passed. The Total Recurring Reimbursement value is correct.");
		
		//9) Verifying the Total Recurring Reimbursement for all Patients Per Month: shows the value $110700.
		assert reimbursementElement.getText().contains("$110700") : "Header value is incorrect!";
		System.out.println("Assertion passed. Header value is correct!");
		
		driver.quit();


}
}