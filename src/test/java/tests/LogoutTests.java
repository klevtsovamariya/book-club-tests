package tests;

import models.login.LoginBodyModel;
import models.logout.LogoutBodyModel;
import models.logout.LogoutValidationErrorResponseModel;
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
        String refreshToken = api.auth.loginAndGetRefreshToken(loginData);

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
        api.auth.logout(logoutData);
    }

    @Test
    public void logoutWithInvalidRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("invalid_refresh_token");

        LogoutValidationErrorResponseModel response = api.auth.logoutUnauthorized(logoutData);

        String expectedDetailError = LOGOUT_INVALID_TOKEN_ERROR;
        String actualDetailError = response.detail();
        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }

    @Test
    public void logoutWithoutRefreshTokenTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel("");

        LogoutValidationErrorResponseModel response = api.auth.logoutInvalid(logoutData);

        String expectedRefreshError = EMPTY_ERROR;
        String actualRefreshError = response.refresh().get(0);
        assertThat(response.refresh()).isNotNull().isNotEmpty();
        assertThat(actualRefreshError).isEqualTo(expectedRefreshError);
    }
}
