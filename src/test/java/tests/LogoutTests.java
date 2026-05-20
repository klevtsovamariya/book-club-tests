package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import models.login.LoginBodyModel;
import models.logout.LogoutBodyModel;
import models.logout.LogoutValidationErrorResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@Feature("Выход из системы")
@DisplayName("Выход из системы")
public class LogoutTests extends TestBase {

    @DisplayName("Успешный logout")
    @Test
    @Tag("api")
    @Description("Логин по API, затем POST /auth/logout/ с валидным refresh токеном.")
    @Severity(SeverityLevel.CRITICAL)
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        step("Проверить, что refresh token получен", () -> {
            assertThat(refreshToken).startsWith(LOGIN_TOKEN_PREFIX);
        });

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        api.auth.logout(logoutData);
    }

    @DisplayName("Logout с невалидным refresh token")
    @Test
    @Tag("api")
    @Description("POST /auth/logout/ с невалидным refresh токеном возвращает ожидаемую ошибку.")
    @Severity(SeverityLevel.NORMAL)
    public void logoutWithInvalidRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("invalid_refresh_token");

        LogoutValidationErrorResponseModel response = api.auth.logoutUnauthorized(logoutData);

        step("Проверить текст ошибки token_not_valid", () -> {
            assertThat(response.detail()).isEqualTo(LOGOUT_INVALID_TOKEN_ERROR);
        });
    }

    @DisplayName("Logout без refresh token")
    @Test
    @Tag("api")
    @Description("POST /auth/logout/ без refresh токена возвращает ошибку обязательного поля.")
    @Severity(SeverityLevel.NORMAL)
    public void logoutWithoutRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("");

        LogoutValidationErrorResponseModel response = api.auth.logoutInvalid(logoutData);

        step("Проверить ошибку обязательного поля refresh", () -> {
            assertThat(response.refresh()).isNotNull().isNotEmpty();
            assertThat(response.refresh().get(0)).isEqualTo(EMPTY_ERROR);
        });
    }

    @DisplayName("UI logout для существующего пользователя")
    @Test
    @Tag("ui")
    @Description("Сессия существующего пользователя подготавливается через API/localStorage, " +
            "после нажатия logout открывается страница входа.")
    @Severity(SeverityLevel.CRITICAL)
    public void uiLogoutForExistingUserTest() {
        profilePage.openBlankPageWithExistingUser();
        profilePage.openPage()
                .pressLogoutButton()
                .loginPageShouldBeOpened();
    }
}
