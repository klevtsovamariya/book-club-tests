package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationValidationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@Feature("Регистрация")
@Tag("api")
@DisplayName("Регистрация")
public class RegistrationTests extends TestBase {

    String username;
    String password;
    RegistrationBodyModel registrationData;

    @BeforeEach
    public void prepareTestData() {
        username = "user_" + faker.internet().uuid().replace("-", "");
        password = "pass_" + faker.number().digits(8);
        registrationData = new RegistrationBodyModel(username, password);
    }

    @DisplayName("Успешная регистрация")
    @Test
    @Description("POST /users/register/ с уникальными username/password создаёт пользователя.")
    @Severity(SeverityLevel.CRITICAL)
    public void successfulRegistrationTest() {
        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

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
    @Description("Повторная регистрация того же username возвращает ошибку существующего пользователя.")
    @Severity(SeverityLevel.NORMAL)
    public void existingUserWrongRegistrationTest() {
        api.users.register(registrationData);

        ExistingUserResponseModel secondRegistrationResponse = api.users.registerExistingUser(registrationData);

        step("Проверить текст ошибки существующего пользователя", () -> {
            assertThat(secondRegistrationResponse.username().get(0)).isEqualTo(REGISTRATION_EXISTING_USER_ERROR);
        });
    }

    @DisplayName("Регистрация без username")
    @Test
    public void registrationWithoutUsernameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", password);

        RegistrationValidationErrorResponseModel response = api.users.registerWithoutUsername(registrationData);

        step("Проверить ошибку username", () -> {
            assertThat(response.username()).isNotNull().isNotEmpty();
            assertThat(response.username().get(0)).isEqualTo(EMPTY_ERROR);
        });
    }

    @DisplayName("Регистрация без password")
    @Test
    public void registrationWithoutPasswordTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, "");

        RegistrationValidationErrorResponseModel response = api.users.registerWithoutPassword(registrationData);

        step("Проверить ошибку password", () -> {
            assertThat(response.password()).isNotNull().isNotEmpty();
            assertThat(response.password().get(0)).isEqualTo(EMPTY_ERROR);
        });
    }

    @DisplayName("Регистрация без username и password")
    @Test
    public void registrationWithoutCredentialsTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", "");

        RegistrationValidationErrorResponseModel response = api.users.registerWithoutCredentials(registrationData);

        step("Проверить ошибки обоих обязательных полей", () -> {
            assertThat(response.username()).isNotNull().isNotEmpty();
            assertThat(response.username().get(0)).isEqualTo(EMPTY_ERROR);
            assertThat(response.password()).isNotNull().isNotEmpty();
            assertThat(response.password().get(0)).isEqualTo(EMPTY_ERROR);
        });
    }
}
