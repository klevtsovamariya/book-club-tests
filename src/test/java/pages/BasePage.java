package pages;

import api.ApiClient;
import io.qameta.allure.Step;
import models.localstorage.LocalStorageAuthModel;
import models.localstorage.UserData;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;

import static com.codeborne.selenide.Selenide.localStorage;
import static com.codeborne.selenide.Selenide.open;
import static tests.TestData.LOGIN_ID;
import static tests.TestData.LOGIN_PASSWORD;
import static tests.TestData.LOGIN_USERNAME;

public class BasePage {
    protected final ApiClient api = new ApiClient();

    @Step("[UI] Регистрация пользователя, установка сессии и открытие страницы")
    public SuccessfulLoginResponseModel openBlankPageWithNewUser() {
        Faker faker = new Faker();
        String username = faker.name().firstName() + faker.name().lastName();
        String password = "12345";

        RegistrationBodyModel registrationBody = new RegistrationBodyModel(username, password);
        SuccessfulRegistrationResponseModel user = api.users.register(registrationBody);

        LoginBodyModel loginBody = new LoginBodyModel(username, password);
        SuccessfulLoginResponseModel login = api.auth.login(loginBody);

        UserData userData = new UserData(
                user.id(),
                user.username(),
                user.firstName(),
                user.lastName(),
                user.email(),
                user.remoteAddr()
        );
        LocalStorageAuthModel authBody = new LocalStorageAuthModel(
                userData,
                login.access(),
                login.refresh(),
                true
        );

        openFaviconAndSetLocalStorage("book_club_auth", authBody.toJson());

        return login;
    }

    @Step("[UI] Открытие страницы с существующим пользователем")
    public void openBlankPageWithExistingUser() {
        LoginBodyModel loginBody = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        SuccessfulLoginResponseModel login = api.auth.login(loginBody);

        UserData userData = new UserData(
                Integer.parseInt(LOGIN_ID),
                LOGIN_USERNAME,
                "",
                "",
                "",
                ""
        );
        LocalStorageAuthModel authBody = new LocalStorageAuthModel(
                userData,
                login.access(),
                login.refresh(),
                true
        );

        openFaviconAndSetLocalStorage("book_club_auth", authBody.toJson());
    }

    @Step("[UI] Открытие /favicon.ico и установка данных в localstorage")
    public void openFaviconAndSetLocalStorage(String key, String value) {
        openFavicon();
        setLocalStorage(key, value);
    }

    @Step("[UI] Установка данных в localstorage")
    public void setLocalStorage(String key, String value) {
        localStorage().setItem(key, value);
    }

    @Step("[UI] Открытие /favicon.ico")
    public void openFavicon() {
        open("/favicon.ico");
    }
}
