package tests;

import actions.LoginPageActions;
import objects.LoginPageObjects;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.BrowserFactory;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginTests {
    final WebDriver driver = new BrowserFactory("chrome").getDriver();
    final String baseUrl = "https://www.saucedemo.com/";
    final LoginPageActions loginPageActions = new LoginPageActions(driver);

    @BeforeEach
    void beforeEach() {
        if (driver == null) {
            throw new RuntimeException("Driver not started");
        }

        driver.get(baseUrl);
    }

    @Test
    void LoginTests_pageDisplayed() {
        WebElement title = loginPageActions.getElement(LoginPageObjects.TITLE);
        WebElement userName = loginPageActions.getElement(LoginPageObjects.USER_NAME);
        WebElement password = loginPageActions.getElement(LoginPageObjects.PASSWORD);
        WebElement loginButton = loginPageActions.getElement(LoginPageObjects.LOGIN_BUTTON);

        assertAll(
                "all page objects displayed",
                () -> assertTrue(title.isDisplayed()),
                () -> assertTrue(userName.isDisplayed()),
                () -> assertTrue(password.isDisplayed()),
                () -> assertTrue(loginButton.isDisplayed())
        );
    }

    @Test
    void TestLoginFeatures() {
        loginPageActions.enterLoginCredentials("standard_user", "secret_sauce");
    }


    @AfterAll
    void afterAll() {
        if (driver != null) {
            driver.quit();
        }
    }
}
