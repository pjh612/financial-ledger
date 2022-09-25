package in.payhere.financialledger.ledgers.repository.dto;

import java.time.LocalDateTime;

import in.payhere.financialledger.ledgers.entity.RecordType;
import lombok.Builder;

@Builder
public record RecordSearchCondition(
	Long userId,
	Long ledgerId,
	Boolean isRemoved,
	RecordType type,
	LocalDateTime startAt,
	LocalDateTime endAt
) {
}
