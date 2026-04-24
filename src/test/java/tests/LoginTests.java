package tests;

import models.login.LoginBodyModel;
import models.login.LoginValidationErrorResponseModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class LoginTests extends TestBase {

    @Test
    public void successfulLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();
        assertThat(actualAccess).startsWith(LOGIN_TOKEN_PREFIX);
        assertThat(actualRefresh).startsWith(LOGIN_TOKEN_PREFIX);
        assertThat(actualAccess).isNotEqualTo(actualRefresh);
    }

    @Test
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, LOGIN_WRONG_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = api.auth.loginWrongCredentials(loginData);

        String expectedDetailError = LOGIN_WRONG_CREDENTIALS_ERROR;
        String actualDetailError = loginResponse.detail();
        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }

    @Test
    public void withoutLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel("", LOGIN_PASSWORD);

        LoginValidationErrorResponseModel loginResponse = api.auth.loginInvalid(loginData);

        String expectedUsernameError = EMPTY_ERROR;
        String actualUsernameError = loginResponse.username().get(0);
        assertThat(loginResponse.username()).isNotNull().isNotEmpty();
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
    }

    @Test
    public void withoutPasswordLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(LOGIN_USERNAME, "");

        LoginValidationErrorResponseModel loginResponse = api.auth.loginInvalid(loginData);

        String expectedPasswordError = EMPTY_ERROR;
        String actualPasswordError = loginResponse.password().get(0);
        assertThat(loginResponse.password()).isNotNull().isNotEmpty();
        assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
    }

}
