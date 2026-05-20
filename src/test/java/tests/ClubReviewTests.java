package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import models.ErrorResponseModel;
import models.club.ClubBodyModel;
import models.club.ClubResponseModel;
import models.club.ClubReviewBodyModel;
import models.club.ClubReviewListResponseModel;
import models.club.ClubReviewModel;
import models.club.ClubReviewPatchBodyModel;
import models.club.ClubReviewValidationErrorResponseModel;
import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

@Feature("Отзывы клубов")
@Tag("api")
@DisplayName("Отзывы клубов")
public class ClubReviewTests extends TestBase {
    private String ownerUsername;
    private String ownerPassword;
    private String ownerAccessToken;
    private Integer clubId;
    private Integer reviewId;

    @BeforeEach
    public void prepareClub() {
        ownerUsername = "review_owner_" + faker.internet().uuid().replace("-", "");
        ownerPassword = "pass_" + faker.number().digits(8);

        RegistrationBodyModel owner = new RegistrationBodyModel(ownerUsername, ownerPassword);
        api.users.register(owner);
        ownerAccessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(ownerUsername, ownerPassword));

        ClubResponseModel club = api.clubs.createClub(ownerAccessToken, createClubData("review"));
        clubId = club.id();
    }

    @AfterEach
    public void cleanUp() {
        if (reviewId != null) {
            api.reviews.deleteClubReview(ownerAccessToken, reviewId);
        }

        if (clubId != null) {
            api.clubs.deleteClub(ownerAccessToken, clubId);
        }
    }

    @DisplayName("Создание отзыва")
    @Test
    @Description("POST /clubs/reviews/ создаёт отзыв и возвращает его с id и данными пользователя.")
    @Severity(SeverityLevel.CRITICAL)
    public void createClubReviewTest() {
        ClubReviewBodyModel requestBody = createClubReviewData(clubId, "create");

        ClubReviewModel response = api.reviews.createClubReview(ownerAccessToken, requestBody);
        reviewId = response.id();

        step("Проверить поля созданного отзыва", () -> {
            assertThat(response.id()).isGreaterThan(0);
            assertThat(response.club()).isEqualTo(clubId);
            assertThat(response.review()).isEqualTo(requestBody.review());
            assertThat(response.assessment()).isEqualTo(requestBody.assessment());
            assertThat(response.readPages()).isEqualTo(requestBody.readPages());
            assertThat(response.user()).isNotNull();
            assertThat(response.user().username()).isEqualTo(ownerUsername);
            assertThat(response.created()).isNotBlank();
        });
    }

    @DisplayName("Получение списка отзывов по club")
    @Test
    public void readClubReviewsListTest() {
        ClubReviewBodyModel requestBody = createClubReviewData(clubId, "list");
        reviewId = api.reviews.createClubReview(ownerAccessToken, requestBody).id();

        ClubReviewListResponseModel response = api.reviews.getClubReviews(clubId);

        step("Проверить, что созданный отзыв есть в списке отзывов клуба", () -> {
            assertThat(response.count()).isNotNull().isGreaterThanOrEqualTo(1);
            assertThat(response.results()).extracting(ClubReviewModel::id).contains(reviewId);
        });
    }

    @DisplayName("Получение отзыва по id")
    @Test
    public void readClubReviewByIdTest() {
        ClubReviewBodyModel requestBody = createClubReviewData(clubId, "read");
        reviewId = api.reviews.createClubReview(ownerAccessToken, requestBody).id();

        ClubReviewModel response = api.reviews.getClubReviewById(reviewId);

        step("Проверить, что отзыв получен по id", () -> {
            assertThat(response.id()).isEqualTo(reviewId);
            assertThat(response.club()).isEqualTo(clubId);
            assertThat(response.review()).isEqualTo(requestBody.review());
            assertThat(response.assessment()).isEqualTo(requestBody.assessment());
            assertThat(response.readPages()).isEqualTo(requestBody.readPages());
        });
    }

    @DisplayName("PUT обновление отзыва")
    @Test
    @Description("PUT /clubs/reviews/{id}/ обновляет поля отзыва его владельцем.")
    @Severity(SeverityLevel.CRITICAL)
    public void updateClubReviewPutTest() {
        reviewId = api.reviews.createClubReview(ownerAccessToken, createClubReviewData(clubId, "base")).id();
        ClubReviewBodyModel requestBody = new ClubReviewBodyModel(clubId, "Updated review", 4, 200);

        ClubReviewModel response = api.reviews.updateClubReviewPut(ownerAccessToken, reviewId, requestBody);

        step("Проверить, что отзыв обновлен через PUT", () -> {
            assertThat(response.id()).isEqualTo(reviewId);
            assertThat(response.review()).isEqualTo(requestBody.review());
            assertThat(response.assessment()).isEqualTo(requestBody.assessment());
            assertThat(response.readPages()).isEqualTo(requestBody.readPages());
        });
    }

    @DisplayName("PATCH обновление отзыва")
    @Test
    public void updateClubReviewPatchTest() {
        reviewId = api.reviews.createClubReview(ownerAccessToken, createClubReviewData(clubId, "base")).id();
        ClubReviewPatchBodyModel requestBody = new ClubReviewPatchBodyModel("Patched review");

        ClubReviewModel response = api.reviews.updateClubReviewPatch(ownerAccessToken, reviewId, requestBody);

        step("Проверить, что отзыв частично обновлен через PATCH", () -> {
            assertThat(response.id()).isEqualTo(reviewId);
            assertThat(response.club()).isEqualTo(clubId);
            assertThat(response.review()).isEqualTo(requestBody.review());
        });
    }

    @DisplayName("Удаление отзыва")
    @Test
    public void deleteClubReviewTest() {
        reviewId = api.reviews.createClubReview(ownerAccessToken, createClubReviewData(clubId, "delete")).id();

        api.reviews.deleteClubReview(ownerAccessToken, reviewId);

        reviewId = null;
    }

    @DisplayName("Создание отзыва без авторизации")
    @Test
    @Description("POST /clubs/reviews/ без токена возвращает 401.")
    @Severity(SeverityLevel.CRITICAL)
    public void createClubReviewWithoutAuthTest() {
        ClubReviewBodyModel requestBody = createClubReviewData(clubId, "unauthorized");

        ErrorResponseModel response = api.reviews.createClubReviewUnauthorized(requestBody);

        step("Проверить ошибку отсутствия авторизации", () ->
                assertThat(response.detail()).isEqualTo(UNAUTHORIZED_ERROR));
    }

    @DisplayName("Обновление отзыва без авторизации")
    @Test
    public void updateClubReviewWithoutAuthTest() {
        reviewId = api.reviews.createClubReview(ownerAccessToken, createClubReviewData(clubId, "base")).id();
        ClubReviewBodyModel requestBody = new ClubReviewBodyModel(clubId, "Unauthorized update", 4, 10);

        ErrorResponseModel response = api.reviews.updateClubReviewPutUnauthorized(reviewId, requestBody);

        step("Проверить ошибку отсутствия авторизации", () ->
                assertThat(response.detail()).isEqualTo(UNAUTHORIZED_ERROR));
    }

    @DisplayName("Удаление отзыва без авторизации")
    @Test
    public void deleteClubReviewWithoutAuthTest() {
        reviewId = api.reviews.createClubReview(ownerAccessToken, createClubReviewData(clubId, "base")).id();

        ErrorResponseModel response = api.reviews.deleteClubReviewUnauthorized(reviewId);

        step("Проверить ошибку отсутствия авторизации", () ->
                assertThat(response.detail()).isEqualTo(UNAUTHORIZED_ERROR));
    }

    @DisplayName("Обновление чужого отзыва запрещено")
    @Test
    @Description("Пользователь не может изменить отзыв, созданный другим пользователем.")
    @Severity(SeverityLevel.CRITICAL)
    public void updateOtherUserClubReviewForbiddenTest() {
        reviewId = api.reviews.createClubReview(ownerAccessToken, createClubReviewData(clubId, "base")).id();
        String otherAccessToken = createUserAndGetAccessToken("review_other");
        ClubReviewBodyModel requestBody = new ClubReviewBodyModel(clubId, "Forbidden update", 3, 50);

        ErrorResponseModel response = api.reviews.updateClubReviewPutForbidden(otherAccessToken, reviewId, requestBody);

        step("Проверить ошибку прав доступа", () ->
                assertThat(response.detail()).isEqualTo(FORBIDDEN_ERROR));
    }

    @DisplayName("Удаление чужого отзыва запрещено")
    @Test
    public void deleteOtherUserClubReviewForbiddenTest() {
        reviewId = api.reviews.createClubReview(ownerAccessToken, createClubReviewData(clubId, "base")).id();
        String otherAccessToken = createUserAndGetAccessToken("review_other");

        ErrorResponseModel response = api.reviews.deleteClubReviewForbidden(otherAccessToken, reviewId);

        step("Проверить ошибку прав доступа", () ->
                assertThat(response.detail()).isEqualTo(FORBIDDEN_ERROR));
    }

    @DisplayName("Создание отзыва без обязательных полей")
    @Test
    public void createClubReviewWithoutRequiredFieldsTest() {
        Map<String, Object> requestBody = Map.of();

        ClubReviewValidationErrorResponseModel response = api.reviews.createInvalidClubReview(ownerAccessToken, requestBody);

        step("Проверить ошибки обязательных полей", () -> {
            assertThat(response.club()).isNotNull().isNotEmpty();
            assertThat(response.club().get(0)).isEqualTo(REQUIRED_ERROR);
            assertThat(response.review()).isNotNull().isNotEmpty();
            assertThat(response.review().get(0)).isEqualTo(REQUIRED_ERROR);
            assertThat(response.assessment()).isNotNull().isNotEmpty();
            assertThat(response.assessment().get(0)).isEqualTo(REQUIRED_ERROR);
            assertThat(response.readPages()).isNotNull().isNotEmpty();
            assertThat(response.readPages().get(0)).isEqualTo(REQUIRED_ERROR);
        });
    }

    private String createUserAndGetAccessToken(String marker) {
        String username = marker + "_" + faker.internet().uuid().replace("-", "");
        String password = "pass_" + faker.number().digits(8);

        RegistrationBodyModel user = new RegistrationBodyModel(username, password);
        api.users.register(user);

        return api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));
    }
}
