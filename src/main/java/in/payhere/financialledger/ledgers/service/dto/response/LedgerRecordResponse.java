package in.payhere.financialledger.ledgers.service.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import in.payhere.financialledger.ledgers.entity.RecordType;

public record LedgerRecordResponse(
	Long recordId,
	int amount,
	String memo,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime datetime,
	RecordType type,
	boolean isRemoved
) {
}
