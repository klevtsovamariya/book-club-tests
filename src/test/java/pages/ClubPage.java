package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.confirm;
import static com.codeborne.selenide.Selenide.open;

public class ClubPage extends BasePage {
    private final SelenideElement leaveButton = $x("//button[normalize-space()='Покинуть клуб']");

    @Step("[UI] Открыть страницу клуба по id: {clubId}")
    public ClubPage openPageById(Integer clubId) {
        open("/clubs/" + clubId);

        return this;
    }

    @Step("[UI] Проверить данные клуба")
    public ClubPage clubShouldBeOpened(String bookTitle, String bookAuthors, String description) {
        $("body").shouldHave(text(bookTitle));
        $("body").shouldHave(text(bookAuthors));
        $("body").shouldHave(text(description));

        return this;
    }

    @Step("[UI] Нажать «Покинуть клуб» и подтвердить диалог")
    public ClubPage pressLeaveClubButton() {
        leaveButton.shouldBe(visible).click();
        confirm();

        return this;
    }

    @Step("[UI] Проверить, что владелец не может покинуть клуб")
    public ClubPage ownerCannotLeaveClubShouldBeShown() {
        $("body").shouldHave(text("Не удалось покинуть клуб"));

        return this;
    }
}
