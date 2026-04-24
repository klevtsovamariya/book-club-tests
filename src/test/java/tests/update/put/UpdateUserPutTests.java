package tests.update.put;

import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.update.UpdateUserPutBodyModel;
import models.update.UpdateUserResponseModel;
import models.update.UpdateUserValidationErrorResponseModel;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.TestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.NULL_ERROR;

public class UpdateUserPutTests extends TestBase {
    private String username;
    private String password;
    private String accessToken;

    @BeforeEach
    public void prepareUserAndToken() {
        username = "upd_user_" + System.currentTimeMillis();
        password = "upd_pass_" + System.currentTimeMillis();

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        api.users.register(registrationData);
        accessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));
    }

    @Test
    public void putUpdateUserWithAllFieldsTest() {
        UpdateUserPutBodyModel requestBody = new UpdateUserPutBodyModel(
                username + "_new",
                "A",
                "B",
                "a.b@example.com"
        );

        Allure.step("Отправить PUT-запрос на полное обновление");
        UpdateUserResponseModel response = api.users.updateUserPut(accessToken, requestBody);

        String expectedUsername = requestBody.username();
        String actualUsername = response.username();
        String expectedFirstName = requestBody.firstName();
        String actualFirstName = response.firstName();
        String expectedLastName = requestBody.lastName();
        String actualLastName = response.lastName();
        String expectedEmail = requestBody.email();
        String actualEmail = response.email();
        Allure.step("Проверить, что все поля обновились");
        assertThat(actualUsername).isEqualTo(expectedUsername);
        assertThat(actualFirstName).isEqualTo(expectedFirstName);
        assertThat(actualLastName).isEqualTo(expectedLastName);
        assertThat(actualEmail).isEqualTo(expectedEmail);
    }

    @Test
    public void putUpdateUserWithoutRequiredFieldsTest() {
        UpdateUserPutBodyModel requestBody = new UpdateUserPutBodyModel(
                null,
                "Name",
                null,
                null
        );

        Allure.step("Отправить PUT-запрос с пустыми обязательными полями");
        UpdateUserValidationErrorResponseModel response = api.users.updateUserPutInvalid(accessToken, requestBody);

        String expectedUsernameError = NULL_ERROR;
        String actualUsernameError = response.username().get(0);
        String expectedLastNameError = NULL_ERROR;
        String actualLastNameError = response.lastName().get(0);
        String expectedEmailError = NULL_ERROR;
        String actualEmailError = response.email().get(0);
        Allure.step("Проверить ошибки обязательных полей");
        assertThat(response.username()).isNotNull().isNotEmpty();
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(response.firstName()).isNull();
        assertThat(response.lastName()).isNotNull().isNotEmpty();
        assertThat(actualLastNameError).isEqualTo(expectedLastNameError);
        assertThat(response.email()).isNotNull().isNotEmpty();
        assertThat(actualEmailError).isEqualTo(expectedEmailError);
    }
}
