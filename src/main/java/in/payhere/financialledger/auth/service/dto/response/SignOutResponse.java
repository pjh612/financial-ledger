package in.payhere.financialledger.auth.service.dto.response;

public record SignOutResponse(Long userId, String accessTokenHeader, String refreshTokenHeader) {
}
