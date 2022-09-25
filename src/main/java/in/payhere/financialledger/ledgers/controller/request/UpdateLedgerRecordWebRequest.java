package in.payhere.financialledger.ledgers.controller.request;

public record UpdateLedgerRecordWebRequest(
	Integer amount,
	String memo
) {
}
