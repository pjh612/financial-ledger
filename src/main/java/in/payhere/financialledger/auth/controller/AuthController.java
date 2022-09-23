package in.payhere.financialledger.auth.controller;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.payhere.financialledger.auth.dto.JwtToken;
import in.payhere.financialledger.auth.dto.SignInRequest;
import in.payhere.financialledger.auth.dto.SignInResponse;
import in.payhere.financialledger.auth.service.AuthService;
import in.payhere.financialledger.common.ApiResponse;
import in.payhere.financialledger.common.config.properties.CookieConfigProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class AuthController {
	private final AuthService authService;
	private final CookieConfigProperties cookieConfigProperties;

	@PostMapping("/signin")
	public ApiResponse<SignInResponse> signIn(@RequestBody @Valid SignInRequest signInRequest,
		HttpServletRequest request, HttpServletResponse response) {
		SignInResponse signInResponse = this.authService.signIn(
			signInRequest.email(),
			signInRequest.password()
		);

		signInResponse.jwtAuthenticationToken()
			.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(signInResponse.jwtAuthenticationToken());

		JwtToken accessToken = signInResponse.accessToken();
		JwtToken refreshToken = signInResponse.refreshToken();
		ResponseCookie accessTokenCookie = createCookie(accessToken.header(), accessToken.token(),
			(int)refreshToken.expirySeconds());
		ResponseCookie refreshTokenCookie = createCookie(refreshToken.header(), refreshToken.token(),
			(int)refreshToken.expirySeconds());
		response.setHeader(SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(SET_COOKIE, refreshTokenCookie.toString());

		return new ApiResponse<>(signInResponse);
	}

	private ResponseCookie createCookie(String header, String token, int expirySecond) {
		return ResponseCookie.from(header, token)
			.path("/")
			.httpOnly(true)
			.secure(cookieConfigProperties.secure())
			.domain(cookieConfigProperties.domain())
			.maxAge(expirySecond)
			.sameSite(cookieConfigProperties.sameSite().attributeValue())
			.build();
	}
}
