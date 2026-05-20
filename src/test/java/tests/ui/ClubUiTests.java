package tests.ui;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import models.club.ClubBodyModel;
import models.club.ClubResponseModel;
import models.login.SuccessfulLoginResponseModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.TestBase;

import static tests.TestData.createClubData;

@Feature("Клубы")
@DisplayName("UI клубов")
public class ClubUiTests extends TestBase {
    private String accessToken;
    private Integer clubId;

    @AfterEach
    public void cleanUpClub() {
        if (clubId != null && accessToken != null) {
            api.clubs.deleteClub(accessToken, clubId);
        }
    }

    @DisplayName("Главная страница отображает созданный через API клуб")
    @Test
    @Tag("ui")
    @Description("Новый пользователь авторизуется через localStorage, клуб создается через API, " +
            "на главной странице проверяется отображение созданного клуба через поиск.")
    @Severity(SeverityLevel.CRITICAL)
    public void createdClubShouldBeVisibleOnMainPageTest() {
        SuccessfulLoginResponseModel login = clubsPage.openBlankPageWithNewUser();
        accessToken = login.access();

        ClubBodyModel clubData = createClubData("ui-main");
        ClubResponseModel club = api.clubs.createClub(accessToken, clubData);
        clubId = club.id();

        clubsPage.openPage()
                .searchClub(clubData.bookTitle())
                .clubShouldBeVisible(
                        clubData.bookTitle(),
                        clubData.bookAuthors(),
                        clubData.description()
                );
    }

    @DisplayName("Страница клуба отображает данные созданного через API клуба")
    @Test
    @Tag("ui")
    @Description("Новый пользователь авторизуется через localStorage, создает клуб через API " +
            "и открывает страницу клуба по id с проверкой отображаемых данных.")
    @Severity(SeverityLevel.CRITICAL)
    public void createdClubShouldBeOpenedByIdTest() {
        SuccessfulLoginResponseModel login = clubPage.openBlankPageWithNewUser();
        accessToken = login.access();

        ClubBodyModel clubData = createClubData("ui-detail");
        ClubResponseModel club = api.clubs.createClub(accessToken, clubData);
        clubId = club.id();

        clubPage.openPageById(clubId)
                .clubShouldBeOpened(
                        clubData.bookTitle(),
                        clubData.bookAuthors(),
                        clubData.description()
                );
    }

    @DisplayName("Владелец не может покинуть свой клуб через UI")
    @Test
    @Tag("ui")
    @Description("Новый пользователь создает клуб через API и в UI пытается покинуть собственный клуб; " +
            "должно отображаться сообщение об ошибке.")
    @Severity(SeverityLevel.CRITICAL)
    public void ownerCannotLeaveOwnClubTest() {
        SuccessfulLoginResponseModel login = clubPage.openBlankPageWithNewUser();
        accessToken = login.access();

        ClubResponseModel club = api.clubs.createRandomClub(accessToken);
        clubId = club.id();

        clubPage.openPageById(clubId)
                .pressLeaveClubButton()
                .ownerCannotLeaveClubShouldBeShown();
    }

    @DisplayName("Профиль открывается с сессией, подготовленной через API")
    @Test
    @Tag("ui")
    @Description("Для существующего пользователя устанавливается localStorage-сессия и проверяется, " +
            "что страница профиля успешно открывается.")
    @Severity(SeverityLevel.NORMAL)
    public void profileShouldBeOpenedWithApiPreparedSessionTest() {
        profilePage.openBlankPageWithExistingUser();

        profilePage.openPage()
                .profileShouldBeOpened();
    }
}
