package in.payhere.financialledger.ledgers.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.payhere.financialledger.ledgers.entity.Ledger;
import in.payhere.financialledger.ledgers.repository.LedgerRepository;
import in.payhere.financialledger.ledgers.service.dto.LedgerResponse;
import in.payhere.financialledger.ledgers.service.dto.response.CreateLedgerResponse;
import in.payhere.financialledger.user.converter.UserConverter;
import in.payhere.financialledger.user.entity.User;
import in.payhere.financialledger.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class LedgerService {
	private final LedgerRepository ledgerRepository;
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

}
