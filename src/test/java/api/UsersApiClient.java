package api;

import io.qameta.allure.Step;
import models.registration.ExistingUserResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationValidationErrorResponseModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.users.UpdateUserPatchBodyModel;
import models.users.UpdateUserPutBodyModel;
import models.users.UpdateUserResponseModel;
import models.users.UpdateUserValidationErrorResponseModel;

import static io.restassured.RestAssured.given;
import static specs.registration.RegistrationSpec.existingUserRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.invalidRegistrationResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.updateuser.UpdateUserSpec.invalidUpdateUserResponseSpec;
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

    @Step("Невалидная регистрация пользователя")
    public RegistrationValidationErrorResponseModel registerInvalid(RegistrationBodyModel body) {
        return given(registrationRequestSpec)
                .body(body)
                .when()
                .post("/users/register/")
                .then()
                .spec(invalidRegistrationResponseSpec)
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
                .spec(invalidUpdateUserResponseSpec)
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
                .spec(invalidUpdateUserResponseSpec)
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
