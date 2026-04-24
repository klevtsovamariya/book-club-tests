package tests;

public class TestData {

    public static final String LOGIN_USERNAME = "user8";
    public static final String LOGIN_PASSWORD = "user8";
    public static final String LOGIN_WRONG_PASSWORD = "qaguru1234";

    public static final String LOGIN_TOKEN_PREFIX = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    public static final String LOGIN_WRONG_CREDENTIALS_ERROR = "Invalid username or password.";
    public static final String EMPTY_ERROR = "This field may not be blank.";
    public static final String LOGOUT_INVALID_TOKEN_ERROR = "Token is invalid";
    public static final String NULL_ERROR = "This field may not be null.";
    public static final String INVALID_EMAIL_ERROR = "Enter a valid email address.";
    public static final String UNAUTHORIZED_ERROR = "Authentication credentials were not provided.";

    public static final String REGISTRATION_EXISTING_USER_ERROR =
            "A user with that username already exists.";

    public static final String REGISTRATION_IP_REGEXP =
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
}
