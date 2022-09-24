package in.payhere.financialledger.auth.dto;

public record SignOutResponse(Long userId, String accessTokenHeader, String refreshTokenHeader) {
}
