package in.payhere.financialledger.ledgers.service.dto.response;

import java.time.LocalDateTime;

import in.payhere.financialledger.ledgers.entity.RecordType;

public record LedgerRecordResponse(
	Long recordId,
	int amount,
	String memo,
	LocalDateTime datetime,
	RecordType type,
	boolean isRemoved
) {
}
