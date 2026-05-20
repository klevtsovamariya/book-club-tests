package tests;

import allure.Attach;
import api.ApiClient;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import pages.ClubPage;
import pages.ClubsPage;
import pages.ProfilePage;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.webdriver;

public class TestBase {

    protected static final ApiClient api = new ApiClient();
    protected final Faker faker = new Faker();
    protected final ClubsPage clubsPage = new ClubsPage();
    protected final ClubPage clubPage = new ClubPage();
    protected final ProfilePage profilePage = new ProfilePage();

    @BeforeAll
    public static void setUp() {
        String baseUrl = TestConfig.baseUrl();

        RestAssured.baseURI = baseUrl;
        RestAssured.basePath = "/api/v1";

        Configuration.baseUrl = baseUrl;
        System.setProperty("selenide.baseUrl", baseUrl);
        Configuration.browser = TestConfig.browser();
        Configuration.browserSize = TestConfig.browserSize();
        Configuration.browserVersion = TestConfig.browserVersion();
        Configuration.headless = TestConfig.headless();
        Configuration.timeout = 10000;
        Configuration.pageLoadStrategy = "eager";

        String remote = TestConfig.remoteUrl();
        if (!remote.isEmpty()) {
            Configuration.remote = remote;
        }
    }

    @BeforeEach
    public void addAllureSelenideListener() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @AfterEach
    public void addUiAttachments() {
        if (webdriver().driver().hasWebDriverStarted()) {
            Attach.screenshotAs("Last screenshot");
            Attach.pageSource();
            Attach.browserConsoleLogs();
            closeWebDriver();
        }
    }
}
