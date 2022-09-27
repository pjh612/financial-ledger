package in.payhere.financialledger.auth.controller;

import static java.text.MessageFormat.format;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.payhere.financialledger.auth.controller.request.SignInWebRequest;
import in.payhere.financialledger.auth.service.AuthService;
import in.payhere.financialledger.auth.service.dto.response.JwtToken;
import in.payhere.financialledger.auth.service.dto.response.SignInResponse;
import in.payhere.financialledger.auth.service.dto.response.SignOutResponse;
import in.payhere.financialledger.common.ApiResponse;
import in.payhere.financialledger.common.config.SecurityConfig;
import in.payhere.financialledger.common.config.properties.JwtConfigureProperties;
import in.payhere.financialledger.common.exception.BusinessException;
import in.payhere.financialledger.common.exception.ErrorCode;
import in.payhere.financialledger.common.exception.ErrorModel;
import in.payhere.financialledger.common.exception.ErrorResponse;
import in.payhere.financialledger.common.security.Role;
import in.payhere.financialledger.common.security.jwt.JwtAuthentication;
import in.payhere.financialledger.common.security.jwt.JwtAuthenticationToken;
import in.payhere.financialledger.common.security.jwt.JwtProvider;
import in.payhere.financialledger.common.security.jwt.TokenService;
import in.payhere.financialledger.security.WithMockJwtAuthentication;

@WebMvcTest({AuthController.class, SecurityConfig.class, JwtConfigureProperties.class, JwtProvider.class,
	TokenService.class})
