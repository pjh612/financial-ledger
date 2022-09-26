package in.payhere.financialledger.ledgers.controller.request;

import java.time.LocalDateTime;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import in.payhere.financialledger.ledgers.entity.RecordType;

public record CreateLedgerRecordWebRequest(
	@Positive
	int amount,

	@Size(min = 2, max = 300)
	String memo,

	LocalDateTime dateTime,

	RecordType type
) {
}
