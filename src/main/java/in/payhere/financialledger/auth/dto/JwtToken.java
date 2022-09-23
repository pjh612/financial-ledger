package in.payhere.financialledger.auth.dto;

public record JwtToken(String header, String token, long expirySeconds) {
}
