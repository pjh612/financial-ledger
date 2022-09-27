package in.payhere.financialledger.ledgers.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.payhere.financialledger.common.ApiResponse;
import in.payhere.financialledger.common.config.SecurityConfig;
import in.payhere.financialledger.common.config.properties.JwtConfigureProperties;
import in.payhere.financialledger.common.exception.ErrorCode;
import in.payhere.financialledger.common.exception.ErrorModel;
import in.payhere.financialledger.common.exception.ErrorResponse;
import in.payhere.financialledger.common.security.jwt.JwtProvider;
import in.payhere.financialledger.common.security.jwt.TokenService;
import in.payhere.financialledger.ledgers.controller.request.CreateLedgerRecordWebRequest;
import in.payhere.financialledger.ledgers.controller.request.CreateLedgerWebRequest;
import in.payhere.financialledger.ledgers.entity.RecordType;
import in.payhere.financialledger.ledgers.service.LedgerService;
import in.payhere.financialledger.ledgers.service.dto.response.CreateLedgerResponse;
import in.payhere.financialledger.security.WithMockJwtAuthentication;

@WebMvcTest({LedgerController.class, SecurityConfig.class, JwtConfigureProperties.class, JwtProvider.class,
	TokenService.class})
class LedgerControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	LedgerService ledgerService;

	@Autowired
	JwtProvider jwtProvider;

	@MockBean
	TokenService tokenService;

	@Autowired
	JwtConfigureProperties jwtConfigureProperties;

	@Test
	@DisplayName("가계부 생성 성공")
	@WithMockJwtAuthentication
	void createLedgerSuccess() throws Exception {

		CreateLedgerWebRequest createLedgerRequest = new CreateLedgerWebRequest("testLedger");
		CreateLedgerResponse createLedgerResponse = new CreateLedgerResponse(1L);

		given(ledgerService.createLedger(1L, createLedgerRequest.name())).willReturn(createLedgerResponse);

		String request = objectMapper.writeValueAsString(createLedgerRequest);
		String response = objectMapper.writeValueAsString(new ApiResponse<>(createLedgerResponse));

		//when
		ResultActions perform = mockMvc.perform(post("/api/ledgers")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("가계부 이름을 입력해야 만들 수 있다.")
	@WithMockJwtAuthentication
	void createTooShortNameLedgerFail() throws Exception {

		CreateLedgerWebRequest createLedgerRequest = new CreateLedgerWebRequest("");

		String request = objectMapper.writeValueAsString(createLedgerRequest);
		ErrorResponse<ErrorModel> response = new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID);

		//when
		ResultActions perform = mockMvc.perform(post("/api/ledgers")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is(response.getCode())))
			.andExpect(jsonPath("$.message", is(response.getMessage())));
	}

	@Test
	@DisplayName("가계부 이름이 공백이면 안된다.")
	@WithMockJwtAuthentication
	void createBlankNameLedgerFail() throws Exception {
		CreateLedgerWebRequest createLedgerRequest = new CreateLedgerWebRequest("         ");

		String request = objectMapper.writeValueAsString(createLedgerRequest);
		ErrorResponse<ErrorModel> response = new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID);

		//when
		ResultActions perform = mockMvc.perform(post("/api/ledgers")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is(response.getCode())))
			.andExpect(jsonPath("$.message", is(response.getMessage())));
	}

	@Test
	@DisplayName("가계부 내역의 금액은 음수면 안된다.")
	@WithMockJwtAuthentication
	void recordNegativeAmountFail() throws Exception {
		CreateLedgerRecordWebRequest createLedgerRequest = new CreateLedgerRecordWebRequest(
			-2000,
			"memo",
			LocalDateTime.now(),
			RecordType.EXPENSE);

		String request = objectMapper.writeValueAsString(createLedgerRequest);
		ErrorResponse<ErrorModel> response = new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID);

		//when
		ResultActions perform = mockMvc.perform(post("/api/ledgers/{ledgerId}/records", 1L)
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		perform.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code", is(response.getCode())))
			.andExpect(jsonPath("$.message", is(response.getMessage())));
	}
}