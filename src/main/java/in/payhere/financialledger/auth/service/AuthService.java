package in.payhere.financialledger.auth.service;

import static java.text.MessageFormat.format;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.payhere.financialledger.auth.service.dto.response.JwtToken;
import in.payhere.financialledger.auth.service.dto.response.SignInResponse;
import in.payhere.financialledger.auth.service.dto.response.SignOutResponse;
import in.payhere.financialledger.common.config.properties.JwtConfigureProperties;
import in.payhere.financialledger.common.exception.BusinessException;
import in.payhere.financialledger.common.exception.EntityNotFoundException;
import in.payhere.financialledger.common.exception.ErrorCode;
import in.payhere.financialledger.common.security.Role;
import in.payhere.financialledger.common.security.jwt.JwtAuthentication;
import in.payhere.financialledger.common.security.jwt.JwtAuthenticationToken;
import in.payhere.financialledger.common.security.jwt.JwtProvider;
import in.payhere.financialledger.user.service.dto.response.UserResponse;
import in.payhere.financialledger.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
	private final UserService userService;
	private final JwtProvider jwtProvider;
	private final JwtConfigureProperties jwtConfigureProperties;
	private final PasswordEncoder passwordEncoder;

	public SignInResponse signIn(String email, String password) {
		UserResponse foundUserResponse;
		try {
			foundUserResponse = userService.findByEmail(email);
		} catch (EntityNotFoundException e) {
			throw new BusinessException(ErrorCode.AUTHENTICATION_FAIL, format("email : {0} not found", email));
		}

		if (!passwordEncoder.matches(password, foundUserResponse.password())) {
			throw new BusinessException(ErrorCode.AUTHENTICATION_FAIL, format("password : {0} not matched", password));
		}
		JwtProvider.Claims claims = JwtProvider.Claims.builder()
			.userId(foundUserResponse.id())
			.email(email)
			.roles(new String[] {Role.USER.getKey()})
			.build();

		String accessToken = jwtProvider.generateAccessToken(claims);
		String refreshToken = jwtProvider.generateRefreshToken(foundUserResponse.id());

		JwtAuthentication authentication = new JwtAuthentication(accessToken, foundUserResponse.id(),
			foundUserResponse.email());
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication, null,
			jwtProvider.getAuthorities(jwtProvider.verify(accessToken)));

		return new SignInResponse(
			foundUserResponse.id(),
			foundUserResponse.email(),
			Role.USER,
			new JwtToken(jwtConfigureProperties.accessToken().header(), accessToken,
				jwtConfigureProperties.refreshToken().expirySeconds()),
			new JwtToken(jwtConfigureProperties.refreshToken().header(), refreshToken,
				jwtConfigureProperties.refreshToken().expirySeconds()),
			authenticationToken
		);
	}

	public SignOutResponse signOut(Long userId) {
		jwtProvider.deactivateRefreshToken(userId);

		return new SignOutResponse(userId, jwtConfigureProperties.accessToken().header(), jwtConfigureProperties.refreshToken().header());
	}
}
