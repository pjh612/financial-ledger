package in.payhere.financialledger.common.security.jwt;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;

import in.payhere.financialledger.common.config.properties.CookieConfigProperties;
import in.payhere.financialledger.common.config.properties.JwtConfigureProperties;
import in.payhere.financialledger.common.security.jwt.exception.JwtTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final JwtConfigureProperties jwtConfigureProperties;
	private final CookieConfigProperties cookieConfigProperties;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			authenticate(getAccessToken(request), request, response);
		} catch (JwtTokenNotFoundException e) {
			log.warn(e.getMessage());
		}
		filterChain.doFilter(request, response);
	}

	private String getAccessToken(HttpServletRequest request) {
		if (request.getCookies() == null) {
			throw new JwtTokenNotFoundException("AccessToken is not found.");
		}
		return Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(jwtConfigureProperties.accessToken().header()))
			.findFirst()
			.map(Cookie::getValue)
			.orElseThrow(() -> new JwtTokenNotFoundException("AccessToken is not found"));
	}

	private void authenticate(String accessToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			JwtProvider.Claims claims = jwtProvider.verify(accessToken);
			JwtAuthenticationToken authentication = createAuthenticationToken(claims, request, accessToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (TokenExpiredException exception) {
			log.warn(exception.getMessage());
			refreshAuthentication(accessToken, request, response);
		} catch (JWTVerificationException exception) {
			log.warn(exception.getMessage());
		}
	}

	private JwtAuthenticationToken createAuthenticationToken(JwtProvider.Claims claims, HttpServletRequest request,
		String accessToken) {
		List<GrantedAuthority> authorities = jwtProvider.getAuthorities(claims);
		JwtAuthentication authentication = new JwtAuthentication(accessToken, claims.getUserId(), claims.getEmail());
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication, null, authorities);
		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		return authenticationToken;
	}

	private void refreshAuthentication(String accessToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			String refreshToken = getRefreshToken(request);
			jwtProvider.verifyRefreshToken(accessToken, refreshToken);
			String reIssuedAccessToken = jwtProvider.generateAccessToken(jwtProvider.decode(accessToken));
			JwtProvider.Claims reIssuedClaims = jwtProvider.verify(reIssuedAccessToken);
			JwtAuthenticationToken authentication = createAuthenticationToken(reIssuedClaims, request,
				reIssuedAccessToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			ResponseCookie cookie = ResponseCookie.from(jwtConfigureProperties.accessToken().header(),
					reIssuedAccessToken)
				.path("/")
				.httpOnly(true)
				.sameSite(cookieConfigProperties.sameSite().attributeValue())
				.domain(cookieConfigProperties.domain())
				.secure(cookieConfigProperties.secure())
				.maxAge(jwtConfigureProperties.refreshToken().expirySeconds())
				.build();
			response.addHeader(SET_COOKIE, cookie.toString());

		} catch (EntityNotFoundException | JwtTokenNotFoundException | JWTVerificationException e) {
			log.warn(e.getMessage());
		}
	}

	private String getRefreshToken(HttpServletRequest request) {
		if (request.getCookies() != null) {
			return Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(jwtConfigureProperties.refreshToken().header()))
				.findFirst()
				.map(Cookie::getValue)
				.orElseThrow(() -> new JwtTokenNotFoundException("RefreshToken is not found."));
		} else {
			throw new JwtTokenNotFoundException("RefreshToken is not found.");
		}
	}
}
