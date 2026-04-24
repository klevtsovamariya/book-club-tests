package api;

import io.qameta.allure.Step;
import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationValidationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.UpdateUserPatchBodyModel;
import models.update.UpdateUserPutBodyModel;
import models.update.UpdateUserResponseModel;
import models.update.UpdateUserValidationErrorResponseModel;

import static io.restassured.RestAssured.given;
import static specs.registration.RegistrationSpec.existingUserRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.wrongRegistrationWithoutCredentialsResponseSpec;
import static specs.registration.RegistrationSpec.wrongRegistrationWithoutLoginResponseSpec;
import static specs.registration.RegistrationSpec.wrongRegistrationWithoutPasswordResponseSpec;
import static specs.updateuser.UpdateUserSpec.invalidPatchUpdateUserResponseSpec;
import static specs.updateuser.UpdateUserSpec.invalidPutUpdateUserResponseSpec;
import static specs.updateuser.UpdateUserSpec.successfulUpdateUserResponseSpec;
import static specs.updateuser.UpdateUserSpec.unauthorizedUpdateUserResponseSpec;
import static specs.updateuser.UpdateUserSpec.updateUserRequestSpec;

public class UsersApiClient {
    private static final String UPDATE_USER_ENDPOINT = "/users/me/";

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

    @Step("Обновление профиля пользователя через PUT")
    public UpdateUserResponseModel updateUserPut(String accessToken, UpdateUserPutBodyModel body) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put(UPDATE_USER_ENDPOINT)
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract()
                .as(UpdateUserResponseModel.class);
    }

    @Step("Частичное обновление профиля пользователя через PATCH")
    public UpdateUserResponseModel updateUserPatch(String accessToken, UpdateUserPatchBodyModel body) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .patch(UPDATE_USER_ENDPOINT)
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract()
                .as(UpdateUserResponseModel.class);
    }

    @Step("Невалидное обновление профиля через PUT")
    public UpdateUserValidationErrorResponseModel updateUserPutInvalid(String accessToken, UpdateUserPutBodyModel body) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put(UPDATE_USER_ENDPOINT)
                .then()
                .spec(invalidPutUpdateUserResponseSpec)
                .extract()
                .as(UpdateUserValidationErrorResponseModel.class);
    }

    @Step("Невалидное обновление профиля через PATCH")
    public UpdateUserValidationErrorResponseModel updateUserPatchInvalid(String accessToken, UpdateUserPatchBodyModel body) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .patch(UPDATE_USER_ENDPOINT)
                .then()
                .spec(invalidPatchUpdateUserResponseSpec)
                .extract()
                .as(UpdateUserValidationErrorResponseModel.class);
    }

    @Step("Обновление профиля без авторизации")
    public UpdateUserValidationErrorResponseModel updateUserPatchUnauthorized(UpdateUserPatchBodyModel body) {
        return given(updateUserRequestSpec)
                .body(body)
                .when()
                .patch(UPDATE_USER_ENDPOINT)
                .then()
                .spec(unauthorizedUpdateUserResponseSpec)
                .extract()
                .as(UpdateUserValidationErrorResponseModel.class);
    }
}
