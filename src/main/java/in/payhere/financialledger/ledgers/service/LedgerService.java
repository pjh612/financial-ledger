package in.payhere.financialledger.ledgers.service;

import static java.text.MessageFormat.format;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.payhere.financialledger.common.exception.BusinessException;
import in.payhere.financialledger.common.exception.EntityNotFoundException;
import in.payhere.financialledger.common.exception.ErrorCode;
import in.payhere.financialledger.ledgers.entity.Ledger;
import in.payhere.financialledger.ledgers.entity.LedgerRecord;
import in.payhere.financialledger.ledgers.repository.LedgerRecordRepository;
import in.payhere.financialledger.ledgers.repository.LedgerRepository;
import in.payhere.financialledger.ledgers.repository.dto.RecordSearchCondition;
import in.payhere.financialledger.ledgers.service.dto.LedgerResponse;
import in.payhere.financialledger.ledgers.service.dto.request.CreateLedgerRecordRequest;
import in.payhere.financialledger.ledgers.service.dto.request.UpdateLedgerRecordRequest;
import in.payhere.financialledger.ledgers.service.dto.response.CreateLedgerRecordResponse;
import in.payhere.financialledger.ledgers.service.dto.response.CreateLedgerResponse;
import in.payhere.financialledger.ledgers.service.dto.response.LedgerRecordResponse;
import in.payhere.financialledger.user.converter.UserConverter;
import in.payhere.financialledger.user.entity.User;
import in.payhere.financialledger.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class LedgerService {
	private final LedgerRepository ledgerRepository;
	private final LedgerRecordRepository ledgerRecordRepository;
	private final UserService userService;
	private final UserConverter userConverter;

	public CreateLedgerResponse createLedger(Long userId, String name) {
		User foundUser = userConverter.toUser(userService.findById(userId));

		Ledger ledger = new Ledger(name, foundUser);
		ledgerRepository.save(ledger);

		return new CreateLedgerResponse(ledger.getId());
	}

	public List<LedgerResponse> findAllLedgersByUserId(Long userId) {
		return ledgerRepository.findAllByUserId(userId)
			.stream()
			.map(ledger -> new LedgerResponse(ledger.getId(), ledger.getName()))
			.toList();
	}

	public LedgerRecordResponse findOneRecordByUserIdAndRecordId(Long userId, Long recordId) {
		LedgerRecord foundLedgerRecord = ledgerRecordRepository.findByIdAndUserIdAndIsRemoved(recordId, userId, false)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_LEDGER_RECORD,
				format("User({0})'s records does not contain record ID{1} or has been removed", userId, recordId)));

		return new LedgerRecordResponse(
			foundLedgerRecord.getId(),
			foundLedgerRecord.getAmount(),
			foundLedgerRecord.getMemo(),
			foundLedgerRecord.getDatetime(),
			foundLedgerRecord.getType(),
			foundLedgerRecord.isRemoved()
		);
	}

	public Page<LedgerRecordResponse> findRecordsByPaging(Pageable pageRequest, RecordSearchCondition condition) {
		return ledgerRecordRepository.findByDynamicQuery(pageRequest, condition)
			.map(ledgerRecord -> new LedgerRecordResponse(
				ledgerRecord.getId(),
				ledgerRecord.getAmount(),
				ledgerRecord.getMemo(),
				ledgerRecord.getDatetime(),
				ledgerRecord.getType(),
				ledgerRecord.isRemoved())
			);
	}

	public CreateLedgerRecordResponse record(CreateLedgerRecordRequest request) {
		Ledger foundLedger = ledgerRepository.findById(request.ledgerId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_LEDGER));

		if (!Objects.equals(foundLedger.getUser().getId(), request.userId())) {
			throw new BusinessException(ErrorCode.FORBIDDEN,
				format("Ledger({0}) is not owned by user({1})", foundLedger.getId(), request.userId())
			);
		}

		LedgerRecord newLedgerRecord = new LedgerRecord(
			foundLedger,
			request.amount(),
			request.memo(),
			request.datetime(),
			request.type()
		);

		return new CreateLedgerRecordResponse(ledgerRecordRepository.save(newLedgerRecord).getId());
	}

	public void remove(long userId, Long recordId) {
		LedgerRecord foundLedgerRecord = ledgerRecordRepository.findByIdAndUserIdAndIsRemoved(recordId, userId, false)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_LEDGER_RECORD,
				format("User({0})'s records does not contain record ID{1} or has been removed", userId, recordId)));

		foundLedgerRecord.remove();
	}

	public void restore(long userId, Long recordId) {
		LedgerRecord foundLedgerRecord = ledgerRecordRepository.findByIdAndUserIdAndIsRemoved(recordId, userId, true)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_LEDGER_RECORD,
				format("User({0})'s records does not contain record ID{1} or is not removed ", userId, recordId)));

		foundLedgerRecord.restore();
	}

	public void update(UpdateLedgerRecordRequest request) {
		LedgerRecord foundLedgerRecord = ledgerRecordRepository.findByIdAndUserIdAndIsRemoved(
				request.recordId(),
				request.userId(),
				false)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_LEDGER_RECORD,
				format("User({0})'s records does not contain record ID{1} or has been removed",
					request.userId(),
					request.recordId()))
			);

		foundLedgerRecord.updateAmount(request.amount());
		foundLedgerRecord.updateMemo(request.memo());
	}
}
