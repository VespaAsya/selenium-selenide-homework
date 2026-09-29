import com.codeborne.selenide.Configuration;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;

@Tag("ui")
public class WebinarUiTest {

    String shopUrl = "http://localhost:8080/";
    String adminUrl = "http://admin:secret123@localhost:8080/admin";

    Long firstProductId;
    Long secondProductId;

    @BeforeEach
    void setUp() {
        Configuration.browser = "chrome";
        Configuration.timeout = 5000;
    }

    @AfterEach
    void afterTest() {
        if (firstProductId != null) {
            deleteProduct(firstProductId);
        }

        if (secondProductId != null) {
            deleteProduct(secondProductId);
        }

        closeWebDriver();
    }

    @Test
    void buyThreeProductsTest() {
        open(shopUrl);

        $(".product-card[data-name='стакан'] .qty-input")
                .setValue("3");

        $(".product-card[data-name='стакан'] " +
                "button[data-action='add-to-cart']")
                .click();

        $("#open-cart-btn").click();

        $("#total-price").shouldBe(text("39"));

        $("#makeOrder").click();

        $(".toast").shouldBe(visible);
    }

    @Test
    void checkTotalPriceTest() {
        String firstName =
                "API товар 1 " + System.currentTimeMillis();

        String secondName =
                "API товар 2 " + System.currentTimeMillis();

        firstProductId = addProductByApi(firstName, 40);
        secondProductId = addProductByApi(secondName, 60);

        open(shopUrl);

        $(".product-card[data-name='" + firstName + "'] " +
                "button[data-action='add-to-cart']")
                .click();

        $(".product-card[data-name='" + secondName + "'] " +
                "button[data-action='add-to-cart']")
                .click();

        $("#open-cart-btn").click();

        $("#total-price").shouldBe(text("100"));
    }

    @Test
    void addProductAndCheckNotificationTest() {
        String productName =
                "товар уведомление " + System.currentTimeMillis();

        open(adminUrl);

        $("#n-name").setValue(productName);
        $("#n-price").setValue("70");
        $("#add-btn").click();

        $(".toast").shouldBe(visible);
    }

    @Test
    void editProductTest() {
        String oldName =
                "товар для изменения " + System.currentTimeMillis();

        String newName =
                "измененный товар " + System.currentTimeMillis();

        firstProductId = addProductByApi(oldName, 80);

        open(adminUrl);

        $("#nm-" + firstProductId)
                .setValue(newName);

        $("#pr-" + firstProductId)
                .setValue("90");

        $("button[data-action='update'][data-id='" +
                firstProductId + "']")
                .click();

        $(".toast").shouldBe(visible);

        open(shopUrl);

        $(".product-card[data-name='" + newName + "']")
                .shouldBe(visible)
                .shouldBe(text("90 ₽"));
    }

    long addProductByApi(String name, int price) {
        String body = "{"
                + "\"name\":\"" + name + "\","
                + "\"price\":" + price
                + "}";

        Response response =
                given()
                        .baseUri("http://localhost:8080")
                        .auth()
                        .preemptive()
                        .basic("admin", "secret123")
                        .contentType("application/json")
                        .body(body)
                .when()
                        .post("/goods/add");

        return response
                .jsonPath()
                .getLong("data.id");
    }

    void deleteProduct(long id) {
        given()
                .baseUri("http://localhost:8080")
                .auth()
                .preemptive()
                .basic("admin", "secret123")
        .when()
                .delete("/goods/" + id)
        .then()
                .statusCode(200);
    }
}
