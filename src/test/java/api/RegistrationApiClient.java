package api;

import io.qameta.allure.Step;
import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationValidationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseModel;

import static io.restassured.RestAssured.given;
import static specs.registration.RegistrationSpec.existingUserRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.wrongRegistrationWithoutCredentialsResponseSpec;
import static specs.registration.RegistrationSpec.wrongRegistrationWithoutLoginResponseSpec;
import static specs.registration.RegistrationSpec.wrongRegistrationWithoutPasswordResponseSpec;

public class RegistrationApiClient {

    @Step("Регистрация нового пользователя")
    public SuccessfulRegistrationResponseModel register(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(SuccessfulRegistrationResponseModel.class);
    }

    @Step("Регистрация уже существующего пользователя")
    public ExistingUserResponseModel registerExistingUser(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingUserRegistrationResponseSpec)
                .extract()
                .as(ExistingUserResponseModel.class);
    }

    @Step("Регистрация без username")
    public RegistrationValidationErrorResponseModel registerWithoutUsername(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongRegistrationWithoutLoginResponseSpec)
                .extract()
                .as(RegistrationValidationErrorResponseModel.class);
    }

    @Step("Регистрация без password")
    public RegistrationValidationErrorResponseModel registerWithoutPassword(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongRegistrationWithoutPasswordResponseSpec)
                .extract()
                .as(RegistrationValidationErrorResponseModel.class);
    }

    @Step("Регистрация без username и password")
    public RegistrationValidationErrorResponseModel registerWithoutCredentials(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongRegistrationWithoutCredentialsResponseSpec)
                .extract()
                .as(RegistrationValidationErrorResponseModel.class);
    }
}
