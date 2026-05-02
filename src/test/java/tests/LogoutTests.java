package tests;

import models.login.LoginBodyModel;
import models.logout.LogoutBodyModel;
import models.logout.LogoutValidationErrorResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@DisplayName("Выход из системы")
public class LogoutTests extends TestBase {

    @DisplayName("Успешный logout")
    @Test
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);
        String refreshToken = step(
                "Получить refresh token через логин",
                () -> api.auth.loginAndGetRefreshToken(loginData)
        );

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        step("Отправить logout с валидным refresh token", () -> {
            api.auth.logout(logoutData);
        });
    }

    @DisplayName("Logout с невалидным refresh token")
    @Test
    public void logoutWithInvalidRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("invalid_refresh_token");

        LogoutValidationErrorResponseModel response = step(
                "Отправить logout с невалидным refresh token",
                () -> api.auth.logoutUnauthorized(logoutData)
        );

        step("Проверить текст ошибки token_not_valid", () -> {
            assertThat(response.detail()).isEqualTo(LOGOUT_INVALID_TOKEN_ERROR);
        });
    }

    @DisplayName("Logout без refresh token")
    @Test
    public void logoutWithoutRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("");

        LogoutValidationErrorResponseModel response = step(
                "Отправить logout без refresh token",
                () -> api.auth.logoutInvalid(logoutData)
        );

        step("Проверить ошибку обязательного поля refresh", () -> {
            assertThat(response.refresh()).isNotNull().isNotEmpty();
            assertThat(response.refresh().get(0)).isEqualTo(EMPTY_ERROR);
        });
    }
}
