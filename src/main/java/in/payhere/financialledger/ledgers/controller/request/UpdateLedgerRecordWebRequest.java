package in.payhere.financialledger.ledgers.controller.request;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public record UpdateLedgerRecordWebRequest(
	@Positive
	Integer amount,

	@Size(min = 2, max = 300)
	String memo
) {
}
