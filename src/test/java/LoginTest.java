import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class LoginTest {

    @Test
    void test_login_with_incorrect_credentials() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        
        try {
            System.out.println("Navigating to login page...");
            driver.get("http://103.139.122.250:4000/login");
            
            // Wait for page to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            
            // Wait for any input field to be present
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("input")));
            
            System.out.println("Page loaded, looking for email field...");
            
            // Try multiple selectors for email field
            Thread.sleep(2000); // Wait a bit more for React to render
            
            // Email field
            try {
                driver.findElement(By.name("email")).sendKeys("qasim@malik.com");
                System.out.println("Found email by name 'email'");
            } catch (Exception e) {
                try {
                    driver.findElement(By.cssSelector("input[type='email']")).sendKeys("qasim@malik.com");
                    System.out.println("Found email by type='email'");
                } catch (Exception e2) {
                    driver.findElement(By.xpath("//input[contains(@name, 'email') or contains(@id, 'email') or contains(@placeholder, 'Email')]")).sendKeys("qasim@malik.com");
                    System.out.println("Found email by xpath pattern");
                }
            }
            
            // Password field
            try {
                driver.findElement(By.name("password")).sendKeys("abcdefg");
                System.out.println("Found password by name 'password'");
            } catch (Exception e) {
                try {
                    driver.findElement(By.cssSelector("input[type='password']")).sendKeys("abcdefg");
                    System.out.println("Found password by type='password'");
                } catch (Exception e2) {
                    driver.findElement(By.xpath("//input[contains(@name, 'pass') or contains(@id, 'pass') or contains(@placeholder, 'Password')]")).sendKeys("abcdefg");
                    System.out.println("Found password by xpath pattern");
                }
            }
            
            // Login button - try different selectors
            try {
                driver.findElement(By.id("m_login_signin_submit")).click();
                System.out.println("Clicked login by id");
            } catch (Exception e) {
                try {
                    driver.findElement(By.cssSelector("button[type='submit']")).click();
                    System.out.println("Clicked login by button type");
                } catch (Exception e2) {
                    driver.findElement(By.xpath("//button[contains(text(), 'Sign in') or contains(text(), 'Login')]")).click();
                    System.out.println("Clicked login by text");
                }
            }
            
            // Wait for error message
            Thread.sleep(3000);
            
            // Check for error message in page
            String pageSource = driver.getPageSource();
            boolean hasError = pageSource.contains("Incorrect") || 
                               pageSource.contains("incorrect") || 
                               pageSource.contains("error") ||
                               pageSource.contains("invalid");
            
            System.out.println("Page contains error message: " + hasError);
            assert(hasError);
            
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            // Take screenshot for debugging (optional)
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
    }
}
