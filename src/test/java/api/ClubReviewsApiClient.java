package api;

import io.qameta.allure.Step;
import models.ErrorResponseModel;
import models.club.ClubReviewBodyModel;
import models.club.ClubReviewListResponseModel;
import models.club.ClubReviewModel;
import models.club.ClubReviewPatchBodyModel;
import models.club.ClubReviewValidationErrorResponseModel;

import static io.restassured.RestAssured.given;
import static specs.club.ClubSpec.clubRequestSpec;
import static specs.club.ClubSpec.forbiddenClubResponseSpec;
import static specs.club.ClubSpec.invalidClubReviewResponseSpec;
import static specs.club.ClubSpec.successfulClubReviewResponseSpec;
import static specs.club.ClubSpec.successfulClubReviewListResponseSpec;
import static specs.club.ClubSpec.successfulCreateClubReviewResponseSpec;
import static specs.club.ClubSpec.successfulDeleteClubResponseSpec;
import static specs.club.ClubSpec.unauthorizedClubResponseSpec;

public class ClubReviewsApiClient {
    private static final String REVIEWS_ENDPOINT = "/clubs/reviews/";

    @Step("Получение списка отзывов клуба")
    public ClubReviewListResponseModel getClubReviews(Integer clubId) {
        return given(clubRequestSpec)
                .queryParam("club", clubId)
                .when()
                .get(REVIEWS_ENDPOINT)
                .then()
                .spec(successfulClubReviewListResponseSpec)
                .extract()
                .as(ClubReviewListResponseModel.class);
    }

    @Step("Создание отзыва клуба")
    public ClubReviewModel createClubReview(String accessToken, ClubReviewBodyModel body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post(REVIEWS_ENDPOINT)
                .then()
                .spec(successfulCreateClubReviewResponseSpec)
                .extract()
                .as(ClubReviewModel.class);
    }

    @Step("Создание отзыва клуба без авторизации")
    public ErrorResponseModel createClubReviewUnauthorized(ClubReviewBodyModel body) {
        return given(clubRequestSpec)
                .body(body)
                .when()
                .post(REVIEWS_ENDPOINT)
                .then()
                .spec(unauthorizedClubResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("Создание невалидного отзыва клуба")
    public ClubReviewValidationErrorResponseModel createInvalidClubReview(String accessToken, Object body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post(REVIEWS_ENDPOINT)
                .then()
                .spec(invalidClubReviewResponseSpec)
                .extract()
                .as(ClubReviewValidationErrorResponseModel.class);
    }

    @Step("Получение отзыва клуба по id")
    public ClubReviewModel getClubReviewById(Integer reviewId) {
        return given(clubRequestSpec)
                .when()
                .get(REVIEWS_ENDPOINT + reviewId + "/")
                .then()
                .spec(successfulClubReviewResponseSpec)
                .extract()
                .as(ClubReviewModel.class);
    }

    @Step("Полное обновление отзыва клуба через PUT")
    public ClubReviewModel updateClubReviewPut(String accessToken, Integer reviewId, ClubReviewBodyModel body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put(REVIEWS_ENDPOINT + reviewId + "/")
                .then()
                .spec(successfulClubReviewResponseSpec)
                .extract()
                .as(ClubReviewModel.class);
    }

    @Step("Частичное обновление отзыва клуба через PATCH")
    public ClubReviewModel updateClubReviewPatch(String accessToken, Integer reviewId, ClubReviewPatchBodyModel body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .patch(REVIEWS_ENDPOINT + reviewId + "/")
                .then()
                .spec(successfulClubReviewResponseSpec)
                .extract()
                .as(ClubReviewModel.class);
    }

    @Step("Обновление отзыва клуба без авторизации")
    public ErrorResponseModel updateClubReviewPutUnauthorized(Integer reviewId, ClubReviewBodyModel body) {
        return given(clubRequestSpec)
                .body(body)
                .when()
                .put(REVIEWS_ENDPOINT + reviewId + "/")
                .then()
                .spec(unauthorizedClubResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("Обновление чужого отзыва клуба")
    public ErrorResponseModel updateClubReviewPutForbidden(String accessToken, Integer reviewId, ClubReviewBodyModel body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put(REVIEWS_ENDPOINT + reviewId + "/")
                .then()
                .spec(forbiddenClubResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("Удаление отзыва клуба")
    public void deleteClubReview(String accessToken, Integer reviewId) {
        given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(REVIEWS_ENDPOINT + reviewId + "/")
                .then()
                .spec(successfulDeleteClubResponseSpec);
    }

    @Step("Удаление отзыва клуба без авторизации")
    public ErrorResponseModel deleteClubReviewUnauthorized(Integer reviewId) {
        return given(clubRequestSpec)
                .when()
                .delete(REVIEWS_ENDPOINT + reviewId + "/")
                .then()
                .spec(unauthorizedClubResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("Удаление чужого отзыва клуба")
    public ErrorResponseModel deleteClubReviewForbidden(String accessToken, Integer reviewId) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(REVIEWS_ENDPOINT + reviewId + "/")
                .then()
                .spec(forbiddenClubResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }
}
