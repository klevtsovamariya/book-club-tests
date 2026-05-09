package api;

import io.qameta.allure.Step;
import models.update.UpdateUserPatchBodyModel;
import models.update.UpdateUserPutBodyModel;
import models.update.UpdateUserResponseModel;
import models.update.UpdateUserValidationErrorResponseModel;

import static io.restassured.RestAssured.given;
import static specs.updateuser.UpdateUserSpec.invalidPatchUpdateUserResponseSpec;
import static specs.updateuser.UpdateUserSpec.invalidPutUpdateUserResponseSpec;
import static specs.updateuser.UpdateUserSpec.successfulUpdateUserResponseSpec;
import static specs.updateuser.UpdateUserSpec.unauthorizedUpdateUserResponseSpec;
import static specs.updateuser.UpdateUserSpec.updateUserRequestSpec;

public class UsersApiClient {
    private static final String USERS_ME_ENDPOINT = "/users/me/";

    @Step("Обновление профиля пользователя через PUT")
    public UpdateUserResponseModel updateUserPut(String accessToken, UpdateUserPutBodyModel body) {
        return given(updateUserRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put(USERS_ME_ENDPOINT)
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
                .patch(USERS_ME_ENDPOINT)
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
                .put(USERS_ME_ENDPOINT)
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
                .patch(USERS_ME_ENDPOINT)
                .then()
                .spec(invalidPatchUpdateUserResponseSpec)
                .extract()
                .as(UpdateUserValidationErrorResponseModel.class);
    }

    @Step("PATCH обновление профиля без авторизации")
    public UpdateUserValidationErrorResponseModel updateUserPatchUnauthorized(UpdateUserPatchBodyModel body) {
        return given(updateUserRequestSpec)
                .body(body)
                .when()
                .patch(USERS_ME_ENDPOINT)
                .then()
                .spec(unauthorizedUpdateUserResponseSpec)
                .extract()
                .as(UpdateUserValidationErrorResponseModel.class);
    }
}
