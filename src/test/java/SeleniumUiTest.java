import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ui")
public class SeleniumUiTest {

    WebDriver driver;

    String shopUrl = "http://localhost:8080/";
    String adminUrl = "http://admin:secret123@localhost:8080/admin";

    @BeforeEach
    void openBrowser() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterEach
    void closeBrowser() {
        driver.quit();
    }

    @Test
    void addProductThroughAdminTest() {
        String productName = "selenium товар " + System.currentTimeMillis();

        driver.get(adminUrl);

        driver.findElement(By.id("n-name")).sendKeys(productName);
        driver.findElement(By.id("n-price")).sendKeys("50");
        driver.findElement(By.id("add-btn")).click();

        driver.get(shopUrl);

        boolean productExists =
                driver.findElements(
                        By.xpath("//h4[text()='" + productName + "']")
                ).size() > 0;

        assertTrue(productExists);
    }

    @Test
    void addProductToCartTest() {
        driver.get(shopUrl);

        driver.findElement(
                By.cssSelector(
                        ".product-card[data-name='стакан'] " +
                        "button[data-action='add-to-cart']"
                )
        ).click();

        driver.findElement(By.id("open-cart-btn")).click();

        WebElement cart = driver.findElement(By.id("cart-items"));

        assertTrue(cart.getText().contains("стакан"));
    }

    @Test
    void wrongAdminLoginTest() {
        driver.get("http://wrong:wrong@localhost:8080/admin");

        boolean adminPageOpened =
                driver.findElements(By.id("n-name")).size() > 0;

        assertFalse(adminPageOpened);
    }

    @Test
    void cartAfterRefreshTest() {
        driver.get(shopUrl);

        driver.findElement(
                By.cssSelector(
                        ".product-card[data-name='стакан'] " +
                        "button[data-action='add-to-cart']"
                )
        ).click();

        driver.navigate().refresh();

        driver.findElement(By.id("open-cart-btn")).click();

        WebElement cart = driver.findElement(By.id("cart-items"));

        assertTrue(cart.getText().contains("стакан"));
    }
}
