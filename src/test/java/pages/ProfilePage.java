package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class ProfilePage extends BasePage {
    private final SelenideElement logoutButton = $("[data-testid=logout-button], .logout-btn");
    private final String pagePath = "/profile";

    @Step("[UI] Открыть страницу профиля")
    public ProfilePage openPage() {
        open(pagePath);

        return this;
    }

    @Step("[UI] Проверить, что профиль открыт")
    public ProfilePage profileShouldBeOpened() {
        logoutButton.shouldBe(visible);

        return this;
    }

    @Step("[UI] Нажатие кнопки выхода из аккаунта")
    public LoginPage pressLogoutButton() {
        logoutButton.click();

        return new LoginPage();
    }
}
