package api;

import io.qameta.allure.Step;
import models.login.LoginBodyModel;
import models.login.LoginValidationErrorResponseModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.LogoutValidationErrorResponseModel;

import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.invalidLoginResponseSpec;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.login.LoginSpec.wrongLoginNullPasswordResponseSpec;
import static specs.login.LoginSpec.wrongCredentialsLoginResponseSpec;
import static specs.logout.LogoutSpec.invalidLogoutResponseSpec;
import static specs.logout.LogoutSpec.logoutRequestSpec;
import static specs.logout.LogoutSpec.successfulLogoutResponseSpec;
import static specs.logout.LogoutSpec.unauthorizedLogoutResponseSpec;

public class AuthApiClient {

    @Step("Успешная авторизация пользователя")
    public SuccessfulLoginResponseModel login(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);
    }

    @Step("Авторизация и получение refresh токена")
    public String loginAndGetRefreshToken(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .path("refresh");
    }

    @Step("Авторизация и получение access токена")
    public String loginAndGetAccessToken(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .path("access");
    }

    @Step("Авторизация с неверными учетными данными")
    public WrongCredentialsLoginResponseModel loginWrongCredentials(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract()
                .as(WrongCredentialsLoginResponseModel.class);
    }

    @Step("Авторизация без username")
    public LoginValidationErrorResponseModel loginWithoutUsername(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(invalidLoginResponseSpec)
                .extract()
                .as(LoginValidationErrorResponseModel.class);
    }

    @Step("Авторизация без password")
    public LoginValidationErrorResponseModel loginWithoutPassword(LoginBodyModel loginBody) {
        return given(loginRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongLoginNullPasswordResponseSpec)
                .extract()
                .as(LoginValidationErrorResponseModel.class);
    }

    @Step("Отправка запроса logout")
    public void logout(LogoutBodyModel logoutBody) {
        given(logoutRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);
    }

    @Step("Негативный logout запрос")
    public LogoutValidationErrorResponseModel logoutInvalid(LogoutBodyModel logoutBody) {
        return given(logoutRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidLogoutResponseSpec)
                .extract()
                .as(LogoutValidationErrorResponseModel.class);
    }

    @Step("Logout с невалидным refresh токеном")
    public LogoutValidationErrorResponseModel logoutUnauthorized(LogoutBodyModel logoutBody) {
        return given(logoutRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(unauthorizedLogoutResponseSpec)
                .extract()
                .as(LogoutValidationErrorResponseModel.class);
    }
}
