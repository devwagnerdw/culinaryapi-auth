package CulinaryAPI_app.dtos;

import io.micrometer.common.lang.NonNull;

public class JwtDto {

    @NonNull
    private String token;
    private String type = "Bearer";

    public JwtDto(@NonNull String token) {
        this.token = token;
    }

    public JwtDto(@NonNull String token, String type) {
        this.token = token;
        this.type = type;
    }

    @NonNull
    public String getToken() {
        return token;
    }

    public void setToken(@NonNull String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}