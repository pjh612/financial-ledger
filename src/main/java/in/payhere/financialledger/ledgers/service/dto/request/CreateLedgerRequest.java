package in.payhere.financialledger.ledgers.service.dto.request;

import javax.validation.constraints.Size;

public record CreateLedgerRequest(
	@Size(min = 2, max = 50)
	String name) {
}
