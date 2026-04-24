package tests.update.patch;

import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.update.UpdateUserPatchBodyModel;
import models.update.UpdateUserResponseModel;
import models.update.UpdateUserValidationErrorResponseModel;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.TestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.INVALID_EMAIL_ERROR;
import static tests.TestData.UNAUTHORIZED_ERROR;

public class UpdateUserPatchTests extends TestBase {
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
    public void patchUpdateFirstNameOnlyTest() {
        UpdateUserPatchBodyModel requestBody = new UpdateUserPatchBodyModel(
                username,
                "Name",
                "",
                ""
        );

        Allure.step("Отправить PATCH-запрос на частичное обновление");
        UpdateUserResponseModel response = api.users.updateUserPatch(accessToken, requestBody);

        String expectedFirstName = requestBody.firstName();
        String actualFirstName = response.firstName();
        Allure.step("Проверить, что изменилось только firstName");
        assertThat(actualFirstName).isEqualTo(expectedFirstName);
    }

    @Test
    public void patchUpdateUserWithInvalidEmailOnlyTest() {
        UpdateUserPatchBodyModel requestBody = new UpdateUserPatchBodyModel(
                username,
                "Name",
                "",
                "wrong!"
        );

        Allure.step("Отправить PATCH-запрос с невалидным email");
        UpdateUserValidationErrorResponseModel response = api.users.updateUserPatchInvalid(accessToken, requestBody);

        String expectedEmailError = INVALID_EMAIL_ERROR;
        assertThat(response.email()).isNotNull().isNotEmpty();
        Allure.step("Проверить текст ошибки email");
        String actualEmailError = response.email().get(0);
        assertThat(actualEmailError).isEqualTo(expectedEmailError);
    }

    @Test
    public void patchUpdateUserWithoutAuthTest() {
        UpdateUserPatchBodyModel requestBody = new UpdateUserPatchBodyModel(
                username,
                "NO",
                "",
                ""
        );

        Allure.step("Отправить PATCH-запрос без авторизации");
        UpdateUserValidationErrorResponseModel response = api.users.updateUserPatchUnauthorized(requestBody);

        String expectedDetailError = UNAUTHORIZED_ERROR;
        Allure.step("Проверить текст ошибки отсутствия авторизации");
        String actualDetailError = response.detail();
        assertThat(actualDetailError).isEqualTo(expectedDetailError);
    }
}
