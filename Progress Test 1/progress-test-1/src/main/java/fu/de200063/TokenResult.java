package fu.de200063;

public class TokenResult {
    private final ResultCode resultCode;
    private final String token;

    public TokenResult(ResultCode resultCode, String token) {
        this.resultCode = resultCode;
        this.token = token;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public String getToken() {
        return token;
    }
}
