package in.payhere.financialledger.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("security.jwt")
public record JwtConfigureProperties(
	TokenProperties accessToken,
	TokenProperties refreshToken,
	String issuer,
	String clientSecret
) {
	public record TokenProperties(String header, long expirySeconds) {
	}
}
