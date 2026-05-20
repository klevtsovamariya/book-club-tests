package tests;

import models.club.ClubBodyModel;
import models.club.ClubReviewBodyModel;
import net.datafaker.Faker;

public class TestData {
    private static final Faker faker = new Faker();
    public static final String LOGIN_ID = "2";

    public static final String LOGIN_USERNAME = "user8";
    public static final String LOGIN_PASSWORD = "user8";
    public static final String LOGIN_WRONG_PASSWORD = "qaguru1234";

    public static final String LOGIN_TOKEN_PREFIX = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    public static final String LOGIN_WRONG_CREDENTIALS_ERROR = "Invalid username or password.";
    public static final String EMPTY_ERROR = "This field may not be blank.";
    public static final String LOGOUT_INVALID_TOKEN_ERROR = "Token is invalid";
    public static final String NULL_ERROR = "This field may not be null.";
    public static final String REQUIRED_ERROR = "This field is required.";
    public static final String INVALID_EMAIL_ERROR = "Enter a valid email address.";
    public static final String UNAUTHORIZED_ERROR = "Authentication credentials were not provided.";
    public static final String FORBIDDEN_ERROR = "You do not have permission to perform this action.";
    public static final String CLUB_NOT_FOUND_ERROR = "No Club matches the given query.";

    public static final String REGISTRATION_EXISTING_USER_ERROR =
            "A user with that username already exists.";

    public static final String REGISTRATION_IP_REGEXP =
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

    public static ClubBodyModel createClubData(String marker) {
        return new ClubBodyModel(
                "API Club " + marker + " " + faker.book().title(),
                faker.book().author(),
                faker.number().numberBetween(1950, 2027),
                faker.lorem().sentence(),
                "https://t.me/" + marker + "_" + faker.internet().uuid().replace("-", "")
        );
    }

    public static ClubReviewBodyModel createClubReviewData(Integer clubId, String marker) {
        return new ClubReviewBodyModel(
                clubId,
                "Review " + marker + " " + faker.lorem().sentence(),
                faker.number().numberBetween(1, 6),
                faker.number().numberBetween(1, 700)
        );
    }
}
