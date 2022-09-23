package in.payhere.financialledger.user.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.payhere.financialledger.common.ApiResponse;
import in.payhere.financialledger.common.config.SecurityConfig;
import in.payhere.financialledger.common.exception.ErrorCode;
import in.payhere.financialledger.common.exception.ErrorResponse;
import in.payhere.financialledger.user.dto.request.SignUpRequest;
import in.payhere.financialledger.user.dto.response.SignUpResponse;
import in.payhere.financialledger.user.service.UserService;

@WebMvcTest({UserController.class, SecurityConfig.class})
public class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	UserService userService;

	@Test
	@DisplayName("회원 가입 성공")
	void signUpSuccess() throws Exception {
		//given
		String email = "test1234@gmail.com";
		String password = "test12345";

		SignUpRequest signUpRequest = new SignUpRequest(email, password);
		SignUpResponse signUpResponse = new SignUpResponse(1L);

		String request = objectMapper.writeValueAsString(signUpRequest);
		String response = objectMapper.writeValueAsString(new ApiResponse<>(signUpResponse));

		given(userService.signUp(signUpRequest.email(), signUpRequest.password())).willReturn(signUpResponse);

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signup")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(userService, times(1)).signUp(signUpRequest.email(), signUpRequest.password());

		perform
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("이메일 형식이 아닐 경우 회원가입에 실패한다.")
	void signUpFailWithNotEmailForm() throws Exception {
		//given
		String email = "test1234";
		String password = "test12345";

		SignUpRequest signUpRequest = new SignUpRequest(email, password);

		String request = objectMapper.writeValueAsString(signUpRequest);
		ErrorResponse<ErrorCode> response = new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID);

		//when
		ResultActions perform = mockMvc.perform(post("/api/users/signup")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is(response.getCode())))
			.andExpect(jsonPath("$.message", is(response.getMessage())));
	}
}
