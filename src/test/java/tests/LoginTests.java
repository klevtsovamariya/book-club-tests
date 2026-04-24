package tests;

import models.login.LoginBodyModel;
import models.login.LoginValidationErrorResponseModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class LoginTests extends TestBase {

    @Test
    public void successfulLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        Allure.step("Отправить запрос на успешный логин");
        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();
        Allure.step("Проверить, что токены получены и отличаются");
        assertThat(actualAccess).startsWith(LOGIN_TOKEN_PREFIX);
        assertThat(actualRefresh).startsWith(LOGIN_TOKEN_PREFIX);
        assertThat(actualAccess).isNotEqualTo(actualRefresh);
    }

    @Test
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD);

        Allure.step("Отправить запрос с неверным паролем");
        WrongCredentialsLoginResponseModel loginResponse = api.auth.loginWrongCredentials(loginData);

        String expectedDetailError = LOGIN_WRONG_CREDENTIALS_ERROR;
        String actualDetailError = loginResponse.detail();
        Allure.step("Проверить текст ошибки авторизации");
        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }

    @Test
    public void withoutLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel("", LOGIN_PASSWORD);

        Allure.step("Отправить запрос без username");
        LoginValidationErrorResponseModel loginResponse = api.auth.loginWithoutUsername(loginData);

        String expectedUsernameError = EMPTY_ERROR;
        Allure.step("Проверить ошибку в поле username");
        String actualUsernameError = loginResponse.username().get(0);
        assertThat(loginResponse.username()).isNotNull().isNotEmpty();
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
    }

    @Test
    public void withoutPasswordLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, "");

        Allure.step("Отправить запрос без password");
        LoginValidationErrorResponseModel loginResponse = api.auth.loginWithoutPassword(loginData);

        String expectedPasswordError = EMPTY_ERROR;
        Allure.step("Проверить ошибку в поле password");
        String actualPasswordError = loginResponse.password().get(0);
        assertThat(loginResponse.password()).isNotNull().isNotEmpty();
        assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
    }
}