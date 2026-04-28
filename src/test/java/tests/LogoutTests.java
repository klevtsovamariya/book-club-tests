package tests;

import io.qameta.allure.Allure;
import models.login.LoginBodyModel;
import models.logout.LogoutBodyModel;
import models.logout.LogoutValidationErrorResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@DisplayName("Выход из системы")
public class LogoutTests extends TestBase {

    @DisplayName("Успешный logout")
    @Test
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String refreshToken = Allure.step(
                "Получить refresh token через логин",
                () -> api.auth.loginAndGetRefreshToken(loginData)
        );

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        Allure.step("Отправить logout с валидным refresh token", () -> {
            api.auth.logout(logoutData);
        });
    }

    @DisplayName("Logout с невалидным refresh token")
    @Test
    public void logoutWithInvalidRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("invalid_refresh_token");

        LogoutValidationErrorResponseModel response = Allure.step(
                "Отправить logout с невалидным refresh token",
                () -> api.auth.logoutUnauthorized(logoutData)
        );

        Allure.step("Проверить текст ошибки token_not_valid");
        assertThat(response.detail()).isEqualTo(LOGOUT_INVALID_TOKEN_ERROR);
    }

    @DisplayName("Logout без refresh token")
    @Test
    public void logoutWithoutRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("");

        LogoutValidationErrorResponseModel response = Allure.step(
                "Отправить logout без refresh token",
                () -> api.auth.logoutInvalid(logoutData)
        );

        Allure.step("Проверить ошибку обязательного поля refresh");
        assertThat(response.refresh()).isNotNull().isNotEmpty();
        assertThat(response.refresh().get(0)).isEqualTo(EMPTY_ERROR);
    }
}