class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	AuthService authService;

	@Autowired
	JwtProvider jwtProvider;

	@MockBean
	TokenService tokenService;

	@Autowired
	JwtConfigureProperties jwtConfigureProperties;

	@Test
	@DisplayName("로그인 성공")
	void signUpSuccess() throws Exception {
		//given
		String email = "test1234@gmail.com";
		String password = "test12345";
		JwtToken accessToken = new JwtToken("at", "accessToken", 30L);
		JwtToken refreshToken = new JwtToken("rt", "refreshToken", 60L);

		SimpleGrantedAuthority roleUser = new SimpleGrantedAuthority(Role.USER.getKey());
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(roleUser);

		JwtAuthentication jwtAuthentication = new JwtAuthentication(accessToken.token(), 1L, email);
		JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwtAuthentication, null,
			authorities);

		SignInWebRequest signInRequest = new SignInWebRequest(email, password);
		SignInResponse signInResponse = new SignInResponse(1L, email, Role.USER, accessToken, refreshToken,
			jwtAuthenticationToken);
		String request = objectMapper.writeValueAsString(signInRequest);
		String response = objectMapper.writeValueAsString(new ApiResponse<>(signInResponse));

		given(authService.signIn(signInRequest.email(), signInRequest.password())).willReturn(signInResponse);

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signin")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(authService, times(1)).signIn(signInRequest.email(), signInRequest.password());

		perform
			.andExpect(status().isOk())
			.andExpect(content().string(response))
			.andExpect(cookie().exists(accessToken.header()))
			.andExpect(cookie().maxAge(accessToken.header(), (int)refreshToken.expirySeconds()))
			.andExpect(cookie().exists(refreshToken.header()))
			.andExpect(cookie().maxAge(refreshToken.header(), (int)refreshToken.expirySeconds()));
	}

	@Test
	@DisplayName("존재하지 않는 이메일로 로그인 시 실패한다 (400 status, errorCode = A001)")
	void signInFailWithNotExistsEmail() throws Exception {
		//given
		String email = "test1234@gmail.com";
		String password = "test12345";

		SignInWebRequest signInRequest = new SignInWebRequest(email, password);

		ErrorModel errorCode = ErrorCode.AUTHENTICATION_FAIL;
		String request = objectMapper.writeValueAsString(signInRequest);
		ErrorResponse<ErrorModel> response = new ErrorResponse<>(errorCode);

		given(authService.signIn(signInRequest.email(), signInRequest.password())).willThrow(
			new BusinessException(errorCode, format("email : {0} not found", email)));

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signin")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(authService, times(1)).signIn(signInRequest.email(), signInRequest.password());

		perform
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is(response.getCode())))
			.andExpect(jsonPath("$.message", is(response.getMessage())));
	}

	@Test
	@DisplayName("패스워드 불일치 시 로그인에 실패한다 (400 status, errorCode = A001)")
	void signInFailWithNotMatchedPassword() throws Exception {
		//given
		String email = "test1234@gmail.com";
		String password = "test12345";

		SignInWebRequest signInRequest = new SignInWebRequest(email, password);

		ErrorModel errorCode = ErrorCode.AUTHENTICATION_FAIL;
		String request = objectMapper.writeValueAsString(signInRequest);
		ErrorResponse<ErrorModel> response = new ErrorResponse<>(errorCode);

		given(authService.signIn(signInRequest.email(), signInRequest.password())).willThrow(
			new BusinessException(errorCode, format("passowrd : {0} not matched", password)));

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signin")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(authService, times(1)).signIn(signInRequest.email(), signInRequest.password());

		perform
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is(response.getCode())))
			.andExpect(jsonPath("$.message", is(response.getMessage())));
	}

	@Test
	@DisplayName("이메일 형식이 아닐 경우 로그인에 실패한다 (400 status, errorCode = V001)")
	void signInFailWithNotEmailForm() throws Exception {
		//given
		String email = "test1234";
		String password = "test12345";

		SignInWebRequest signInRequest = new SignInWebRequest(email, password);

		String request = objectMapper.writeValueAsString(signInRequest);
		ErrorResponse<ErrorModel> response = new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID);

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signin")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is(response.getCode())))
			.andExpect(jsonPath("$.message", is(response.getMessage())));
	}

	@Test
	@DisplayName("로그아웃 성공")
	@WithMockJwtAuthentication
	void signOutSuccess() throws Exception {
		//given
		//setTestAuthentication();
		JwtToken accessToken = new JwtToken("at", "accessToken", 30L);
		JwtToken refreshToken = new JwtToken("rt", "refreshToken", 60L);

		Cookie accessTokenCookie = new Cookie(jwtConfigureProperties.accessToken().header(), "accessToken");
		Cookie refreshTokenCookie = new Cookie(jwtConfigureProperties.refreshToken().header(), "refreshToken");

		SignOutResponse signOutResponse = new SignOutResponse(
			1L,
			jwtConfigureProperties.accessToken().header(),
			jwtConfigureProperties.refreshToken().header()
		);

		String response = objectMapper.writeValueAsString(new ApiResponse<>("signed out"));

		given(authService.signOut(1L)).willReturn(signOutResponse);

		//when
		ResultActions perform = mockMvc.perform(delete("/api/users/signout")
			.cookie(accessTokenCookie, refreshTokenCookie)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(authService, times(1)).signOut(1L);

		perform
			.andExpect(status().isOk())
			.andExpect(content().string(response))
			.andExpect(cookie().exists(accessToken.header()))
			.andExpect(cookie().exists(refreshToken.header()))
			.andExpect(cookie().maxAge(accessToken.header(), 0))
			.andExpect(cookie().maxAge(refreshToken.header(), 0));
	}

	private void setTestAuthentication() {
		JwtProvider.Claims claims = JwtProvider.Claims.builder().userId(1L)
			.email("test1234@gmail.com")
			.roles(new String[] {Role.USER.getKey()})
			.build();

		String token = jwtProvider.generateAccessToken(claims);
		JwtAuthenticationToken authentication = new JwtAuthenticationToken(

			new JwtAuthentication(token, claims.getUserId(), claims.getEmail()),
			null,
			createAuthorityList(Role.USER.getKey())
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
