package tests;

public class TestConfig {

    private static final String DEFAULT_BASE_URL = "https://book-club.qa.guru";

    public static String baseUrl() {
        return property("baseUrl", DEFAULT_BASE_URL);
    }

    public static String browser() {
        return property("browser", "chrome");
    }

    public static String browserSize() {
        return property("browserSize", "1920x1080");
    }

    public static String browserVersion() {
        return property("browserVersion", "");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(property("headless", "false"));
    }

    public static String remoteUrl() {
        String selenoidUrl = property("urlSelenoid", "");
        if (selenoidUrl.isEmpty()) {
            return "";
        }

        String login = property("loginSelenoid", "");
        String password = property("passwordSelenoid", "");
        if (login.isEmpty() || password.isEmpty()) {
            return normalizeUrl(selenoidUrl);
        }

        return String.format(
                "%s://%s:%s@%s",
                protocol(selenoidUrl),
                login,
                password,
                withoutProtocol(selenoidUrl)
        );
    }

    private static String property(String name, String defaultValue) {
        String value = System.getProperty(name, "");
        return value.isBlank() ? defaultValue : value;
    }

    private static String normalizeUrl(String url) {
        return url.startsWith("http") ? url : "https://" + url;
    }

    private static String protocol(String url) {
        return url.startsWith("http://") ? "http" : "https";
    }

    private static String withoutProtocol(String url) {
        return url.replaceFirst("^https?://", "");
    }
}
