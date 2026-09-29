import com.codeborne.selenide.Configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@Tag("ui")
public class SelenideUiTest {

    String shopUrl = "http://localhost:8080/";
    String adminUrl = "http://admin:secret123@localhost:8080/admin";

    @BeforeEach
    void setUp() {
        Configuration.browser = "chrome";
        Configuration.timeout = 5000;
    }

    @AfterEach
    void closeBrowser() {
        closeWebDriver();
    }

    @Test
    void addProductThroughAdminTest() {
        String productName = "selenide товар " + System.currentTimeMillis();

        open(adminUrl);

        $("#n-name").setValue(productName);
        $("#n-price").setValue("60");
        $("#add-btn").click();

        open(shopUrl);

        $x("//h4[text()='" + productName + "']")
                .shouldBe(visible);
    }

    @Test
    void addProductToCartTest() {
        open(shopUrl);

        $(".product-card[data-name='стакан'] " +
                "button[data-action='add-to-cart']")
                .click();

        $("#open-cart-btn").click();

        $("#cart-items")
                .shouldBe(visible)
                .shouldBe(text("стакан"));
    }

    @Test
    void wrongAdminLoginTest() {
        open("http://wrong:wrong@localhost:8080/admin");

        $("#n-name").shouldBe(hidden);
    }

    @Test
    void cartAfterRefreshTest() {
        open(shopUrl);

        $(".product-card[data-name='стакан'] " +
                "button[data-action='add-to-cart']")
                .click();

        refresh();

        $("#open-cart-btn").click();

        $("#cart-items")
                .shouldBe(visible)
                .shouldBe(text("стакан"));
    }

    @Test
    void orderMoreThan300Test() {
        open(shopUrl);

        $(".product-card[data-name='стакан'] .qty-input")
                .setValue("24");

        $(".product-card[data-name='стакан'] " +
                "button[data-action='add-to-cart']")
                .click();

        $("#open-cart-btn").click();

        $("#total-price").shouldBe(text("312"));

        $("#makeOrder").click();

        confirm();
    }
}
