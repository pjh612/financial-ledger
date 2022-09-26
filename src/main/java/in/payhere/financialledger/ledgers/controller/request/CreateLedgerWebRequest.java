package in.payhere.financialledger.ledgers.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record CreateLedgerWebRequest(
	@NotBlank
	@Size(min = 2, max = 50)
	String name
) {
}
