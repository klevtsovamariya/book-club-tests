package tests;

import models.login.LoginBodyModel;
import models.logout.LogoutBodyModel;
import models.logout.LogoutValidationErrorResponseModel;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.EMPTY_ERROR;
import static tests.TestData.LOGOUT_INVALID_TOKEN_ERROR;
import static tests.TestData.LOGIN_PASSWORD;
import static tests.TestData.LOGIN_USERNAME;

public class LogoutTests extends TestBase {

    @Test
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        Allure.step("Получить refresh token через логин");
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        Allure.step("Отправить logout с валидным refresh token");
        api.auth.logout(logoutData);
    }

    @Test
    public void logoutWithInvalidRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("invalid_refresh_token");

        Allure.step("Отправить logout с невалидным refresh token");
        LogoutValidationErrorResponseModel response = api.auth.logoutUnauthorized(logoutData);

        String expectedDetailError = LOGOUT_INVALID_TOKEN_ERROR;
        Allure.step("Проверить текст ошибки token_not_valid");
        String actualDetailError = response.detail();
        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }

    @Test
    public void logoutWithoutRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("");

        Allure.step("Отправить logout без refresh token");
        LogoutValidationErrorResponseModel response = api.auth.logoutInvalid(logoutData);

        String expectedRefreshError = EMPTY_ERROR;
        Allure.step("Проверить ошибку обязательного поля refresh");
        String actualRefreshError = response.refresh().get(0);
        assertThat(response.refresh()).isNotNull().isNotEmpty();
        assertThat(actualRefreshError).isEqualTo(expectedRefreshError);
    }
}
