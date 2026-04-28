package tests.update.put;

import io.qameta.allure.Allure;
import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.update.UpdateUserPutBodyModel;
import models.update.UpdateUserResponseModel;
import models.update.UpdateUserValidationErrorResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.TestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@DisplayName("PUT обновление пользователя")
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

    @DisplayName("PUT обновляет пользователя всеми полями")
    @Test
    public void putUpdateUserWithAllFieldsTest() {
        UpdateUserPutBodyModel requestBody = new UpdateUserPutBodyModel(
                username + "_new",
                "A",
                "B",
                "a.b@example.com"
        );


        UpdateUserResponseModel response = Allure.step(
                "Отправить PUT-запрос на полное обновление",
                () -> api.users.updateUserPut(accessToken, requestBody)
        );

        Allure.step("Проверить, что все поля обновились");
        assertThat(response.username()).isEqualTo(requestBody.username());
        assertThat(response.firstName()).isEqualTo(requestBody.firstName());
        assertThat(response.lastName()).isEqualTo(requestBody.lastName());
        assertThat(response.email()).isEqualTo(requestBody.email());
    }

    @DisplayName("PUT без обязательных полей")
    @Test
    public void putUpdateUserWithoutRequiredFieldsTest() {
        UpdateUserPutBodyModel requestBody = new UpdateUserPutBodyModel(
                null,
                "Name",
                null,
                null
        );


        UpdateUserValidationErrorResponseModel response = Allure.step(
                "Отправить PUT-запрос с пустыми обязательными полями",
                () -> api.users.updateUserPutInvalid(accessToken, requestBody)
        );

        Allure.step("Проверить ошибки обязательных полей");
        assertThat(response.username()).isNotNull().isNotEmpty();
        assertThat(response.username().get(0)).isEqualTo(NULL_ERROR);
        assertThat(response.firstName()).isNull();
        assertThat(response.lastName()).isNotNull().isNotEmpty();
        assertThat(response.lastName().get(0)).isEqualTo(NULL_ERROR);
        assertThat(response.email()).isNotNull().isNotEmpty();
        assertThat(response.email().get(0)).isEqualTo(NULL_ERROR);
    }
}
