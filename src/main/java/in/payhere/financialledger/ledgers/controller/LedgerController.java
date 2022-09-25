package in.payhere.financialledger.ledgers.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.payhere.financialledger.common.ApiResponse;
import in.payhere.financialledger.common.security.jwt.JwtAuthentication;
import in.payhere.financialledger.ledgers.controller.request.CreateLedgerWebRequest;
import in.payhere.financialledger.ledgers.service.LedgerService;
import in.payhere.financialledger.ledgers.service.dto.LedgerResponse;
import in.payhere.financialledger.ledgers.service.dto.response.CreateLedgerResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/ledgers")
@RestController
public class LedgerController {

	private final LedgerService ledgerService;

	@PostMapping
	public ApiResponse<CreateLedgerResponse> create(
		@AuthenticationPrincipal JwtAuthentication auth,
		@RequestBody @Valid CreateLedgerWebRequest request) {
		return new ApiResponse<>(ledgerService.createLedger(auth.id(), request.name()));
	}

	@GetMapping
	public ApiResponse<List<LedgerResponse>> getLedgers(@AuthenticationPrincipal JwtAuthentication auth) {
		return new ApiResponse<>(ledgerService.findAllLedgersByUserId(auth.id()));
	}

	@PostMapping("/records")
	public ApiResponse<CreateLedgerRecordResponse> record(
		@AuthenticationPrincipal JwtAuthentication auth,
		@RequestBody @Valid CreateLedgerRecordWebRequest request) {
		CreateLedgerRecordRequest serviceRequest = new CreateLedgerRecordRequest(
			request.ledgerId(),
			auth.id(),
			request.amount(),
			request.memo(),
			request.dateTime(),
			request.type()
		);

		return new ApiResponse<>(ledgerService.record(serviceRequest));
	}
}
