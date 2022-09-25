package in.payhere.financialledger.ledgers.service.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import in.payhere.financialledger.ledgers.entity.RecordType;

public record CreateLedgerRecordRequest(
	Long ledgerId,
	Long userId,

	@Positive
	int amount,

	@Size(min = 2, max = 300)
	String memo,

	LocalDateTime datetime,

	RecordType type
) {
}
