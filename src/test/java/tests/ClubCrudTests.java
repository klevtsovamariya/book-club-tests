package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import models.ErrorResponseModel;
import models.club.ClubBodyModel;
import models.club.ClubListResponseModel;
import models.club.ClubResponseModel;
import models.club.ClubReviewListResponseModel;
import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.CLUB_NOT_FOUND_ERROR;
import static tests.TestData.FORBIDDEN_ERROR;
import static tests.TestData.createClubData;
import static tests.TestData.LOGIN_PASSWORD;
import static tests.TestData.LOGIN_USERNAME;
import static tests.TestData.UNAUTHORIZED_ERROR;

@Feature("Клубы")
@Tag("api")
@DisplayName("Операции с клубами")
public class ClubCrudTests extends TestBase {
    private String accessToken;
    private Integer clubId;

    @BeforeEach
    public void prepareTestData() {
        accessToken = api.auth.loginAndGetAccessToken(new LoginBodyModel(LOGIN_USERNAME, LOGIN_PASSWORD));
    }

    @AfterEach
    public void deleteCreatedClub() {
        if (clubId != null) {
            api.clubs.deleteClub(accessToken, clubId);
        }
    }

    @DisplayName("Создание клуба")
    @Test
    @Description("POST /clubs/ с валидным bearer-токеном создаёт клуб и возвращает заполненные поля сущности.")
    @Severity(SeverityLevel.CRITICAL)
    public void createClubTest() {
        ClubBodyModel requestBody = createClubData("create");

        ClubResponseModel response = api.clubs.createClub(accessToken, requestBody);
        clubId = response.id();

        step("Проверить поля созданного клуба", () -> {
            assertThat(response.id()).isGreaterThan(0);
            assertThat(response.bookTitle()).isEqualTo(requestBody.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(requestBody.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(requestBody.publicationYear());
            assertThat(response.description()).isEqualTo(requestBody.description());
            assertThat(response.telegramChatLink()).isEqualTo(requestBody.telegramChatLink());
            assertThat(response.owner()).isNotNull();
            assertThat(response.members()).isNotNull();
            assertThat(response.reviews()).isNotNull();
            assertThat(response.created()).isNotBlank();
        });
    }

    @DisplayName("Получение списка клубов")
    @Test
    @Description("GET /clubs/ возвращает пагинированный список клубов и согласованные значения count/results.")
    @Severity(SeverityLevel.NORMAL)
    public void readClubsListTest() {
        ClubListResponseModel response = api.clubs.getClubs();

        step("Проверить, что список клубов получен", () -> {
            assertThat(response.count()).isNotNull().isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(response.results().size());
        });
    }

    @DisplayName("Чтение клуба")
    @Test
    public void readClubTest() {
        ClubBodyModel requestBody = createClubData("base");
        clubId = api.clubs.createClub(accessToken, requestBody).id();

        ClubResponseModel response = api.clubs.getClubById(clubId);

        step("Проверить, что клуб получен по id", () -> {
            assertThat(response.id()).isEqualTo(clubId);
            assertThat(response.bookTitle()).isEqualTo(requestBody.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(requestBody.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(requestBody.publicationYear());
            assertThat(response.description()).isEqualTo(requestBody.description());
            assertThat(response.telegramChatLink()).isEqualTo(requestBody.telegramChatLink());
            assertThat(response.owner()).isNotNull().isGreaterThan(0);
            assertThat(response.members()).isNotNull();
            assertThat(response.reviews()).isNotNull();
            assertThat(response.created()).isNotBlank();
        });
    }

    @DisplayName("Обновление клуба")
    @Test
    @Description("PUT /clubs/{id}/ обновляет изменяемые поля клуба владельцем.")
    @Severity(SeverityLevel.CRITICAL)
    public void updateClubTest() {
        clubId = api.clubs.createClub(accessToken, createClubData("base")).id();
        ClubBodyModel requestBody = createClubData("updated");

        ClubResponseModel response = api.clubs.updateClubPut(accessToken, clubId, requestBody);

        step("Проверить, что клуб обновился", () -> {
            assertThat(response.id()).isEqualTo(clubId);
            assertThat(response.bookTitle()).isEqualTo(requestBody.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(requestBody.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(requestBody.publicationYear());
            assertThat(response.description()).isEqualTo(requestBody.description());
            assertThat(response.telegramChatLink()).isEqualTo(requestBody.telegramChatLink());
            assertThat(response.owner()).isNotNull().isGreaterThan(0);
            assertThat(response.members()).isNotNull();
            assertThat(response.reviews()).isNotNull();
            assertThat(response.created()).isNotBlank();
        });
    }

    @DisplayName("PUT обновление клуба сохраняется после повторного чтения")
    @Test
    public void putUpdatedClubShouldBeSavedTest() {
        clubId = api.clubs.createClub(accessToken, createClubData("base")).id();
        ClubBodyModel requestBody = createClubData("saved-put");

        api.clubs.updateClubPut(accessToken, clubId, requestBody);
        ClubResponseModel response = api.clubs.getClubById(clubId);

        step("Проверить, что изменения PUT сохранены", () -> {
            assertThat(response.id()).isEqualTo(clubId);
            assertThat(response.bookTitle()).isEqualTo(requestBody.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(requestBody.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(requestBody.publicationYear());
            assertThat(response.description()).isEqualTo(requestBody.description());
            assertThat(response.telegramChatLink()).isEqualTo(requestBody.telegramChatLink());
        });
    }

    @DisplayName("Удаление клуба")
    @Test
    @Description("DELETE /clubs/{id}/ удаляет клуб и последующий GET по id возвращает 404.")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteClubTest() {
        clubId = api.clubs.createClub(accessToken, createClubData("base")).id();

        api.clubs.deleteClub(accessToken, clubId);

        api.clubs.getDeletedClubById(clubId);
        clubId = null;
    }

    @DisplayName("Вступление пользователя в клуб")
    @Test
    public void joinClubTest() {
        clubId = api.clubs.createClub(accessToken, createClubData("base")).id();
        String memberPassword = "pass_" + faker.number().digits(8);
        RegistrationBodyModel memberData = new RegistrationBodyModel(
                "club_member_" + faker.internet().uuid().replace("-", ""),
                memberPassword
        );
        SuccessfulRegistrationResponseModel member = api.users.register(memberData);
        String memberAccessToken = api.auth.loginAndGetAccessToken(
                new LoginBodyModel(memberData.username(), memberPassword)
        );

        api.clubs.joinClub(memberAccessToken, clubId);

        ClubResponseModel club = api.clubs.getClubById(clubId);

        step("Проверить, что пользователь добавлен в участники клуба", () ->
                assertThat(club.members()).contains(member.id()));
    }

    @DisplayName("Выход пользователя из клуба")
    @Test
    public void leaveClubTest() {
        clubId = api.clubs.createClub(accessToken, createClubData("base")).id();
        String memberPassword = "pass_" + faker.number().digits(8);
        RegistrationBodyModel memberData = new RegistrationBodyModel(
                "club_member_" + faker.internet().uuid().replace("-", ""),
                memberPassword
        );
        SuccessfulRegistrationResponseModel member = api.users.register(memberData);
        String memberAccessToken = api.auth.loginAndGetAccessToken(
                new LoginBodyModel(memberData.username(), memberPassword)
        );

        api.clubs.joinClub(memberAccessToken, clubId);
        api.clubs.leaveClub(memberAccessToken, clubId);

        ClubResponseModel club = api.clubs.getClubById(clubId);

        step("Проверить, что пользователь удален из участников клуба", () ->
                assertThat(club.members()).doesNotContain(member.id()));
    }

    @DisplayName("Получение списка отзывов клуба")
    @Test
    public void readClubReviewsTest() {
        clubId = api.clubs.createClub(accessToken, createClubData("base")).id();

        ClubReviewListResponseModel response = api.reviews.getClubReviews(clubId);

        step("Проверить, что список отзывов клуба получен", () -> {
            assertThat(response.count()).isNotNull().isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(response.results().size());
        });
    }

    @DisplayName("Создание клуба без авторизации")
    @Test
    @Description("POST /clubs/ без токена возвращает 401 с сообщением об отсутствии credentials.")
    @Severity(SeverityLevel.CRITICAL)
    public void createClubWithoutAuthTest() {
        ClubBodyModel requestBody = createClubData("unauthorized");

        ErrorResponseModel response = api.clubs.createClubUnauthorized(requestBody);

        step("Проверить ошибку отсутствия авторизации", () ->
                assertThat(response.detail()).isEqualTo(UNAUTHORIZED_ERROR));
    }

    @DisplayName("PUT обновление клуба без авторизации")
    @Test
    public void updateClubWithoutAuthTest() {
        clubId = api.clubs.createClub(accessToken, createClubData("base")).id();
        ClubBodyModel requestBody = createClubData("unauthorized-update");

        ErrorResponseModel response = api.clubs.updateClubPutUnauthorized(clubId, requestBody);

        step("Проверить ошибку отсутствия авторизации", () ->
                assertThat(response.detail()).isEqualTo(UNAUTHORIZED_ERROR));
    }

    @DisplayName("Удаление клуба без авторизации")
    @Test
    public void deleteClubWithoutAuthTest() {
        clubId = api.clubs.createClub(accessToken, createClubData("base")).id();

        ErrorResponseModel response = api.clubs.deleteClubUnauthorized(clubId);

        step("Проверить ошибку отсутствия авторизации", () ->
                assertThat(response.detail()).isEqualTo(UNAUTHORIZED_ERROR));
    }

    @DisplayName("PUT обновление чужого клуба запрещено")
    @Test
    @Description("Пользователь, не являющийся владельцем клуба, не может выполнить PUT /clubs/{id}/.")
    @Severity(SeverityLevel.CRITICAL)
    public void updateOtherUserClubForbiddenTest() {
        clubId = api.clubs.createClub(accessToken, createClubData("base")).id();
        String otherAccessToken = registerUserAndGetAccessToken("club_other");
        ClubBodyModel requestBody = createClubData("forbidden-update");

        ErrorResponseModel response = api.clubs.updateClubPutForbidden(otherAccessToken, clubId, requestBody);

        step("Проверить ошибку прав доступа", () ->
                assertThat(response.detail()).isEqualTo(FORBIDDEN_ERROR));
    }

    @DisplayName("Удаление чужого клуба запрещено")
    @Test
    public void deleteOtherUserClubForbiddenTest() {
        clubId = api.clubs.createClub(accessToken, createClubData("base")).id();
        String otherAccessToken = registerUserAndGetAccessToken("club_other");

        ErrorResponseModel response = api.clubs.deleteClubForbidden(otherAccessToken, clubId);

        step("Проверить ошибку прав доступа", () ->
                assertThat(response.detail()).isEqualTo(FORBIDDEN_ERROR));
    }

    @DisplayName("Получение несуществующего клуба")
    @Test
    public void getMissingClubTest() {
        ErrorResponseModel response = api.clubs.getDeletedClubById(999999999);

        step("Проверить ошибку отсутствующего клуба", () ->
                assertThat(response.detail()).isEqualTo(CLUB_NOT_FOUND_ERROR));
    }

    private String registerUserAndGetAccessToken(String marker) {
        String username = marker + "_" + faker.internet().uuid().replace("-", "");
        String password = "pass_" + faker.number().digits(8);

        RegistrationBodyModel user = new RegistrationBodyModel(username, password);
        api.users.register(user);

        return api.auth.loginAndGetAccessToken(new LoginBodyModel(username, password));
    }
}
