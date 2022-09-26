package in.payhere.financialledger.ledgers.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.payhere.financialledger.common.ApiResponse;
import in.payhere.financialledger.common.security.jwt.JwtAuthentication;
import in.payhere.financialledger.ledgers.controller.request.CreateLedgerRecordWebRequest;
import in.payhere.financialledger.ledgers.controller.request.CreateLedgerWebRequest;
import in.payhere.financialledger.ledgers.controller.request.SearchLedgerRecordWebRequest;
import in.payhere.financialledger.ledgers.controller.request.UpdateLedgerRecordWebRequest;
import in.payhere.financialledger.ledgers.repository.dto.RecordSearchCondition;
import in.payhere.financialledger.ledgers.service.LedgerService;
import in.payhere.financialledger.ledgers.service.dto.LedgerResponse;
import in.payhere.financialledger.ledgers.service.dto.request.CreateLedgerRecordRequest;
import in.payhere.financialledger.ledgers.service.dto.request.UpdateLedgerRecordRequest;
import in.payhere.financialledger.ledgers.service.dto.response.CreateLedgerRecordResponse;
import in.payhere.financialledger.ledgers.service.dto.response.CreateLedgerResponse;
import in.payhere.financialledger.ledgers.service.dto.response.LedgerRecordResponse;
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

	@PostMapping("/{ledgerId}/records")
	public ApiResponse<CreateLedgerRecordResponse> record(
		@AuthenticationPrincipal JwtAuthentication auth,
		@PathVariable Long ledgerId,
		@RequestBody @Valid CreateLedgerRecordWebRequest request) {
		CreateLedgerRecordRequest serviceRequest = new CreateLedgerRecordRequest(
			ledgerId,
			auth.id(),
			request.amount(),
			request.memo(),
			request.dateTime(),
			request.type()
		);

		return new ApiResponse<>(ledgerService.record(serviceRequest));
	}

	@GetMapping("/{ledgerId}/records")
	public ApiResponse<Page<LedgerRecordResponse>> getRecords(
		@AuthenticationPrincipal JwtAuthentication auth,
		@PathVariable Long ledgerId,
		SearchLedgerRecordWebRequest condition,
		Pageable pageable) {
		RecordSearchCondition serviceCondition = new RecordSearchCondition(
			auth.id(),
			ledgerId,
			condition.isRemoved(),
			condition.type(),
			condition.startAt(),
			condition.endAt()
		);
		return new ApiResponse<>(ledgerService.findRecordsByPaging(pageable, serviceCondition));
	}

	@GetMapping("/records/{recordId}")
	public ApiResponse<LedgerRecordResponse> getRecord(
		@AuthenticationPrincipal JwtAuthentication auth,
		@PathVariable Long recordId) {
		return new ApiResponse<>(ledgerService.findOneRecordByUserIdAndRecordId(auth.id(), recordId));
	}

	@PatchMapping("/records/{recordId}")
	public ApiResponse<String> updateRecord(
		@AuthenticationPrincipal JwtAuthentication auth,
		@PathVariable Long recordId,
		@RequestBody @Valid UpdateLedgerRecordWebRequest request) {
		UpdateLedgerRecordRequest serviceRequest = new UpdateLedgerRecordRequest(
			recordId,
			auth.id(),
			request.amount(),
			request.memo()
		);
		ledgerService.update(serviceRequest);

		return new ApiResponse<>("updated");
	}

	@DeleteMapping("/records/{recordId}")
	public ApiResponse<String> remove(@AuthenticationPrincipal JwtAuthentication auth, @PathVariable Long recordId) {
		ledgerService.remove(auth.id(), recordId);

		return new ApiResponse<>("removed");
	}

	@PostMapping("/records/{recordId}")
	public ApiResponse<String> restore(@AuthenticationPrincipal JwtAuthentication auth, @PathVariable Long recordId) {
		ledgerService.restore(auth.id(), recordId);

		return new ApiResponse<>("restored");
	}
}
