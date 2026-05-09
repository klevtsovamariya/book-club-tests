package api;

import io.qameta.allure.Step;
import models.club.ClubBodyModel;
import models.club.ClubListResponseModel;
import models.club.ClubPatchBodyModel;
import models.club.ClubResponseModel;
import models.club.ClubReviewListResponseModel;

import static io.restassured.RestAssured.given;
import static specs.club.ClubSpec.clubRequestSpec;
import static specs.club.ClubSpec.notFoundClubResponseSpec;
import static specs.club.ClubSpec.successfulClubListResponseSpec;
import static specs.club.ClubSpec.successfulClubMembershipResponseSpec;
import static specs.club.ClubSpec.successfulClubResponseSpec;
import static specs.club.ClubSpec.successfulClubReviewListResponseSpec;
import static specs.club.ClubSpec.successfulCreateClubResponseSpec;
import static specs.club.ClubSpec.successfulDeleteClubResponseSpec;

public class ClubsApiClient {

    @Step("Создание клуба")
    public ClubResponseModel createClub(String accessToken, ClubBodyModel body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(successfulCreateClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);
    }

    @Step("Получение клуба по id")
    public ClubResponseModel getClubById(Integer clubId) {
        return given(clubRequestSpec)
                .when()
                .get("/clubs/" + clubId + "/")
                .then()
                .spec(successfulClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);
    }

    @Step("Получение списка клубов")
    public ClubListResponseModel getClubs() {
        return given(clubRequestSpec)
                .when()
                .get("/clubs/")
                .then()
                .spec(successfulClubListResponseSpec)
                .extract()
                .as(ClubListResponseModel.class);
    }

    @Step("Получение списка отзывов клуба")
    public ClubReviewListResponseModel getClubReviews(Integer clubId) {
        return given(clubRequestSpec)
                .queryParam("club", clubId)
                .when()
                .get("/clubs/reviews/")
                .then()
                .spec(successfulClubReviewListResponseSpec)
                .extract()
                .as(ClubReviewListResponseModel.class);
    }

    @Step("Полное обновление клуба через PUT")
    public ClubResponseModel updateClubPut(String accessToken, Integer clubId, ClubBodyModel body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put("/clubs/" + clubId + "/")
                .then()
                .spec(successfulClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);
    }

    @Step("Частичное обновление клуба через PATCH")
    public ClubResponseModel updateClubPatch(String accessToken, Integer clubId, ClubPatchBodyModel body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .patch("/clubs/" + clubId + "/")
                .then()
                .spec(successfulClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);
    }

    @Step("Удаление клуба")
    public void deleteClub(String accessToken, Integer clubId) {
        given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/clubs/" + clubId + "/")
                .then()
                .spec(successfulDeleteClubResponseSpec);
    }

    @Step("Вступление текущего пользователя в клуб")
    public void joinClub(String accessToken, Integer clubId) {
        given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .post("/clubs/" + clubId + "/members/me/")
                .then()
                .spec(successfulClubMembershipResponseSpec);
    }

    @Step("Выход текущего пользователя из клуба")
    public void leaveClub(String accessToken, Integer clubId) {
        given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/clubs/" + clubId + "/members/me/")
                .then()
                .spec(successfulClubMembershipResponseSpec);
    }

    @Step("Получение удаленного клуба по id")
    public void getDeletedClubById(Integer clubId) {
        given(clubRequestSpec)
                .when()
                .get("/clubs/" + clubId + "/")
                .then()
                .spec(notFoundClubResponseSpec);
    }
}
