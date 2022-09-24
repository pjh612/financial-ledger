package in.payhere.financialledger.common.security.jwt;

public interface TokenService {
	TokenResponse findByUserId(Long userId);
	String save(Long userId, String token, Long expirySeconds);
	void delete(Long userId);
}
