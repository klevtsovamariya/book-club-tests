package models.localstorage;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

public record LocalStorageAuthModel(UserData user,
                                    String accessToken,
                                    String refreshToken,
                                    boolean isAuthenticated) {
    private static final JsonMapper JSON = JsonMapper.builder().build();

    public String toJson() {
        try {
            return JSON.writeValueAsString(this);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }
}
