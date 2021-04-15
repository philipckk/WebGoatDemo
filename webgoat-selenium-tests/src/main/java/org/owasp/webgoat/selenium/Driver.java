package org.owasp.webgoat.selenium;


import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

public class Driver {

    static private final long DefaultTimeoutInSeconds = 5;
    static private Driver __onlyInstance = null;

    private ChromeDriver driver = null;
    private String url = null;


    // Singleton class factory
    static synchronized Driver getOnlyInstance(String driver) {
        __onlyInstance = new Driver(driver);

        return __onlyInstance;
    }

    // Private constructor
    private Driver(String driver) {
        System.setProperty("webdriver.chrome.driver", driver);
    }

    // The Selenium invocation.
    public void invoke(String url, String user, String password, boolean register, boolean verbose) {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-logging");
        if (verbose) {
            options.addArguments("--verbose");
        }

        this.driver = new ChromeDriver(options);

        try {
            this.url = url;

            if (register) {
                register(user, password);
            } else {
                login(user, password);
            }

            invokeSQLiTestCases();

        } finally {
            driver.quit();
        }
    }

    private void register(String user, String password) {
        // Login
        driver.get(getBaseUrl() + "/registration");
        driver.manage().timeouts().implicitlyWait(DefaultTimeoutInSeconds, TimeUnit.SECONDS);

        driver.findElement(By.id("username")).sendKeys(user);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("matchingPassword")).sendKeys(password);
        driver.findElement(By.name("agree")).click();
        driver.findElement(By.className("btn-primary")).click();

    }

    private void login(String user, String password) {
        // Login
        driver.get(getBaseUrl() + "/login");
        driver.manage().timeouts().implicitlyWait(DefaultTimeoutInSeconds, TimeUnit.SECONDS);


        driver.findElement(By.name("username")).sendKeys(user);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.className("btn-primary")).click();
    }

    private void invokeSQLiTestCases() {
        // SQL Injection test
		System.out.println("run SQLi test");
		driver.get(url + "/start.mvc#lesson/SqlInjection.lesson/10");
        driver.manage().timeouts().implicitlyWait(DefaultTimeoutInSeconds, TimeUnit.SECONDS);


        driver.findElement(By.name("name")).sendKeys("a");
        driver.findElement(By.name("auth_tan")).sendKeys("' OR '1'='1");
        driver.findElement(By.xpath("//*[text()='Get department']")).click();
    }

    private String getBaseUrl() {
        return this.url;
    }
}
