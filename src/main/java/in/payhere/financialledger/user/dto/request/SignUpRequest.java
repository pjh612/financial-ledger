package in.payhere.financialledger.user.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

public record SignUpRequest(
	@Email
	String email,

	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
	String password) {
}
