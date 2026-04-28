package tests;

import io.qameta.allure.Allure;
import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationValidationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@DisplayName("Регистрация")
public class RegistrationTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        // оставляем генерацию данных в тесте, чтобы каждый запуск был с новыми пользователями
        username = "user_" + System.currentTimeMillis();
        password = "pass_" + System.currentTimeMillis();
    }

    @DisplayName("Успешная регистрация")
    @Test
    public void successfulRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = Allure.step(
                "Отправить запрос на регистрацию",
                () -> api.users.register(registrationData)
        );

        Allure.step("Проверить поля успешного ответа");
        assertThat(registrationResponse.id()).isGreaterThan(0);
        assertThat(registrationResponse.username()).isEqualTo(username);
        assertThat(registrationResponse.firstName()).isEqualTo("");
        assertThat(registrationResponse.lastName()).isEqualTo("");
        assertThat(registrationResponse.email()).isEqualTo("");
        assertThat(registrationResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
    }

    @DisplayName("Повторная регистрация существующего пользователя")
    @Test
    public void existingUserWrongRegistrationTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel firstRegistrationResponse = Allure.step(
                "Зарегистрировать пользователя",
                () -> api.users.register(registrationData)
        );

        assertThat(firstRegistrationResponse.username()).isEqualTo(username);

        ExistingUserResponseModel secondRegistrationResponse = Allure.step(
                "Повторно отправить регистрацию с теми же данными",
                () -> api.users.registerExistingUser(registrationData)
        );

        Allure.step("Проверить текст ошибки существующего пользователя");
        assertThat(secondRegistrationResponse.username().get(0)).isEqualTo(REGISTRATION_EXISTING_USER_ERROR);
    }

    @DisplayName("Регистрация без username")
    @Test
    public void registrationWithoutUsernameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", password);

        RegistrationValidationErrorResponseModel response = Allure.step(
                "Отправить регистрацию без username",
                () -> api.users.registerWithoutUsername(registrationData)
        );

        Allure.step("Проверить ошибку username");
        assertThat(response.username()).isNotNull().isNotEmpty();
        assertThat(response.username().get(0)).isEqualTo(EMPTY_ERROR);
    }

    @DisplayName("Регистрация без password")
    @Test
    public void registrationWithoutPasswordTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, "");

        RegistrationValidationErrorResponseModel response = Allure.step(
                "Отправить регистрацию без password",
                () -> api.users.registerWithoutPassword(registrationData)
        );

        Allure.step("Проверить ошибку password");
        assertThat(response.password()).isNotNull().isNotEmpty();
        assertThat(response.password().get(0)).isEqualTo(EMPTY_ERROR);
    }

    @DisplayName("Регистрация без username и password")
    @Test
    public void registrationWithoutCredentialsTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", "");

        RegistrationValidationErrorResponseModel response = Allure.step(
                "Отправить регистрацию без username и password",
                () -> api.users.registerWithoutCredentials(registrationData)
        );

        Allure.step("Проверить ошибки обоих обязательных полей");
        assertThat(response.username()).isNotNull().isNotEmpty();
        assertThat(response.username().get(0)).isEqualTo(EMPTY_ERROR);
        assertThat(response.password()).isNotNull().isNotEmpty();
        assertThat(response.password().get(0)).isEqualTo(EMPTY_ERROR);
    }
}
