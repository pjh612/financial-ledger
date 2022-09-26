package in.payhere.financialledger.ledgers.service.dto.request;

import java.time.LocalDateTime;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import in.payhere.financialledger.ledgers.entity.RecordType;

public record CreateLedgerRecordRequest(
	Long ledgerId,
	Long userId,

	@Positive
	int amount,

	@Size(min = 2, max = 300)
	String memo,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime datetime,

	RecordType type
) {
}
