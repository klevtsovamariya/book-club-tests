package tests;

import models.club.ClubBodyModel;
import models.club.ClubListResponseModel;
import models.club.ClubPatchBodyModel;
import models.club.ClubResponseModel;
import models.club.ClubReviewListResponseModel;
import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.createClubData;
import static tests.TestData.LOGIN_PASSWORD;
import static tests.TestData.LOGIN_USERNAME;

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

    @DisplayName("Частичное обновление клуба")
    @Test
    public void patchClubTest() {
        clubId = api.clubs.createClub(accessToken, createClubData("base")).id();
        ClubBodyModel requestBody = createClubData("patched");
        ClubPatchBodyModel patchBody = new ClubPatchBodyModel(
                requestBody.bookTitle(),
                requestBody.bookAuthors(),
                requestBody.publicationYear(),
                requestBody.description(),
                requestBody.telegramChatLink()
        );

        ClubResponseModel response = api.clubs.updateClubPatch(accessToken, clubId, patchBody);

        step("Проверить, что клуб обновился через PATCH", () -> {
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

    @DisplayName("Удаление клуба")
    @Test
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
        String memberPassword = "pass_" + System.currentTimeMillis();
        RegistrationBodyModel memberData = new RegistrationBodyModel(
                "club_member_" + System.currentTimeMillis(),
                memberPassword
        );
        SuccessfulRegistrationResponseModel member = api.registration.register(memberData);
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
        String memberPassword = "pass_" + System.currentTimeMillis();
        RegistrationBodyModel memberData = new RegistrationBodyModel(
                "club_member_" + System.currentTimeMillis(),
                memberPassword
        );
        SuccessfulRegistrationResponseModel member = api.registration.register(memberData);
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

        ClubReviewListResponseModel response = api.clubs.getClubReviews(clubId);

        step("Проверить, что список отзывов клуба получен", () -> {
            assertThat(response.count()).isNotNull().isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.count()).isGreaterThanOrEqualTo(response.results().size());
        });
    }
}
