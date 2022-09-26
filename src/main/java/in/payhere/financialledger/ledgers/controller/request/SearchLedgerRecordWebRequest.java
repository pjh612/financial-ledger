package in.payhere.financialledger.ledgers.controller.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import in.payhere.financialledger.ledgers.entity.RecordType;

public record SearchLedgerRecordWebRequest(
	Boolean isRemoved,
	RecordType type,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime startAt,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime endAt
) {
}
