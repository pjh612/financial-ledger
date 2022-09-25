package in.payhere.financialledger.ledgers.controller.request;

import java.time.LocalDateTime;

import in.payhere.financialledger.ledgers.entity.RecordType;

public record SearchLedgerRecordWebRequest(
	Boolean isRemoved,
	RecordType type,
	LocalDateTime startAt,
	LocalDateTime endAt
) {
}
