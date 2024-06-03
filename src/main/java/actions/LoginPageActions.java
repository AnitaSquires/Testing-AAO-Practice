package actions;

import objects.LoginPageObjects;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPageActions {


    private final WebDriver driver;

    public LoginPageActions(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement getElement(By by) {
        return driver.findElement(by);
    }

    public void enterText(By by, String text) {
        getElement(by).sendKeys(text);
    }

    public void enterLoginCredentials(String userName, String password) {
        enterText(LoginPageObjects.USER_NAME, userName);
        enterText(LoginPageObjects.PASSWORD, password);
        //click log in button
    }
}
