package tests;

import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationValidationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@DisplayName("Регистрация")
public class RegistrationTests extends TestBase {

    String username;
    String password;
    RegistrationBodyModel registrationData;

    @BeforeEach
    public void prepareTestData(TestInfo testInfo) {
        username = "user_" + System.currentTimeMillis();
        password = "pass_" + System.currentTimeMillis();
        registrationData = new RegistrationBodyModel(username, password);

        testInfo.getTestMethod()
                .filter(method -> method.getName().equals("existingUserWrongRegistrationTest"))
                .ifPresent(method -> api.registration.register(registrationData));
    }

    @DisplayName("Успешная регистрация")
    @Test
    public void successfulRegistrationTest() {
        SuccessfulRegistrationResponseModel registrationResponse = api.registration.register(registrationData);

        step("Проверить поля успешного ответа", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");
            assertThat(registrationResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);
        });
    }

    @DisplayName("Повторная регистрация существующего пользователя")
    @Test
    public void existingUserWrongRegistrationTest() {
        ExistingUserResponseModel secondRegistrationResponse = api.registration.registerExistingUser(registrationData);

        step("Проверить текст ошибки существующего пользователя", () -> {
            assertThat(secondRegistrationResponse.username().get(0)).isEqualTo(REGISTRATION_EXISTING_USER_ERROR);
        });
    }

    @DisplayName("Регистрация без username")
    @Test
    public void registrationWithoutUsernameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", password);

        RegistrationValidationErrorResponseModel response = api.registration.registerWithoutUsername(registrationData);

        step("Проверить ошибку username", () -> {
            assertThat(response.username()).isNotNull().isNotEmpty();
            assertThat(response.username().get(0)).isEqualTo(EMPTY_ERROR);
        });
    }

    @DisplayName("Регистрация без password")
    @Test
    public void registrationWithoutPasswordTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, "");

        RegistrationValidationErrorResponseModel response = api.registration.registerWithoutPassword(registrationData);

        step("Проверить ошибку password", () -> {
            assertThat(response.password()).isNotNull().isNotEmpty();
            assertThat(response.password().get(0)).isEqualTo(EMPTY_ERROR);
        });
    }

    @DisplayName("Регистрация без username и password")
    @Test
    public void registrationWithoutCredentialsTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", "");

        RegistrationValidationErrorResponseModel response = api.registration.registerWithoutCredentials(registrationData);

        step("Проверить ошибки обоих обязательных полей", () -> {
            assertThat(response.username()).isNotNull().isNotEmpty();
            assertThat(response.username().get(0)).isEqualTo(EMPTY_ERROR);
            assertThat(response.password()).isNotNull().isNotEmpty();
            assertThat(response.password().get(0)).isEqualTo(EMPTY_ERROR);
        });
    }
}
