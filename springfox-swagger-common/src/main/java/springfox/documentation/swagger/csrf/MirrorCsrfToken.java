package springfox.documentation.swagger.csrf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A mirror class of spring-security's `CsrfToken / server CsrfToken`
 *
 * @author liuxy
 */
public class MirrorCsrfToken {

    private final String token;

    private final String parameterName;

    private final String headerName;

    MirrorCsrfToken(String headerName, String parameterName, String token) {
        this.headerName = headerName;
        this.parameterName = parameterName;
        this.token = token;
    }

    @JsonProperty("headerName")
    public String getHeaderName() {
        return this.headerName;
    }

    @JsonProperty("parameterName")
    public String getParameterName() {
        return this.parameterName;
    }

    @JsonProperty("token")
    public String getToken() {
        return this.token;
    }
}
