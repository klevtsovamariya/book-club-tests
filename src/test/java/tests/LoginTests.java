package tests;

import io.qameta.allure.Allure;
import models.login.LoginBodyModel;
import models.login.LoginValidationErrorResponseModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@DisplayName("Авторизация")
public class LoginTests extends TestBase {

    @DisplayName("Успешный логин")
    @Test
    public void successfulLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        SuccessfulLoginResponseModel loginResponse = Allure.step(
                "Отправить запрос на успешный логин",
                () -> api.auth.login(loginData)
        );

        Allure.step("Проверить, что токены получены и отличаются");
        assertThat(loginResponse.access()).startsWith(LOGIN_TOKEN_PREFIX);
        assertThat(loginResponse.refresh()).startsWith(LOGIN_TOKEN_PREFIX);
        assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
    }

    @DisplayName("Логин с неверным паролем")
    @Test
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = Allure.step(
                "Отправить запрос с неверным паролем",
                () -> api.auth.loginWrongCredentials(loginData)
        );

        Allure.step("Проверить текст ошибки авторизации");
        assertThat(loginResponse.detail()).isEqualTo(LOGIN_WRONG_CREDENTIALS_ERROR);
    }

    @DisplayName("Логин без username")
    @Test
    public void withoutLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel("", LOGIN_PASSWORD);

        LoginValidationErrorResponseModel loginResponse = Allure.step(
                "Отправить запрос без username",
                () -> api.auth.loginWithoutUsername(loginData)
        );

        Allure.step("Проверить ошибку в поле username");
        assertThat(loginResponse.username()).isNotNull().isNotEmpty();
        assertThat(loginResponse.username().get(0)).isEqualTo(EMPTY_ERROR);
    }

    @DisplayName("Логин без password")
    @Test
    public void withoutPasswordLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, "");

        LoginValidationErrorResponseModel loginResponse = Allure.step(
                "Отправить запрос без password",
                () -> api.auth.loginWithoutPassword(loginData)
        );

        Allure.step("Проверить ошибку в поле password");
        assertThat(loginResponse.password()).isNotNull().isNotEmpty();
        assertThat(loginResponse.password().get(0)).isEqualTo(EMPTY_ERROR);
    }
}
