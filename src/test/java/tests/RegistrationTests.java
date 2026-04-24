package tests;

import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationValidationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseModel;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.EMPTY_ERROR;
import static tests.TestData.REGISTRATION_EXISTING_USER_ERROR;
import static tests.TestData.REGISTRATION_IP_REGEXP;

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

        Allure.step("Отправить запрос на регистрацию");
        SuccessfulRegistrationResponseModel registrationResponse =
                api.users.register(registrationData);

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

        Allure.step("Зарегистрировать пользователя");
        SuccessfulRegistrationResponseModel firstRegistrationResponse =
                api.users.register(registrationData);

        assertThat(firstRegistrationResponse.username()).isEqualTo(username);

        Allure.step("Повторно отправить регистрацию с теми же данными");
        ExistingUserResponseModel secondRegistrationResponse =
                api.users.registerExistingUser(registrationData);

        String expectedError = REGISTRATION_EXISTING_USER_ERROR;
        Allure.step("Проверить текст ошибки существующего пользователя");
        String actualError = secondRegistrationResponse.username().get(0);
        assertThat(actualError).isEqualTo(expectedError);
    }

    @DisplayName("Регистрация без username")
    @Test
    public void registrationWithoutUsernameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", password);

        Allure.step("Отправить регистрацию без username");
        RegistrationValidationErrorResponseModel response = api.users.registerWithoutUsername(registrationData);

        String expectedUsernameError = EMPTY_ERROR;
        Allure.step("Проверить ошибку username");
        String actualUsernameError = response.username().get(0);
        assertThat(response.username()).isNotNull().isNotEmpty();
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
    }

    @DisplayName("Регистрация без password")
    @Test
    public void registrationWithoutPasswordTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, "");

        Allure.step("Отправить регистрацию без password");
        RegistrationValidationErrorResponseModel response = api.users.registerWithoutPassword(registrationData);

        String expectedPasswordError = EMPTY_ERROR;
        Allure.step("Проверить ошибку password");
        String actualPasswordError = response.password().get(0);
        assertThat(response.password()).isNotNull().isNotEmpty();
        assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
    }

    @DisplayName("Регистрация без username и password")
    @Test
    public void registrationWithoutCredentialsTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel("", "");

        Allure.step("Отправить регистрацию без username и password");
        RegistrationValidationErrorResponseModel response = api.users.registerWithoutCredentials(registrationData);

        String expectedUsernameError = EMPTY_ERROR;
        String actualUsernameError = response.username().get(0);
        String expectedPasswordError = EMPTY_ERROR;
        String actualPasswordError = response.password().get(0);
        Allure.step("Проверить ошибки обоих обязательных полей");
        assertThat(response.username()).isNotNull().isNotEmpty();
        assertThat(actualUsernameError).isEqualTo(expectedUsernameError);
        assertThat(response.password()).isNotNull().isNotEmpty();
        assertThat(actualPasswordError).isEqualTo(expectedPasswordError);
    }

}
