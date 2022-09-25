package in.payhere.financialledger.auth.service.dto.response;

public record JwtToken(String header, String token, long expirySeconds) {
}
