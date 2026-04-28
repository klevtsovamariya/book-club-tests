package tests.update.patch;

import io.qameta.allure.Allure;
import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.update.UpdateUserPatchBodyModel;
import models.update.UpdateUserResponseModel;
import models.update.UpdateUserValidationErrorResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@DisplayName("PATCH обновление пользователя")
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

    @DisplayName("PATCH обновляет только firstName")
    @Test
    public void patchUpdateFirstNameOnlyTest() {
        UpdateUserPatchBodyModel requestBody = new UpdateUserPatchBodyModel(
                username,
                "Name",
                "",
                ""
        );

        UpdateUserResponseModel response = Allure.step(
                "Отправить PATCH-запрос на частичное обновление",
                () -> api.users.updateUserPatch(accessToken, requestBody)
        );

        Allure.step("Проверить, что изменилось только firstName");
        assertThat(response.firstName()).isEqualTo(requestBody.firstName());
    }

    @DisplayName("PATCH с невалидным email")
    @Test
    public void patchUpdateUserWithInvalidEmailOnlyTest() {
        UpdateUserPatchBodyModel requestBody = new UpdateUserPatchBodyModel(
                username,
                "Name",
                "",
                "wrong!"
        );

        UpdateUserValidationErrorResponseModel response = Allure.step(
                "Отправить PATCH-запрос с невалидным email",
                () -> api.users.updateUserPatchInvalid(accessToken, requestBody)
        );

        assertThat(response.email()).isNotNull().isNotEmpty();
        Allure.step("Проверить текст ошибки email");
        assertThat(response.email().get(0)).isEqualTo(INVALID_EMAIL_ERROR);
    }

    @DisplayName("PATCH без авторизации")
    @Test
    public void patchUpdateUserWithoutAuthTest() {
        UpdateUserPatchBodyModel requestBody = new UpdateUserPatchBodyModel(
                username,
                "NO",
                "",
                ""
        );

        UpdateUserValidationErrorResponseModel response = Allure.step(
                "Отправить PATCH-запрос без авторизации",
                () -> api.users.updateUserPatchUnauthorized(requestBody)
        );

        Allure.step("Проверить текст ошибки отсутствия авторизации");
        assertThat(response.detail()).isEqualTo(UNAUTHORIZED_ERROR);
    }
}
