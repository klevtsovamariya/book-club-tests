package tests.update.patch;

import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.update.UpdateUserPatchBodyModel;
import models.update.UpdateUserResponseModel;
import models.update.UpdateUserValidationErrorResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;

import static io.qameta.allure.Allure.step;
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
        api.registration.register(registrationData);
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

        UpdateUserResponseModel response = api.users.updateUserPatch(accessToken, requestBody);

        step("Проверить, что изменилось только firstName", () -> {
            assertThat(response.firstName()).isEqualTo(requestBody.firstName());
        });
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

        UpdateUserValidationErrorResponseModel response = api.users.updateUserPatchInvalid(accessToken, requestBody);

        step("Проверить текст ошибки email", () -> {
            assertThat(response.email()).isNotNull().isNotEmpty();
            assertThat(response.email().get(0)).isEqualTo(INVALID_EMAIL_ERROR);
        });
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

        UpdateUserValidationErrorResponseModel response = api.users.updateUserPatchUnauthorized(requestBody);

        step("Проверить текст ошибки отсутствия авторизации", () -> {
            assertThat(response.detail()).isEqualTo(UNAUTHORIZED_ERROR);
        });
    }
}
