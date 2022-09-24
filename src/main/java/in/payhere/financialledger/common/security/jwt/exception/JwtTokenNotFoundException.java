package in.payhere.financialledger.common.security.jwt.exception;

public class JwtTokenNotFoundException extends RuntimeException {
	public JwtTokenNotFoundException(String message) {
		super(message);
	}
}
