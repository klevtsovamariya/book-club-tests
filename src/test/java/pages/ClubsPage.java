package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

public class ClubsPage extends BasePage {
    private final SelenideElement mainContent = $("main");
    private final SelenideElement searchInput = $("input[placeholder='Поиск книжных клубов...']");
    private final SelenideElement searchButton = $x("//button[normalize-space()='Найти']");

    @Step("[UI] Открыть главную страницу клубов")
    public ClubsPage openPage() {
        open("/");

        return this;
    }

    @Step("[UI] Найти клуб: {query}")
    public ClubsPage searchClub(String query) {
        searchInput.setValue(query);
        searchButton.click();

        return this;
    }

    @Step("[UI] Проверить, что клуб отображается в списке")
    public ClubsPage clubShouldBeVisible(String bookTitle, String bookAuthors, String description) {
        mainContent.shouldBe(visible);
        mainContent.shouldHave(text(bookTitle));
        mainContent.shouldHave(text(bookAuthors));
        mainContent.shouldHave(text(description));

        return this;
    }
}
