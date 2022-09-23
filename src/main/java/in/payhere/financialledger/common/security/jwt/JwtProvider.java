package in.payhere.financialledger.common.security.jwt;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import in.payhere.financialledger.common.config.properties.JwtConfigureProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtProvider {
	private final Algorithm algorithm;
	private final JWTVerifier jwtVerifier;
	private final JwtConfigureProperties jwtConfigureProperties;
	private final TokenService tokenService;

	public String generateAccessToken(Claims claims) {
		Date now = new Date();
		JWTCreator.Builder builder = JWT.create();

		builder.withSubject(claims.userId.toString());
		builder.withIssuer(jwtConfigureProperties.issuer());
		builder.withIssuedAt(now);

		if (jwtConfigureProperties.accessToken().expirySeconds() > 0) {
			builder.withExpiresAt(new Date(now.getTime() + jwtConfigureProperties.accessToken().expirySeconds() * 1000L));
		}
		builder.withClaim("userId", claims.userId);
		builder.withClaim("email", claims.email);
		builder.withArrayClaim("roles", claims.roles);

		return builder.sign(this.algorithm);
	}

	public String generateRefreshToken(Long userId) {
		Date now = new Date();
		JWTCreator.Builder builder = JWT.create();
		builder.withIssuer(this.jwtConfigureProperties.issuer());
		builder.withIssuedAt(now);
		if (this.jwtConfigureProperties.refreshToken().expirySeconds() > 0) {
			builder.withExpiresAt(new Date(now.getTime() + jwtConfigureProperties.refreshToken().expirySeconds() * 1000L));
		}

		String refreshToken = builder.sign(this.algorithm);
		tokenService.save(userId, refreshToken, jwtConfigureProperties.refreshToken().expirySeconds());

		return refreshToken;
	}

	public void verifyRefreshToken(String accessToken, String refreshToken) {
		verify(refreshToken);
		Long userId = decode(accessToken).getUserId();
		TokenResponse token = tokenService.findByUserId(userId);

		if (!refreshToken.equals(token.token())) {
			throw new JWTVerificationException("Invalid refresh token.");
		}
	}

	public List<GrantedAuthority> getAuthorities(Claims claims) {
		String[] roles = claims.roles;

		return roles == null || roles.length == 0
			? Collections.emptyList()
			: Arrays.stream(roles)
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}

	public Claims decode(String token) {
		return new Claims(JWT.decode(token));

	}

	public Claims verify(String token) {
		return new Claims(this.jwtVerifier.verify(token));
	}

	@Getter
	public static class Claims {
		private Long userId;
		private String email;
		private String[] roles;
		private Date iat;
		private Date exp;

		private Claims() {
		}

		Claims(DecodedJWT decodedJWT) {
			Claim userId = decodedJWT.getClaim("userId");
			if (!userId.isNull()) {
				this.userId = userId.asLong();
			}

			Claim email = decodedJWT.getClaim("email");
			if (!email.isNull()) {
				this.email = email.asString();
			}

			Claim roles = decodedJWT.getClaim("roles");
			if (!roles.isNull()) {
				this.roles = roles.asArray(String.class);
			}
			this.iat = decodedJWT.getIssuedAt();
			this.exp = decodedJWT.getExpiresAt();
		}

		@Builder
		Claims(Long userId, String email, String[] roles, Date iat, Date exp) {
			this.userId = userId;
			this.email = email;
			this.roles = roles;
			this.iat = iat;
			this.exp = exp;
		}
	}
}
