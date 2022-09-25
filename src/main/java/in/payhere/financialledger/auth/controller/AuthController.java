package in.payhere.financialledger.auth.controller;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.payhere.financialledger.auth.service.dto.response.JwtToken;
import in.payhere.financialledger.auth.controller.request.SignInWebRequest;
import in.payhere.financialledger.auth.service.dto.response.SignInResponse;
import in.payhere.financialledger.auth.service.dto.response.SignOutResponse;
import in.payhere.financialledger.auth.service.AuthService;
import in.payhere.financialledger.common.ApiResponse;
import in.payhere.financialledger.common.config.properties.CookieConfigProperties;
import in.payhere.financialledger.common.security.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Slf4j
public class AuthController {
	private final AuthService authService;
	private final CookieConfigProperties cookieConfigProperties;

	@PostMapping("/signin")
	public ApiResponse<SignInResponse> signIn(@RequestBody @Valid SignInWebRequest signInRequest,
		HttpServletRequest request, HttpServletResponse response) {
		SignInResponse signInResponse = this.authService.signIn(
			signInRequest.email(),
			signInRequest.password()
		);
		log.error(signInRequest.email());
		log.error(signInRequest.password());
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

	@DeleteMapping("/signout")
	public ApiResponse<String> signOut(@AuthenticationPrincipal JwtAuthentication auth, HttpServletResponse response) {
		SignOutResponse signOutResponse = authService.signOut(auth.id());
		ResponseCookie accessTokenCookie = ResponseCookie.from(signOutResponse.accessTokenHeader(), "")
			.path("/")
			.maxAge(0)
			.httpOnly(true)
			.secure(cookieConfigProperties.secure())
			.domain(cookieConfigProperties.domain())
			.build();
		ResponseCookie refreshTokenCookie = ResponseCookie.from(signOutResponse.refreshTokenHeader(), "")
			.path("/")
			.maxAge(0)
			.httpOnly(true)
			.secure(cookieConfigProperties.secure())
			.domain(cookieConfigProperties.domain())
			.build();
		response.setHeader(SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(SET_COOKIE, refreshTokenCookie.toString());

		return new ApiResponse<>("signed out");
	}
}
