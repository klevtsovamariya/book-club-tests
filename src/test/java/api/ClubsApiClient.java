package api;

import io.qameta.allure.Step;
import models.ErrorResponseModel;
import models.club.ClubBodyModel;
import models.club.ClubListResponseModel;
import models.club.ClubPatchBodyModel;
import models.club.ClubResponseModel;
import net.datafaker.Faker;

import static io.restassured.RestAssured.given;
import static specs.club.ClubSpec.clubRequestSpec;
import static specs.club.ClubSpec.forbiddenClubResponseSpec;
import static specs.club.ClubSpec.notFoundClubResponseSpec;
import static specs.club.ClubSpec.successfulClubListResponseSpec;
import static specs.club.ClubSpec.successfulClubMembershipResponseSpec;
import static specs.club.ClubSpec.successfulClubResponseSpec;
import static specs.club.ClubSpec.successfulCreateClubResponseSpec;
import static specs.club.ClubSpec.successfulDeleteClubResponseSpec;
import static specs.club.ClubSpec.unauthorizedClubResponseSpec;

public class ClubsApiClient {
    private static final String CLUBS_ENDPOINT = "/clubs/";

    @Step("Создание клуба")
    public ClubResponseModel createClub(String accessToken, ClubBodyModel body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post(CLUBS_ENDPOINT)
                .then()
                .spec(successfulCreateClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);
    }

    @Step("[API] Создание случайного клуба POST /clubs/")
    public ClubResponseModel createRandomClub(String accessToken) {
        Faker faker = new Faker();
        ClubBodyModel body = new ClubBodyModel(
                "API Club " + faker.book().title(),
                faker.book().author(),
                faker.number().numberBetween(1950, 2027),
                faker.lorem().sentence(),
                "https://t.me/qa_guru_" + faker.internet().uuid().replace("-", "")
        );

        return createClub(accessToken, body);
    }

    @Step("Создание клуба без авторизации")
    public ErrorResponseModel createClubUnauthorized(ClubBodyModel body) {
        return given(clubRequestSpec)
                .body(body)
                .when()
                .post(CLUBS_ENDPOINT)
                .then()
                .spec(unauthorizedClubResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("Получение клуба по id")
    public ClubResponseModel getClubById(Integer clubId) {
        return given(clubRequestSpec)
                .when()
                .get(CLUBS_ENDPOINT + clubId + "/")
                .then()
                .spec(successfulClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);
    }

    @Step("Получение списка клубов")
    public ClubListResponseModel getClubs() {
        return given(clubRequestSpec)
                .when()
                .get(CLUBS_ENDPOINT)
                .then()
                .spec(successfulClubListResponseSpec)
                .extract()
                .as(ClubListResponseModel.class);
    }

    @Step("Получение списка клубов с параметрами")
    public ClubListResponseModel getClubs(String membership, Integer page, Integer pageSize, String search) {
        return given(clubRequestSpec)
                .queryParam("membership", membership)
                .queryParam("page", page)
                .queryParam("page_size", pageSize)
                .queryParam("search", search)
                .when()
                .get(CLUBS_ENDPOINT)
                .then()
                .spec(successfulClubListResponseSpec)
                .extract()
                .as(ClubListResponseModel.class);
    }

    @Step("Полное обновление клуба через PUT")
    public ClubResponseModel updateClubPut(String accessToken, Integer clubId, ClubBodyModel body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put(CLUBS_ENDPOINT + clubId + "/")
                .then()
                .spec(successfulClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);
    }

    @Step("Полное обновление клуба без авторизации")
    public ErrorResponseModel updateClubPutUnauthorized(Integer clubId, ClubBodyModel body) {
        return given(clubRequestSpec)
                .body(body)
                .when()
                .put(CLUBS_ENDPOINT + clubId + "/")
                .then()
                .spec(unauthorizedClubResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("Полное обновление клуба чужим пользователем")
    public ErrorResponseModel updateClubPutForbidden(String accessToken, Integer clubId, ClubBodyModel body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .put(CLUBS_ENDPOINT + clubId + "/")
                .then()
                .spec(forbiddenClubResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("Частичное обновление клуба через PATCH")
    public ClubResponseModel updateClubPatch(String accessToken, Integer clubId, ClubPatchBodyModel body) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .patch(CLUBS_ENDPOINT + clubId + "/")
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
                .delete(CLUBS_ENDPOINT + clubId + "/")
                .then()
                .spec(successfulDeleteClubResponseSpec);
    }

    @Step("Удаление клуба без авторизации")
    public ErrorResponseModel deleteClubUnauthorized(Integer clubId) {
        return given(clubRequestSpec)
                .when()
                .delete(CLUBS_ENDPOINT + clubId + "/")
                .then()
                .spec(unauthorizedClubResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("Удаление клуба чужим пользователем")
    public ErrorResponseModel deleteClubForbidden(String accessToken, Integer clubId) {
        return given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(CLUBS_ENDPOINT + clubId + "/")
                .then()
                .spec(forbiddenClubResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }

    @Step("Вступление текущего пользователя в клуб")
    public void joinClub(String accessToken, Integer clubId) {
        given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .post(CLUBS_ENDPOINT + clubId + "/members/me/")
                .then()
                .spec(successfulClubMembershipResponseSpec);
    }

    @Step("Выход текущего пользователя из клуба")
    public void leaveClub(String accessToken, Integer clubId) {
        given(clubRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(CLUBS_ENDPOINT + clubId + "/members/me/")
                .then()
                .spec(successfulClubMembershipResponseSpec);
    }

    @Step("Получение удаленного клуба по id")
    public ErrorResponseModel getDeletedClubById(Integer clubId) {
        return given(clubRequestSpec)
                .when()
                .get(CLUBS_ENDPOINT + clubId + "/")
                .then()
                .spec(notFoundClubResponseSpec)
                .extract()
                .as(ErrorResponseModel.class);
    }
}
