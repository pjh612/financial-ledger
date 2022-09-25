package in.payhere.financialledger.ledgers.service.dto.request;

public record UpdateLedgerRecordRequest(
	Long recordId,
	Long userId,
	Integer amount,
	String memo
) {
}
