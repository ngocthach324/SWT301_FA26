package lab2.account;

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
